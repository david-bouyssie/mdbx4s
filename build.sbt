import scala.language.postfixOps

val sharedSettings = Seq(
  name := "mdbx4s",
  organization := "com.github.david-bouyssie",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.6", //2.11.12",
  crossScalaVersions := Seq("2.13.6", "2.12.15", "2.11.12"),

  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.7.10" % Test
  ),
  
  //resolvers += "FuseSource Community Snapshot Repository" at "https://repository.jboss.org/nexus/content/groups/fs-public-snapshots/"

  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val mdbx4s = crossProject(JVMPlatform, NativePlatform)
//lazy val mdbx4s = crossProject(NativePlatform)
  .crossType(CrossType.Full)
  .in(file("mdbx4s"))
  .settings(
    sharedSettings
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.fusesource.hawtjni" % "hawtjni-runtime" % "1.19-SNAPSHOT" from
        "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/hawtjni-runtime-1.19-SNAPSHOT.jar",
      "org.fusesource.mdbxjni" % "mdbxjni-win64" % "99-master-SNAPSHOT" from
        "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-win64-99-master-SNAPSHOT.jar",
      "org.fusesource.mdbxjni" % "mdbxjni-all" % "99-master-SNAPSHOT" from
        "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-99-master-SNAPSHOT.jar"
    ),
    fork := true
  )
  // configure Scala-Native settings
  .nativeSettings(

    // Customize Scala Native settings
    nativeLinkStubs := true, // Set to false or remove if you want to show stubs as linking errors
    nativeMode := "debug", // "debug", //"release-fast", //"release-full",
    nativeLTO := "none",   // "thin",
    nativeGC := "immix",   // "immix",

    /*nativeCompileOptions ++= Seq(
      "-gcodeview-ghash", "-Wl,/DEBUG:GHASH"
    ),*/

    nativeLinkingOptions ++= Seq(
      // Location of native libraries
      "-L" ++ baseDirectory.value.getAbsolutePath() ++ "/nativelib"
    )

  )

lazy val mdbx4sJVM    = mdbx4s.jvm
lazy val mdbx4sNative = mdbx4s.native

// TODO: uncomment this when ready for publishing
/*
val publishSettings = Seq(

  // Your profile name of the sonatype account. The default is the same with the organization value
  sonatypeProfileName := "david-bouyssie",

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/david-bouyssie/mdbx4s"),
      "scm:git@github.com:david-bouyssie/mdbx4s.git"
    )
  ),

  developers := List(
    Developer(
      id    = "david-bouyssie",
      name  = "David Bouyssié",
      email = "",
      url   = url("https://github.com/david-bouyssie")
    )
  ),
  description := "",
  licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")), // FIXME: update license
  homepage := Some(url("https://github.com/david-bouyssie/mdbx4s")),
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true,

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },

  // Workaround for issue https://github.com/sbt/sbt/issues/3570
  updateOptions := updateOptions.value.withGigahorse(false),

  useGpg := true,
  pgpPublicRing := file("~/.gnupg/pubring.kbx"),
  pgpSecretRing := file("~/.gnupg/pubring.kbx"),

  Test / skip in publish := true
)*/
