package com.trafficland.augmentsbt.releasemanagement

import sbt._
import Keys._
import com.trafficland.augmentsbt.git.GitPlugin
import sbt.complete.Parser
import org.eclipse.jgit.lib.Repository
import com.trafficland.augmentsbt.git.GitPlugin.RemoteBranch
import com.trafficland.augmentsbt.versionmanagement.{SemanticVersion, VersionManagementPlugin}
import com.trafficland.augmentsbt.AugmentSBTKeys._

import scala.language.postfixOps
import scala.util.matching.Regex

object ReleaseManagementPlugin extends AutoPlugin {

  object autoImport extends ReleaseManagementKeys
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

    releaseReady := {
      val finalVersion = version.value.toFinal.toReleaseFormat
      streams.value.log.info(s"stable version $finalVersion")
      streams.value.log.debug(s"remoteGitRepoPatterns = ${remoteGitRepoPatterns.value}.")

      val masterRemote: Option[RemoteBranch] = {
        for {
          master <- gitDescribeBranches.value.find { case (branchName, _) => branchName == "master" }
          masterRemote <- master._2
        } yield masterRemote
      }
      val masterOk = masterRemote.exists(r => r.branch == "master" && remoteRepoCorrect(remoteGitRepoPatterns.value, r.remoteUri))
      val developOk = gitTrackingBranch.value.exists(r => r.branch == "develop" && masterRemote.exists(_.remoteUri == r.remoteUri))

      // we don't release dirty trees
      val allTags = gitShowAllTags.value
      val log = streams.value.log
      if (!gitIsCleanWorkingTree.value) {
        log.error("Working directory is not clean.")
        false
      }
      // we don't double-release
      else if (allTags.map(Repository.shortenRefName).exists(_.endsWith(finalVersion))) {
        log.error("Cannot tag release version %s: tag already exists.".format(finalVersion))
        false
      }
      else if (!masterOk) {
        log.error(s"The local master branch must track master on a remote matching $masterRemote")
        false
      }
      else if (!developOk) {
        val trackingBranchDisplay = gitTrackingBranch.value.map(t => s"${t.name}:${t.branch}").getOrElse("{untracked}")
        log.error(s"This branch must track develop on the same remote as master, is tracking $trackingBranchDisplay instead")
        false
      } else {
        log.info("Current project is ok for release.")
        true
      }
    }
  )

  def remoteRepoCorrect(remoteRepoPatterns: Seq[Regex], repoURL: String): Boolean =
    remoteRepoPatterns.exists( pattern => pattern.unapplySeq(repoURL).isDefined )



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
        val pubTasks = extracted.get(releaseType.getReleaseTasks(extracted.get(isApp))).map(Exec(_, s.source)).toList
        s.copy(remainingCommands = pubTasks ++ s.remainingCommands)
    } else {
      state.fail
    }
  }
}
