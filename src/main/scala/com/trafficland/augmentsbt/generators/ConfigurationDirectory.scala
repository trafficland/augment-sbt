package com.trafficland.augmentsbt.generators

import sbt._
import java.io.File
import sbt.Keys._

object ConfigurationDirectory extends AutoPlugin {
  import autoImport._
  
  object autoImport {
    val confDirectory: SettingKey[File] = SettingKey[File]("conf-directory")
  }
  
  override lazy val projectSettings = Seq(
    confDirectory := baseDirectory.value / "conf"
  )
}