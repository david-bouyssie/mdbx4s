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

  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val copySharedLibraries = taskKey[Unit]("Copy shared libraries")

lazy val mdbx4s = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("mdbx4s"))
  .settings(
    sharedSettings
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.fusesource.hawtjni" % "hawtjni-runtime" % "1.19-SNAPSHOT" from
        "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/hawtjni-runtime-1.19-SNAPSHOT.jar",
      "org.fusesource.mdbxjni" % "mdbxjni-all" % "99-master-SNAPSHOT" from
        "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-99-master-SNAPSHOT.jar"
    ) ++ {
      Seq(
        sys.props("os.name").toLowerCase match {
          case win if win.contains("win") =>
            "org.fusesource.mdbxjni" % "mdbxjni-win64" % "99-master-SNAPSHOT" from
              "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-win64-99-master-SNAPSHOT.jar"
          case linux if linux.contains("linux") =>
            "org.fusesource.mdbxjni" % "mdbxjni-linux64" % "99-master-SNAPSHOT" from
              "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-linux64-99-master-SNAPSHOT.jar"
          case mac if mac.contains("mac") =>
            "org.fusesource.mdbxjni" % "mdbxjni-osx64" % "99-master-SNAPSHOT" from
              "https://github.com/castortech/mdbxjni/releases/download/v0.11.2/mdbxjni-osx64-99-master-SNAPSHOT.jar"
          case osName: String => throw new RuntimeException(s"Unknown operating system $osName")
        }
      )
    },
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
    ),

    // See: https://stackoverflow.com/questions/36237174/how-to-copy-some-files-to-the-build-target-directory-with-sbt
    copySharedLibraries := {

      val s: TaskStreams = streams.value

      val nativeLibDir = baseDirectory.value / "nativelib"
      s.log.info("libDir="+nativeLibDir)
      Predef.assert(nativeLibDir.isDirectory(), "can't find 'nativelib' directory")

      val targetDir = (Compile / crossTarget).value

      // Find the shared libraries
      val libFiles: Seq[File] = (nativeLibDir ** "*.dll").get() ++ (nativeLibDir ** "*.so").get()

      // Use Path.rebase to pair source files with target destination in crossTarget
      import Path._
      val pairs = libFiles pair rebase(nativeLibDir, targetDir)

      s.log.info(s"Copying shared libraries from '$nativeLibDir' to target '$targetDir'...")

      // Copy files to source files to target
      IO.copy(pairs, CopyOptions.apply(overwrite = false, preserveLastModified = true, preserveExecutable = false))

      ()
    },

    (Compile / compile) := ((Compile / compile) dependsOn copySharedLibraries).value

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
