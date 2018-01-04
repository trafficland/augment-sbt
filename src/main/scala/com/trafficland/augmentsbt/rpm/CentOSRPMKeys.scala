package com.trafficland.augmentsbt.rpm

import sbt.{File, SettingKey}

trait CentOSRPMKeys {
  val scriptsDirectory: SettingKey[File] = SettingKey[File]("scripts-directory")
}
