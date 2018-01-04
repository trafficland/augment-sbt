package com.trafficland.augmentsbt.generators

import sbt.{File, SettingKey, TaskKey}

trait BuildInfoKeys {
  val buildInfoPropertiesFileName: SettingKey[String] = SettingKey[String](
    "build-info-properties-file-name",
    "filename used to build the buildInfoPropertiesFile setting"
  )

  val buildInfoClassFileName = "BuildInfo.scala"

  val buildInfoPropertiesWrite: TaskKey[Seq[File]] = TaskKey[Seq[File]](
    "build-info-properties-write",
    "writes various build properties to a file in managed resources"
  )

  val buildInfoPropertiesFile: SettingKey[File] = SettingKey[File](
    "build-info-properties-file",
    "path to the buildinfo.properties file (will end up as a child under managed resources)"
  )

  val generateBuildInfoClass: TaskKey[Seq[File]] = TaskKey[Seq[File]](
    "generate-build-info-class",
    "generates the BuildInfo.scala file"
  )
}
