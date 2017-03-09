package com.trafficland.augmentsbt.releasemanagement

import sbt.SettingKey
import com.trafficland.augmentsbt.versionmanagement.SemanticVersion
import com.trafficland.augmentsbt.versionmanagement.VersionManagementPlugin.autoImport._

case class SnapshotRelease() extends ReleaseType() {

  protected val appReleaseTasks: SettingKey[Seq[String]] = SnapshotReleaseTasks.releaseAppSnapshotSettingKey

  protected val libReleaseTasks: SettingKey[Seq[String]] = SnapshotReleaseTasks.releasePublishLibSnapshotSettingKey

  def isValidReleaseVersion(version: String): Boolean = version.isSnapshot

  // Any version is OK. We don't require version confirmation for snapshot releases.
  def versionsMatch(currentVersion: SemanticVersion, maybeReleaseVersion: Option[SemanticVersion]): Boolean = true
}
