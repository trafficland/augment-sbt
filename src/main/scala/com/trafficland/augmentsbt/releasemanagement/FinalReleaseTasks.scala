package com.trafficland.augmentsbt.releasemanagement

import sbt.{Setting, SettingKey}

object FinalReleaseTasks {

  lazy val releasePublishLibFinalSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "releasePublishLibFinalTasks",
    "a list of tasks to execute (in order) for publishing a library's final release"
  )

  lazy val releasePublishLibFinalTasks: Setting[Seq[String]] = releasePublishLibFinalSettingKey := Seq(
    "releaseReady",
    "versionToFinal",
    "+publishLocal",
    "+publish",
    "gitReleaseCommit",
    "gitCheckoutMaster",
    "gitMergeDevelop",
    "gitTag",
    "gitCheckoutDevelop",
    "versionBumpPatch",
    "versionToSnapshot",
    "gitVersionBumpCommit",
    "gitPushOrigin"
  )

  lazy val releaseAppFinalSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "releaseAppFinalTasks",
    "a list of tasks to execute (in order) for releasing an app's final release"
  )

  lazy val releaseAppFinalTasks: Setting[Seq[String]] = releaseAppFinalSettingKey := Seq(
    "releaseReady",
    "versionToFinal",
    "gitReleaseCommit",
    "gitCheckoutMaster",
    "gitMergeDevelop",
    "gitTag",
    "gitCheckoutDevelop",
    "versionBumpPatch",
    "versionToSnapshot",
    "gitVersionBumpCommit",
    "gitPushOrigin"
  )
}