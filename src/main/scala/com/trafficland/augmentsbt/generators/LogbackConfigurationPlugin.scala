package com.trafficland.augmentsbt.generators

import sbt._

import scala.collection.Seq
import java.io.File

import sbt.Keys._
import ConfigurationDirectory.autoImport._
import sbt.plugins.JvmPlugin

object LogbackConfigurationPlugin extends AutoPlugin with FileGenerator {

  override lazy val requires: Plugins = ConfigurationDirectory && JvmPlugin

  object autoImport extends LogbackConfigurationKeys
  import autoImport._

  override lazy val projectSettings = Seq(
    logbackTargetFile := confDirectory(_ / "logback.xml").value,
    logbackTestTargetFile := confDirectory(_ / "logback-test.xml").value,
    generateLogbackConf := generate(streams.value, "logback.xml.template", normalizedNameModification(normalizedName.value), logbackTargetFile.value),
    generateLogbackTestConf := generate(streams.value, "logback-test.xml.template", normalizedNameModification(normalizedName.value), logbackTestTargetFile.value)
  )

}
