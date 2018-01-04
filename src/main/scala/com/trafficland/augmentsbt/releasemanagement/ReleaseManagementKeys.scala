package com.trafficland.augmentsbt.releasemanagement

import sbt.{SettingKey, TaskKey}

import scala.util.matching.Regex

trait ReleaseManagementKeys {
  val releaseReady: TaskKey[Boolean] = TaskKey[Boolean](
    "release-ready",
    "checks to see if current source tree and project can be published"
  )

  val isApp: SettingKey[Boolean] = SettingKey[Boolean]("is-app",
    "Used by the release commands to determine if the release should be published.  " +
      "If isApp is set to true (default) then the release will not be published.")

  val remoteGitRepoPatterns: SettingKey[Seq[Regex]] = SettingKey[Seq[Regex]](
    "remote-git-repo-patterns",
    "the pattern that the tracking remote must match to be considered acceptable for release"
  )
}

