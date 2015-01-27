import sbt._
import sbt.Keys._

object App extends Build 
{
  val appDependencies = List(
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "org.json4s" %% "json4s-native" % "3.2.10"
  )

  val appSettings = List(
    name := "app",
    version := "1.0",
    scalaVersion := "2.11.4",
    libraryDependencies := appDependencies
  )

  override lazy val settings = super.settings ++ appSettings
}
