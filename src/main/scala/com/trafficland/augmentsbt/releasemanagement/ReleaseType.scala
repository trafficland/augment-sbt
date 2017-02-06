package com.trafficland.augmentsbt.releasemanagement

import sbt.SettingKey
import com.trafficland.augmentsbt.versionmanagement.SemanticVersion

abstract class ReleaseType(){

  protected val appReleaseTasks : SettingKey[Seq[String]]
  protected val libReleaseTasks : SettingKey[Seq[String]]

  def getReleaseTasks(isApp:Boolean) : SettingKey[Seq[String]] = if (isApp) {
    appReleaseTasks
  } else {
    libReleaseTasks
  }

  def isValidReleaseVersion(version:String) : Boolean

  def versionsMatch(currentVersion: SemanticVersion, maybeReleaseVersion: Option[SemanticVersion]): Boolean
}
