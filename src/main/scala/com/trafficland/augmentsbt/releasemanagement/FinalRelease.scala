package com.trafficland.augmentsbt.releasemanagement

import sbt.SettingKey
import com.trafficland.augmentsbt.versionmanagement.SemanticVersion

case class FinalRelease() extends ReleaseType() {

  protected val appReleaseTasks: SettingKey[Seq[String]] = FinalReleaseTasks.releaseAppFinalSettingKey

  protected val libReleaseTasks: SettingKey[Seq[String]] = FinalReleaseTasks.releasePublishLibFinalSettingKey

  /* Any version is considered OK, including snapshots. versionsMatch will check for an unintended version early in the
     release process. Snapshot versions will be upgraded to final later, just before actual publishing */
  def isValidReleaseVersion(version: String): Boolean = true

  def versionsMatch(currentVersion: SemanticVersion, maybeReleaseVersion: Option[SemanticVersion]): Boolean =
    maybeReleaseVersion.exists(_ == currentVersion.toFinal)
}