package com.trafficland.augmentsbt.generators

import sbt._
import java.io.File
import sbt.Keys._

object ConfigurationDirectory extends AutoPlugin {

  object autoImport extends ConfigurationDirectoryKeys
  import autoImport._

  override lazy val projectSettings = Seq(
    confDirectory := baseDirectory.value / "conf"
  )
}