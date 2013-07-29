name := "scravatar"

scalaVersion:="2.10.2"

crossScalaVersions := Seq("2.9.3", "2.10.2")

organization := "com.andersen-gott"

version := "1.0.3"

libraryDependencies <++= (scalaVersion) { sv =>
  sv.split("[.-]").toList match {
    case "2" :: "9" :: _ => Seq(
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
      "org.scalatest" %% "scalatest" % "1.9.1" % "test")
    case _ => Seq(
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
      "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test")
  }
}

credentials += Credentials(Path.userHome / ".sbt" / "magott-credentials")

useGpg:=true

publishTo <<= (version) { version: String =>
    if (version.trim.endsWith("SNAPSHOT"))
      Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
    else
      Some("Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

seq(aetherPublishSettings: _*)

homepage := Some(new URL("http://github.com/magott/scravatar"))

startYear := Some(2012)

licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))

pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ xml.Group(
  <scm>
    <url>http://github.com/magott/scravatar</url>
  	<connection>scm:git:git://github.com/magott/scravatar.git</connection>
  	<developerConnection>scm:git:git@github.com:magott/scravatar.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>magott</id>
  	  <name>Morten Andersen-Gott</name>
  	  <url>http://www.andersen-gott.com</url>
  	</developer>
  </developers>
)}