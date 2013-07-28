package scravatar

import org.scalatest.FunSuite
import java.io.FileOutputStream

class ScravatarTest extends FunSuite {
  val email = "morten@andersen-gott.com"

  test("Simple Avatar url") {
    val gravatar = Gravatar(email).ssl(true).default(Monster)
    assert(gravatar.defaultImage.isDefined)
  }

  test("All props are combined") {
    val gravatar = Gravatar(email).ssl(true).default(Monster).maxRatedAs(R).forceDefault(true).size(100).url
    assert(gravatar.contains("=monster"))
  }

  test("Download") {
    val fos = new FileOutputStream("//tmp/pic.jpg")
    fos.write(Gravatar(email).image)
  }

  test("Fails if size > 2048") {
    intercept[IllegalArgumentException] {
      Gravatar(email).size(2049)
    }
  }
  test("Fails if size < 0 ") {
    intercept[IllegalArgumentException] {
      Gravatar(email).size(-1)
    }
  }

  test("E-mail is trimmed and lower cased ") {
    val target = Gravatar(email)
    val upper = Gravatar("MORTEN@ANDERSEN-GOTT.com")
    val space = Gravatar(" %s ".format(email))
    assert(target.hash == upper.hash)
    assert(target.hash == space.hash)
  }

  test("URL is encoded"){
    val target = Gravatar(email).default(DefaultImage("http://example.com/image.jpg"))
    assert (target.defaultImage.get.value == "http%3A%2F%2Fexample.com%2Fimage.jpg")
  }

  test("URL is validated") {
    intercept[IllegalArgumentException]{
      DefaultImage("http://example.com}")
    }
  }

  test("Get gravatar profile") {
    val gravatar = Gravatar("beau@dentedreality.com.au")
    val profile = scala.concurrent.Await.result(gravatar.profile, scala.concurrent.duration.Duration.Inf)
    assert(profile.hash === "205e460b479e2e5b48aec07710c08d50")
    assert(profile.givenName === Some("Beau"))
    assert(profile.familyName === Some("Lebens"))
  }
}