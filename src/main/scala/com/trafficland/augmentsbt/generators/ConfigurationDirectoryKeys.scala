package com.trafficland.augmentsbt.generators

import java.io.File

import sbt.SettingKey

trait ConfigurationDirectoryKeys {
  val confDirectory: SettingKey[File] = SettingKey[File]("conf-directory")
}
