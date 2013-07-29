# Scravatar - A simple Scala library for Gravatar

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






More info at [Gravatar](http://gravatar.com/site/implement/)


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