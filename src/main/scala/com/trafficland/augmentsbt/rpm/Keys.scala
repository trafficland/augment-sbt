package com.trafficland.augmentsbt.rpm

import sbt.{SettingKey, URL}

object Keys {
  // vendor directory on target machine; defaults to /opt/$rpmVendor
  val vendorDirectory: SettingKey[String] = SettingKey[String]("vendor-directory")
  // destination directory on target machine as a child of the vendor-directory setting
  val destinationDirectory: SettingKey[String] = SettingKey[String]("destination-directory")
  // combination of vendor-directory and destination-directory separated by a path character
  val installationDirectory: SettingKey[String] = SettingKey[String]("installation-directory")
  // the linux user to run the software under
  // the linux group to run the software under
  val linuxUserAndGroup: SettingKey[(String, String)] = SettingKey[(String, String)]("linux-user-and-group")
  val installedInitScriptName: SettingKey[String] = SettingKey[String]("installed-init-script-name")
  val manageDaemonAccounts: SettingKey[Boolean] = SettingKey[Boolean]("manage-daemon-accounts", "Controls whether to create and remove the daemon user and group on install and uninstall")
  val scriptTemplates: SettingKey[Map[String, URL]] = SettingKey[Map[String, URL]]("script-templates", "Mappings of RPM script type to its template")
}
