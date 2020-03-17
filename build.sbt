val scala212 = "2.12.10"
val scala213 = "2.13.1"
organization := "com.codacy"
scalaVersion := scala212
crossScalaVersions := Seq(scala212, scala213)
name := "codacy-duplication-scala-seed"
libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.8.1",
                            "com.codacy" %% "codacy-plugins-api" % "4.0.2" withSources (),
                            "com.github.pathikrit" %% "better-files" % "3.8.0",
                            "org.specs2" %% "specs2-core" % "4.8.0" % Test)

// HACK: This setting is not picked up properly from the plugin
pgpPassphrase := Option(System.getenv("SONATYPE_GPG_PASSPHRASE")).map(_.toCharArray)

scmInfo := Some(
  ScmInfo(url("https://github.com/codacy/codacy-duplication-scala-seed"),
          "scm:git:git@github.com:codacy/codacy-duplication-scala-seed.git"))

description := "Library to develop Codacy duplication plugins"
licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("http://www.github.com/codacy/codacy-duplication-scala-seed/"))
pomExtra :=
  <developers>
    <developer>
      <id>rtfpessoa</id>
      <name>Rodrigo Fernandes</name>
      <email>rodrigo [at] codacy.com</email>
      <url>https://github.com/rtfpessoa</url>
    </developer>
    <developer>
      <id>bmbferreira</id>
      <name>Bruno Ferreira</name>
      <email>bruno.ferreira [at] codacy.com</email>
      <url>https://github.com/bmbferreira</url>
    </developer>
    <developer>
      <id>xplosunn</id>
      <name>Hugo Sousa</name>
      <email>hugo [at] codacy.com</email>
      <url>https://github.com/xplosunn</url>
    </developer>
    <developer>
      <id>pedrocodacy</id>
      <name>Pedro Amaral</name>
      <email>pamaral [at] codacy.com</email>
      <url>https://github.com/pedrocodacy</url>
    </developer>
  </developers>

publicMvnPublish
