package com.trafficland.augmentsbt.scalaconfiguration

import sbt._
import sbt.Keys._

object ScalaConfigurationPlugin extends AutoPlugin {

  override lazy val projectSettings = Seq(
    scalaVersion  := "2.12.1",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps", "-language:implicitConversions")
  )
}
