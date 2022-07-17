ThisBuild / organization := "com.github.ammrat13.pynqz2ramtester"
ThisBuild / scalaVersion := "2.13.8"

// SpinalHDL Packages
val spinalVersion = "1.7.1"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin(
  "com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "root",
    libraryDependencies ++= Seq(spinalCore, spinalLib, spinalIdslPlugin),
  )
