package com.trafficland

import scala.language.implicitConversions
import com.trafficland.augmentsbt.generators.AppInfoPlugin
import com.trafficland.augmentsbt.versionmanagement.{SemanticVersion, VersionManagementPlugin}
import sbt.SettingKey

package object augmentsbt {

  @deprecated("VersionManagementPlugin autoImports this functionality", "1.1.0")
  implicit def toVersion(originalVersion:String): SemanticVersion = VersionManagementPlugin.autoImport.toVersion(originalVersion)
  val isApp: SettingKey[Boolean] = releasemanagement.ReleaseManagementPlugin.autoImport.isApp
  val Git = git.GitPlugin
  val PackageManagement = packagemanagement.PackageManagementPlugin
  val Play20 = play20.Play20Plugin
  val ReleaseManagement = releasemanagement.ReleaseManagementPlugin
  val ScalaConfiguration = scalaconfiguration.ScalaConfigurationPlugin
  val VersionManagement = versionmanagement.VersionManagementPlugin
  val AppInfo = AppInfoPlugin
}
