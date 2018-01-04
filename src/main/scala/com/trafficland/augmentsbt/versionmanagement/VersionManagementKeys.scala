package com.trafficland.augmentsbt.versionmanagement

import sbt.SettingKey

trait VersionManagementKeys {
  val versionSettingRegexes: SettingKey[Seq[String]] = SettingKey[Seq[String]](
    "version-setting-regexes",
    "a list of regexes to use to replace versions"
  )
}
