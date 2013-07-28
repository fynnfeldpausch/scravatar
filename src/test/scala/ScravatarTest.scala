package scravatar

import org.scalatest.FunSuite

class ScravatarTest extends FunSuite {
  val email = "morten@andersen-gott.com"

  test("Create Gravatar and set properties") {
    val gravatar = Gravatar(email).ssl(true).default(Monster, true).maxRatedAs(R).size(100).url
    assert(gravatar.contains("https"))
    assert(gravatar.contains("d=monster"))
    assert(gravatar.contains("f=y"))
    assert(gravatar.contains("r=r"))
    assert(gravatar.contains("s=100"))
  }

  test("Gravatar's email is normalized") {
    val target = Gravatar(email)
    val upper = Gravatar("MORTEN@ANDERSEN-GOTT.com")
    val space = Gravatar(" morten@andersen-gott.com   ")
    assert(target.hash === upper.hash)
    assert(target.hash === space.hash)
  }

  test("Gravatar's size is validated") {
    intercept[IllegalArgumentException] {
      Gravatar(email).size(0)
    }
    intercept[IllegalArgumentException] {
      Gravatar(email).size(2049)
    }
  }

  test("Gravatar's custom image URL is encoded and validated") {
    val target = Gravatar(email).default(CustomImage("http://example.com/image.jpg"))
    assert(target.defaultImage.get.value === "http%3A%2F%2Fexample.com%2Fimage.jpg")
    intercept[IllegalArgumentException] {
      CustomImage("http://example.com}")
    }
  }

  test("Get the Gravatar's image") {
    val gravatar = Gravatar("morten@andersen-gott.com").size(5)
    val img64 = new sun.misc.BASE64Encoder().encode(gravatar.image)
    val str64 = 
      """/9j/4AAQSkZJRgABAQAAAQABAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcg
        |SlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwADAgIDAgIDAwMDBAMDBAUIBQUEBAUKBwcGCAwK
        |DAwLCgsLDQ4SEA0OEQ4LCxAWEBETFBUVFQwPFxgWFBgSFBUU/9sAQwEDBAQFBAUJBQUJFA0LDRQU
        |FBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQU/8AAEQgABQAF
        |AwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMF
        |BQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkq
        |NDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqi
        |o6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/E
        |AB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMR
        |BAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVG
        |R0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKz
        |tLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A
        |8juPi/rmr2NnZXN1dBbIMYkiuSkKI5yFSMDC428nPzdeKKKK+TcIpvQ9FV6tl73RH//Z""".stripMargin
    assert(img64 === str64)
  }

  test("Get the Gravatar's profile") {
    val gravatar = Gravatar("f.feldpausch@live.de")
    val profile = scala.concurrent.Await.result(gravatar.profile, scala.concurrent.duration.Duration.Inf)
    assert(profile.hash === "70646dc0868a5f941408d4e9c14e9f8c")
    assert(profile.givenName === Some("Fynn"))
    assert(profile.familyName === Some("Feldpausch"))
  }
}