package com.trafficland.augmentsbt.generators

import java.io.File

import sbt.{SettingKey, TaskKey}

import scala.collection.Seq

trait LogbackConfigurationKeys {
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
