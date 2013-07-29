# Scravatar - A Scala library for Gravatar

This library is an extended version of the [tiny scala library for Gravatar](https://github.com/magott/scravatar)
by [Morten Andersen-Gott](https://github.com/magott).

## Using the Scravatar library

To start off simply create a new `Gravatar` instance and access its field.

    val gravatar = Gravatar("you@example.com")
    gravatar.email // the normalized email address
    gravatar.hash // the hashed value of the email address
    gravatar.url // the Gravatar image url

To set the Gravatar's properties call the respective instance methods.
This will create a new immutable `Gravatar` instance.

    val gravatar = Gravatar("you@example.com")
    gravatar.ssl(true)
    gravatar.default(Monster, force = true)
    gravatar.maxRatedAs(R)
    gravatar.size(100)

The Gravatar image can be downloaded and is available as a byte array.

    val gravatar = Gravatar("you@example.com")
    val imgage = gravatar.image

Finally, Scravatar provides easy access to the Gravatar's profile data.
As the response time of the Gravatar API may vary, the `Profile` is computed
asynchronously and wrapped in a future call. Further information on Scala's
futures can be found [in the Scala docs](http://docs.scala-lang.org/overviews/core/futures.html).

    import scala.concurrent._
    
    val gravatar = Gravatar("you@example.com")
    val profile = gravatar.profile
    val data = Await.result(profile, duration.Duration.Inf)
    data.url // the profile URL
    data.about // the optional AboutMe text
    //...

More info at [Gravatar.com](http://gravatar.com/site/implement/).

## Adding the Scravatar dependency

Scravatar is built and deployed on maven central. To use it with sbt add the following to your build file:

    libraryDependencies ++= Seq(
      "com.andersen-gott" %% "scravatar" % "1.0.2"
    )

With Maven:

    <dependency>
      <groupId>com.andersen-gott</groupId>
      <artifactId>scravatar_2.10</artifactId>
      <version>1.0.2</version>
    </dependency>
