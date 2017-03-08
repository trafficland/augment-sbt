package com.trafficland.augmentsbt.releasemanagement

import sbt.{Setting, SettingKey}

object SnapshotReleaseTasks {

  lazy val releasePublishLibSnapshotSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "release-publish-lib-snapshot-tasks",
    "a list of tasks to execute (in order) for publishing a library's snapshot release"
  )

  lazy val releasePublishLibSnapshotTasks: Setting[Seq[String]] = releasePublishLibSnapshotSettingKey := Seq(
    "release-ready",
    "+publish-local",
    "+publish",
    "versionWriteSnapshotRelease",
    "git-release-commit",
    "git-tag",
    "versionToSnapshot",
    "git-version-bump-commit",
    "git-push-origin"
  )

  lazy val releaseAppSnapshotSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "release-app-snapshot-tasks",
    "a list of tasks to execute (in order) for releasing an app's snapshot release"
  )

  lazy val releaseAppSnapshotTasks: Setting[Seq[String]] = releaseAppSnapshotSettingKey := Seq(
    "release-ready",
    "versionWriteSnapshotRelease",
    "git-release-commit",
    "git-tag",
    "versionToSnapshot",
    "git-version-bump-commit",
    "git-push-origin"
  )
}