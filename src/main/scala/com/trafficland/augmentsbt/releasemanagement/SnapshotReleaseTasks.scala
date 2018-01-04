package com.trafficland.augmentsbt.releasemanagement

import sbt.{Setting, SettingKey}

object SnapshotReleaseTasks {

  lazy val releasePublishLibSnapshotSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "releasePublishLibSnapshotTasks",
    "a list of tasks to execute (in order) for publishing a library's snapshot release"
  )

  lazy val releasePublishLibSnapshotTasks: Setting[Seq[String]] = releasePublishLibSnapshotSettingKey := Seq(
    "releaseReady",
    "+publishLocal",
    "+publish",
    "versionWriteSnapshotRelease",
    "gitReleaseCommit",
    "gitTag",
    "versionToSnapshot",
    "gitVersionBumpCommit",
    "gitPushOrigin"
  )

  lazy val releaseAppSnapshotSettingKey: SettingKey[Seq[String]] = SettingKey[Seq[String]] (
    "releaseAppSnapshotTasks",
    "a list of tasks to execute (in order) for releasing an app's snapshot release"
  )

  lazy val releaseAppSnapshotTasks: Setting[Seq[String]] = releaseAppSnapshotSettingKey := Seq(
    "releaseReady",
    "versionWriteSnapshotRelease",
    "gitReleaseCommit",
    "gitTag",
    "versionToSnapshot",
    "gitVersionBumpCommit",
    "gitPushOrigin"
  )
}