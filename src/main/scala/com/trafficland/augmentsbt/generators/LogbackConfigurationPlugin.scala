package com.trafficland.augmentsbt.generators

import sbt._

import scala.collection.Seq
import java.io.File

import sbt.Keys._
import ConfigurationDirectory.autoImport._
import sbt.plugins.JvmPlugin

object LogbackConfigurationPlugin extends AutoPlugin with FileGenerator {
  import autoImport._

  override lazy val requires: Plugins = ConfigurationDirectory && JvmPlugin

  object autoImport {
    val generateLogbackConf: TaskKey[Seq[File]] = TaskKey[Seq[File]](
      "generate-logback-conf",
      "destructively generates a default logback configuration"
    )

    val generateLogbackTestConf: TaskKey[Seq[File]] = TaskKey[Seq[File]](
      "generate-logback-test-conf",
      "destructively generates a default logback test configuration that restarts log files and writes to STDOUT"
    )

    val logbackTargetFile: SettingKey[File] = SettingKey[File]("logback-target-file")
    /**
      * The logback-test.xml file destination target.
      */
    val logbackTestTargetFile: SettingKey[File] = SettingKey[File]("logback-test-target-file")
  }

  override lazy val projectSettings = Seq(
    logbackTargetFile <<= confDirectory(_ / "logback.xml"),
    logbackTestTargetFile <<= confDirectory(_ / "logback-test.xml"),
    generateLogbackConf <<= (streams, normalizedName, logbackTargetFile) map { (out, name, tf) =>
      generate(out, "logback.xml.template", normalizedNameModification(name), tf)
    },
    generateLogbackTestConf <<= (streams, normalizedName, logbackTestTargetFile) map { (out, name, tf) =>
      generate(out, "logback-test.xml.template", normalizedNameModification(name), tf)
    }
  )

}
