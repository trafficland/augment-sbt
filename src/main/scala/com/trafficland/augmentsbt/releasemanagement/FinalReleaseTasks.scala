package com.trafficland.augmentsbt.releasemanagement

import sbt.{Setting, SettingKey}

object FinalReleaseTasks {

  lazy val releasePublishLibFinalSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "release-publish-lib-final-tasks",
    "a list of tasks to execute (in order) for publishing a library's final release"
  )

  lazy val releasePublishLibFinalTasks: Setting[Seq[String]] = releasePublishLibFinalSettingKey := Seq(
    "release-ready",
    "versionToFinal",
    "+publish-local",
    "+publish",
    "git-release-commit",
    "git-checkout-master",
    "git-merge-develop",
    "git-tag",
    "git-checkout-develop",
    "versionBumpPatch",
    "versionToSnapshot",
    "git-version-bump-commit",
    "git-push-origin"
  )

  lazy val releaseAppFinalSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "release-app-final-tasks",
    "a list of tasks to execute (in order) for releasing an app's final release"
  )

  lazy val releaseAppFinalTasks: Setting[Seq[String]] = releaseAppFinalSettingKey := Seq(
    "release-ready",
    "versionToFinal",
    "git-release-commit",
    "git-checkout-master",
    "git-merge-develop",
    "git-tag",
    "git-checkout-develop",
    "versionBumpPatch",
    "versionToSnapshot",
    "git-version-bump-commit",
    "git-push-origin"
  )
}