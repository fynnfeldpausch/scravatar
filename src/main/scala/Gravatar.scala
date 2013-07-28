package scravatar

import dispatch._, Defaults._
import java.net.{URI, URL, URLEncoder}
import java.security.MessageDigest

/**
 * Immutable (thread safe) class used to generate Gravatar URLs.
 * @author Morten Andersen-Gott - code@andersen-gott.com
 */
case class Gravatar(private val emailAddress: String, ssl: Boolean, forceDefault: Boolean, defaultImage: Option[DefaultImage], rating: Option[Rating], size: Option[Int]) {
  if(!size.forall(s => s > 0 && s <= 2048))
    throw new IllegalArgumentException("Size must be positive and cannot exceed 2048")

  def ssl(ssl: Boolean): Gravatar = copy(ssl = ssl)

  def default(default: DefaultImage, force: Boolean = false): Gravatar = copy(defaultImage = Some(default), forceDefault = force)
  
  def maxRatedAs(rating: Rating): Gravatar = copy(rating = Some(rating))
  
  def size(size: Int): Gravatar = copy(size = Some(size))

  lazy val email: String =
    emailAddress.trim.toLowerCase

  lazy val hash: String =
    Gravatar.hash(email)

  lazy val url: String = 
    initUriBuilder.segments("avatar", hash)
      .queryParam("d", defaultImage.map(_.value))
      .queryParam("r", rating.map(_.value))
      .queryParam("s", size.map(_.toString))
      .build.toString

  lazy val image: Array[Byte] = {
    val is = new URL(url).openStream
    Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray
  }

  lazy val profile: Future[Profile] = {
    val req = dispatch.url(initUriBuilder.segments(hash + ".xml").build.toString)
    val res = Http.configure(_ setFollowRedirects true)(req OK as.xml.Elem)
    res.map { xml =>
      (xml \ "entry").map { entry =>
        val id = (entry \ "id").text.toLong
        val hash = (entry \ "hash").text
        val url = (entry \ "profileUrl").text
        val givenName = (entry \ "name" \ "givenName").headOption.map(_.text)
        val familyName = (entry \ "name" \ "familyName").headOption.map(_.text)
        val displayName = (entry \ "displayName").headOption.map(_.text)
        val about = (entry \ "aboutMe").headOption.map(_.text)
        val location = (entry \ "currentLocation").headOption.map(_.text)
        val ims = (entry \ "ims").map { im =>
          val key = (im \ "type").text
          val value = (im \ "value").text
          key -> value
        }.toMap
        val urls = (entry \ "urls").map { im =>
          val key = (im \ "title").text
          val value = (im \ "value").text
          key -> value
        }.toMap
        Profile(id, hash, url, givenName, familyName, displayName, about, location, ims, urls)
      }.head
    }
  } 

  private def initUriBuilder: URIBuilder = {
    val gravatarBase = "www.gravatar.com"
    val gravatarSslBase = "secure.gravatar.com"
    val urlBuilder =
      if(ssl) URIBuilder.empty.withHost(gravatarSslBase).withScheme("https")
      else URIBuilder.empty.withHost(gravatarBase).withScheme("http")
    if(forceDefault)
      urlBuilder.queryParam("f", "y")
    else urlBuilder
  }
}

object Gravatar{
  def apply(email: String): Gravatar = Gravatar(email, false, false, None, None, None)

  protected def hash(input: String): String = {
    val md5 = MessageDigest.getInstance("MD5").digest(input.getBytes)
    md5.map("%02x".format(_)).mkString
  }
}

case class Profile(
  id: Long,
  hash: String,
  url: String,
  givenName: Option[String],
  familyName: Option[String],
  displayName: Option[String],
  about: Option[String],
  location: Option[String],
  ims: Map[String, String],
  urls: Map[String, String]
)

sealed abstract class DefaultImage(val value: String)
case object Monster extends DefaultImage("monsterid")
case object MysteryMan extends DefaultImage("mm")
case object IdentIcon extends DefaultImage("identicon")
case object Wavatar extends DefaultImage("wavatar")
case object Retro extends DefaultImage("retro")
case object FourOFour extends DefaultImage("404")
case class CustomImage(url: String) extends DefaultImage(URLEncoder.encode(URI.create(url).toString, "UTF-8"))
object DefaultImage{
  def apply(value: String):DefaultImage = value match {
    case Monster.value => Monster
    case MysteryMan.value => MysteryMan
    case IdentIcon.value => IdentIcon
    case Wavatar.value => Wavatar
    case Retro.value => Retro
    case FourOFour.value => FourOFour
    case x => CustomImage(x)
  }
  def unapply(di: DefaultImage) = Some(di.value)
}

sealed abstract class Rating(val value: String)
case object G extends Rating("g")
case object PG extends Rating("pg")
case object R extends Rating("r")
case object X extends Rating("x")
object Rating {
  def apply(value: String): Rating = value match{
    case G.value => G
    case PG.value => PG
    case R.value => R
    case X.value => X
    case x => throw new IllegalArgumentException(x + " is not a valid rating")
  }
}