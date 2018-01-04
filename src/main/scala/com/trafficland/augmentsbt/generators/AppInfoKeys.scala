package com.trafficland.augmentsbt.generators

import sbt.{File, SettingKey, TaskKey}

trait AppInfoKeys {
  val appInfoPropertiesFileName: SettingKey[String] = SettingKey[String](
    "app-info-properties-file-name",
    "filename used to build the appInfoPropertiesFile setting"
  )
  val appInfoClassFileName = "AppInfo.scala"

  val appInfoPropertiesWrite: TaskKey[Seq[File]] = TaskKey[Seq[File]](
    "app-info-properties-write",
    "writes the application properties file in managed resources"
  )

  val appInfoPropertiesFile: SettingKey[File] = SettingKey[File](
    "app-info-properties-file",
    "path to the appinfo.properties file (will end up as a child under managed resources)"
  )

  val generateAppInfoClass: TaskKey[Seq[File]] = TaskKey[Seq[File]](
    "generate-app-info-class",
    "generates the AppInfo.scala file"
  )
}