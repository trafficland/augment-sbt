package com.trafficland.augmentsbt.releasemanagement

import sbt._
import Keys._
import com.trafficland.augmentsbt.git.GitPlugin
import GitPlugin.autoImport._
import sbt.complete.Parser
import org.eclipse.jgit.lib.Repository
import com.trafficland.augmentsbt.git.GitPlugin.RemoteBranch
import com.trafficland.augmentsbt.versionmanagement.{SemanticVersion, VersionManagementPlugin}

import scala.language.postfixOps
import scala.util.matching.Regex

object ReleaseManagementPlugin extends AutoPlugin {
  import autoImport._
  import VersionManagementPlugin.autoImport._

  override lazy val requires: Plugins = GitPlugin && VersionManagementPlugin

  override def globalSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(
    remoteGitRepoPatterns := Seq.empty
  )

  override lazy val projectSettings = Seq(
    isApp := true,

    commands ++= Seq(releaseSnapshot(), releaseFinal()),

    FinalReleaseTasks.releaseAppFinalTasks,
    FinalReleaseTasks.releasePublishLibFinalTasks,
    SnapshotReleaseTasks.releasePublishLibSnapshotTasks,
    SnapshotReleaseTasks.releaseAppSnapshotTasks,

    releaseReady <<= (gitIsCleanWorkingTree, gitTrackingBranch, gitDescribeBranches, gitShowAllTags, version, remoteGitRepoPatterns, streams) map {
      (isClean, maybeTrackingBranch, branches, tags, ver, remotePatterns, stream) =>

      val finalVersion = ver.toFinal.toReleaseFormat
      stream.log.info(s"stable version $finalVersion")
      stream.log.debug(s"remoteGitRepoPatterns = $remotePatterns.")

      val masterRemote: Option[RemoteBranch] = {
        for {
          master <- branches.find { case (branchName, _) => branchName == "master" }
          masterRemote <- master._2
        } yield masterRemote
      }
      val masterOk = masterRemote.exists(r => r.branch == "master" && remoteRepoCorrect(remotePatterns, r.remoteUri))
      val developOk = maybeTrackingBranch.exists(r => r.branch == "develop" && masterRemote.exists(_.remoteUri == r.remoteUri))

      // we don't release dirty trees
      if (!isClean) {
        stream.log.error("Working directory is not clean.")
        false
      }
      // we don't double-release
      else if (tags.map(Repository.shortenRefName).exists(_.endsWith(finalVersion))) {
        stream.log.error("Cannot tag release version %s: tag already exists.".format(finalVersion))
        false
      }
      else if (!masterOk) {
        stream.log.error(s"The local master branch must track master on a remote matching $masterRemote")
        false
      }
      else if (!developOk) {
        val trackingBranchDisplay = maybeTrackingBranch.map(t => s"${t.name}:${t.branch}").getOrElse("{untracked}")
        stream.log.error(s"This branch must track develop on the same remote as master, is tracking $trackingBranchDisplay instead")
        false
      } else {
        stream.log.info("Current project is ok for release.")
        true
      }
    }
  )

  def remoteRepoCorrect(remoteRepoPatterns: Seq[Regex], repoURL: String): Boolean =
    remoteRepoPatterns.exists( pattern => pattern.unapplySeq(repoURL).isDefined )

  object autoImport {
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

  def releaseSnapshot(): Command = Command.command(
    "releaseSnapshot",
    "Tag and release a snapshot version of an app or lib.",
    ""
  )(release(SnapshotRelease())(_: State, None))

  def releaseFinal(): Command = Command(
    "releaseFinal",
    Help.more(
      "releaseFinal",
      "Tag and release a final version of an app or lib by removing SNAPSHOT from the version and bumping the patch value. " +
        "Takes the intended version as input for confirmation."
    )
  )(releaseParser)(release(FinalRelease()))

  def releaseParser(state: State): Parser[Option[SemanticVersion]] = {
    import sbt.complete.DefaultParsers._
    val version = Space ~> NatBasic ~ ("." ~> NatBasic) ~ ("." ~> NatBasic)
    version.?.map {
      _.map {
        case ((major, minor), patch) =>
          SemanticVersion(major, minor, patch, "")
      }
    }
  }

  def release(releaseType:ReleaseType): (State, Option[SemanticVersion]) => State = { (state: State, intendedVersion: Option[SemanticVersion]) =>
    state.log.info(releaseType.toString)
    val extracted = Project.extract(state)

    val currentVersion = extracted.get(version)
    val validVersion = releaseType.isValidReleaseVersion(currentVersion)
    if (!validVersion)
      state.log.error("Attempting snapshot release but version is set to final version.")

    val correctIntendedVersion = releaseType.versionsMatch(currentVersion, intendedVersion)
    if (!correctIntendedVersion){
      intendedVersion match {
        case Some(version) => state.log.error(s"The version you intend to release, $version, does not match the current version of the project, ${currentVersion.toFinal}.")

        case _ => state.log.error(s"You did not specify the version you intend to release. E.g. > releaseFinal 0.99.0")
      }
    }

    if (validVersion && correctIntendedVersion) Project.runTask(releaseReady, state) match {
      // returned if releaseReady doesn't exist in the current state
      case None =>
        state.log.error("no release-ready task defined")
        state.fail

      // returned if releaseReady failed
      case Some((_, Inc(i))) =>
        Incomplete.show(i.tpe)
        state.fail

      case Some((_, Value(false))) =>
        state.log.error("Stopping release.")
        state.fail

      // we're ok for release, so return a new state with the publish tasks appended
      case Some((s, Value(true))) =>
        val pubTasks = extracted.get(releaseType.getReleaseTasks(extracted.get(isApp)))
        s.copy(remainingCommands = pubTasks ++ s.remainingCommands)
    } else {
      state.fail
    }
  }
}
