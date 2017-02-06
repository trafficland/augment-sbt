package com.trafficland

import scala.language.implicitConversions
import com.trafficland.augmentsbt.generators.AppInfoPlugin
import com.trafficland.augmentsbt.versionmanagement.SemanticVersion
import sbt.SettingKey

package object augmentsbt {

  implicit def toVersion(originalVersion:String): SemanticVersion = SemanticVersion.toVersion(originalVersion)
  val isApp: SettingKey[Boolean] = releasemanagement.ReleaseManagementPlugin.autoImport.isApp
  val Git = git.GitPlugin
  val PackageManagement = packagemanagement.PackageManagementPlugin
  val Play20 = play20.Play20Plugin
  val ReleaseManagement = releasemanagement.ReleaseManagementPlugin
  val ScalaConfiguration = scalaconfiguration.ScalaConfigurationPlugin
  val VersionManagement = versionmanagement.VersionManagementPlugin
  val AppInfo = AppInfoPlugin
}
