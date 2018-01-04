package com.trafficland.augmentsbt.git

import com.jcraft.jsch.agentproxy.Connector
import com.trafficland.augmentsbt.git.GitPlugin.RemoteBranch
import sbt.{InputKey, SettingKey, TaskKey}

trait GitKeys {
  val gitIsRepository: TaskKey[Boolean] = TaskKey[Boolean](
    "git-is-repository",
    "true if this is project is inside a git repo"
  )

  val gitStatus: TaskKey[Unit] = TaskKey[Unit](
    "git-status",
    "prints the current git repository status"
  )

  val gitIsCleanWorkingTree: TaskKey[Boolean] = TaskKey[Boolean](
    "git-is-clean-working-tree",
    "Checks if all tracked files have been committed"
  )

  val gitTrackingBranch: TaskKey[Option[RemoteBranch]] = TaskKey[Option[RemoteBranch]](
    "git-tracking-branch",
    "returns the remote branch name that the current branch is tracking"
  )

  val gitDescribeBranches: TaskKey[Map[String, Option[RemoteBranch]]] = TaskKey[Map[String, Option[RemoteBranch]]](
    "git-describe-branches",
    "returns a list of local branches and what they are tracking"
  )

  val gitBranchName: TaskKey[Option[String]] = TaskKey[Option[String]](
    "git-branch-name",
    "the name of the current git branch"
  )

  val gitShowAllTags: TaskKey[Seq[String]] = TaskKey[Seq[String]](
    "git-show-all-tags",
    "Returns all of the tags in the repository."
  )

  val gitTag: TaskKey[Unit] = TaskKey[Unit](
    "git-tag",
    "tag the project with the current version"
  )

  val gitTagName: TaskKey[String] = TaskKey[String](
    "git-tag-name",
    "define a tag name for the current project version (used by git-tag)"
  )

  val gitVersionBumpCommitMessage: TaskKey[String] = TaskKey[String](
    "git-version-bump-commit-message",
    "create a commit message for a version bump commit of this project (used by git-version-bump-commit)."
  )

  val gitVersionBumpCommit: TaskKey[Unit] = TaskKey[Unit](
    "git-version-bump-commit",
    "commit after bumping the version number of this project. Automatically generates the commit message."
  )


  val gitReleaseCommitMessage: TaskKey[String] = TaskKey[String](
    "git-release-commit-message",
    "create a commit message for a new release of this project (used by git-release-commit)"
  )

  val gitReleaseCommit: TaskKey[Unit] = TaskKey[Unit](
    "git-release-commit",
    "commit pending changes to this project (usually as part of publishing a release). Automatically generates the commit message."
  )

  val gitCommit: InputKey[Unit] = InputKey[Unit](
    "git-commit",
    "commit pending changes to this project (usually as part of publishing a release). You must provide a commit message."
  )

  val gitPushOrigin: TaskKey[Unit] = TaskKey[Unit](
    "git-push-origin",
    "pushes any commits and tags to the remote repository."
  )

  val gitCheckoutMaster: TaskKey[Unit] = TaskKey[Unit](
    "git-checkout-master",
    "Checkout the master branch.  This is usually used for doing a release."
  )

  val gitCheckoutDevelop: TaskKey[Unit] = TaskKey[Unit](
    "git-checkout-develop",
    "Checkout the develop branch.  This is usually used for doing a release."
  )

  val gitMergeDevelop: TaskKey[Unit] = TaskKey[Unit](
    "git-merge-develop",
    "Merge the develop branch into the current branch."
  )

  val gitHeadCommitSha: TaskKey[Option[String]] = TaskKey[Option[String]](
    "git-head-commit-sha",
    "Prints the HEAD commit SHA."
  )

  val gitLastCommitsCount: SettingKey[Int] = SettingKey[Int](
    "git-last-commits-count",
    "the number of commits to show with git-last-commits"
  )

  val gitLastCommits: TaskKey[Option[Seq[String]]] = TaskKey[Option[Seq[String]]](
    "git-last-commits",
    "the latest commits to the project"
  )

  val gitSshKeyAgent: SettingKey[Option[Connector]] = SettingKey[Option[Connector]](
    "git-ssh-key-agent",
    "the connector to acquire ssh identities"
  )
}
