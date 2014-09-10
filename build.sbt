name:= "pasties"

organization := "org.bmcg"

version := "0.0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "junit" % "junit" % "4.10" % "test"
  )

seq(bintrayPublishSettings:_*)

licenses := Seq("GPL-3.0" -> url("https://www.gnu.org/licenses/gpl.txt"))

homepage := Some(url("https://www.github.com/b-mcg"))
