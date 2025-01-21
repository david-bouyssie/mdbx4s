# MDBX4S

MDBX4S is port of the Java library [MDBX JNI](https://github.com/castortech/mdbxjni) for the Scala Native platform.

The goal of this project is to provide a thin wrapper around the LIBMDBX C library with an API similar to the 'MDBX JNI' one.

# Current status of the project

This is the first release of this library, so the implementation/API are still unstable, you have been warned ;-)
The long-term goal is to provide similar features, API and stability for both the JVM and SN implementations.

Please, also note that this library is not yet released on maven central, so to use it, you will have to clone this repository and then publishLocal from SBT.

## Getting started
<!-- [![Maven Central](https://img.shields.io/maven-central/v/com.github.david-bouyssie/sqlite4s_native0.3_2.11/0.1.0)](https://mvnrepository.com/artifact/com.github.david-bouyssie/sqlite4s_native0.3_2.11/0.1.0) -->

If you are already familiar with Scala Native you can jump right in by adding the following dependency in your `sbt` build file.

```scala
libraryDependencies += "com.github.david-bouyssie" %%% "mdbx4s" % "x.y.z"
```

To use in `sbt`, replace `x.y.z` with the latest version number (currently 0.1.0-SNAPSHOT).

<!-- To use in `sbt`, replace `x.y.z` with the version from Maven Central badge above.
     All available versions can be seen at the [Maven Repository](https://mvnrepository.com/artifact/com.github.david-bouyssie/sqlite4s). -->

If you are not familiar with Scala Native, please follow the relative [Getting Started](https://scala-native.readthedocs.io/en/latest/user/setup.html) instructions.

Additionally, you need to install [LIBMDBX](https://gitflic.ru/project/erthink/libmdbx) on your system as follows:

```
git clone https://gitflic.ru/project/erthink/libmdbx.git ../libmdbx --branch v0.7.0
make -C ../libmdbx dist
```

* Then copy the generated shared library in sub-directory of your own project (called for instance "nativelib").
Then you would also have to change your build.sbt file and add the following settings:
```
nativeLinkingOptions ++= Seq("-L" ++ baseDirectory.value.getAbsolutePath() ++ "/nativelib")
```

## Useful links:
* [LIBMDBX Github](https://gitflic.ru/project/erthink/libmdbx)
* [LIBMDBX documentation](https://libmdbx.dqdkfa.ru/)
* [MDBX JNI Github](https://github.com/castortech/mdbxjni)
* [A blog article about a Rust wrapper for MDBX](https://rmw.link/log/2021-12-21-mdbx.html)
