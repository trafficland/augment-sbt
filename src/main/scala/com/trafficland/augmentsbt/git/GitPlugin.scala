package com.trafficland.augmentsbt.git

import java.io.File

import com.jcraft.jsch.agentproxy.{AgentProxyException, Connector}
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{BranchConfig, Repository, RepositoryBuilder}
import org.eclipse.jgit.transport._
import sbt.Keys._
import sbt._
import sbt.complete.Parser
import com.trafficland.augmentsbt.utils.ARM.auto
import com.trafficland.augmentsbt.utils.JGitExtensions.TransportCommandExtensions

import scala.collection.JavaConverters.{asScalaBufferConverter, iterableAsScalaIterableConverter}
import scala.language.postfixOps
import scala.sys.process.Process

object GitPlugin extends AutoPlugin {
  import autoImport._

  val commentParser: Parser[String] = {
    import complete.DefaultParsers._
    any.* map (_.mkString.trim.stripPrefix("\"").stripSuffix("\""))
  }

  case class RemoteBranch(name: String, remoteUri: String, branch: String)
  object RemoteBranch {
    def lookup(repository: Repository, branchName: String): Option[RemoteBranch] = {
      val branchConfig = new BranchConfig(repository.getConfig, branchName)
      val remoteConfig = new RemoteConfig(repository.getConfig, branchConfig.getRemote)
      val maybeRemoteUri = remoteConfig.getPushURIs.asScala.headOption.orElse(remoteConfig.getURIs.asScala.headOption)
      val maybeRemoteTrackingBranch = Option(branchConfig.getRemoteTrackingBranch)
      for {
        remoteUri <- maybeRemoteUri
        remoteTrackingBranch <- maybeRemoteTrackingBranch
        shortRemoteTrackingBranch = repository.shortenRemoteBranchName(remoteTrackingBranch)
      } yield RemoteBranch(branchConfig.getRemote, remoteUri.toString, shortRemoteTrackingBranch)
    }
  }

  override lazy val projectSettings = {
    Seq(
      gitIsRepository := {
        var log = streams.value.log
        withDefaultGitRepo { git =>
          val exists = git.getRepository.getObjectDatabase.exists()
          if (!exists) {
            log.warn("This project is not part of a git repository")
          }
          exists
        }
      },

      gitIsCleanWorkingTree := {
        var log = streams.value.log
        withDefaultGitRepo { git =>
          val isClean = git.status().call().isClean
          if (isClean) {
            log.info("Working tree is clean")
          }
          else
            log.info("Working tree is not clean")
          isClean
        }
      },

      gitBranchName :=
        {
          var log = streams.value.log
          ifRepo(gitIsRepository.value) {
            withDefaultGitRepo { git =>
              val branchName = git.getRepository.getBranch
              log.debug(s"Current branch is $branchName")
              branchName
            }
          }
        },

      gitTrackingBranch := {
        var log = streams.value.log
        ifRepoFlat(gitIsRepository.value) {
          withDefaultGitRepo { git =>
            val repository = git.getRepository
            RemoteBranch.lookup(repository, repository.getBranch)
          }
        }
      },

      gitStatus := {
        var log = streams.value.log
        withDefaultGitRepo { git =>
          val status = git.status().call()
          log.info(
            s"""
               |Added: ${status.getAdded}
               |Changed: ${status.getChanged}
               |Conflicting: ${status.getConflicting}
               |ConflictingStageState: ${status.getConflictingStageState}
               |IgnoredNotInIndex: ${status.getIgnoredNotInIndex}
               |Missing: ${status.getMissing}
               |Modified: ${status.getModified}
               |Removed: ${status.getRemoved}
               |Untracked: ${status.getUntracked}
               |UntrackedFolders: ${status.getUntrackedFolders}
         """.stripMargin
          )
        }
      },

      gitDescribeBranches := {
        withDefaultGitRepo { git =>
          val repository = git.getRepository
          val localBranches = git.branchList().call().asScala
          localBranches.map { ref =>
            val refName = Repository.shortenRefName(ref.getName)
            refName -> RemoteBranch.lookup(repository, refName)
          }.toMap
        }
      },

      gitShowAllTags := withDefaultGitRepo { git =>
        git.tagList().call().asScala.map(_.getName)
      },

      gitTagName := {
        val tagName = "%s-%s".format(name.value, version.value)
        streams.value.log.info("Created tag name %s.".format(tagName))
        tagName
      },

      gitTag := {
        val tagName = gitTagName.value
        streams.value.log.info("Tagging branch %s with tag name %s.".format(gitBranchName.value.get, tagName))
        withDefaultGitRepo { git =>
          val cmd = git.tag()
          cmd.setName(tagName)
          cmd.setMessage(tagName)
          cmd.call()
        }
      },

      gitVersionBumpCommitMessage := {
        val versionBumpMessage = "Version bumped to %s".format(version.value)
        streams.value.log.info(versionBumpMessage)
        versionBumpMessage
      },

      gitVersionBumpCommit := {
        streams.value.log.info("Creating version bump commit for version %s.".format(version.value))
        runGitCommit(gitVersionBumpCommitMessage.value)
      },

      gitReleaseCommitMessage := {
        val releaseMessage = "release commit for %s".format(version.value)
        streams.value.log.info(releaseMessage)
        releaseMessage
      },

      gitReleaseCommit := {
        streams.value.log.info("Creating release commit for version %s.".format(version.value))
        runGitCommit(gitReleaseCommitMessage.value)
      },

      gitCommit := {
        val commentArg = commentParser.parsed
        if (commentArg.isEmpty) throw InvalidCommitMessageException()

        streams.value.log.info(s"""Creating commit with message "$commentArg".""")
        runGitCommit(commentArg)
      },

      gitSshKeyAgent := {
        try {
          val socketFactory = new JNAUSocketFactory()
          val connector = new SSHAgentConnector(socketFactory)
          Some(connector)
        } catch {
          case _: AgentProxyException =>
            sLog.value.warn(
              "Your SSH_AUTH_SOCK environment variable is not set. " +
                "The Git Plugin and tasks that rely on the Git Plugin may not work in this configuration." +
                "See http://blog.joncairns.com/2013/12/understanding-ssh-agent-and-ssh-add/ for more information."
            )
            None
        }
      },

      gitPushOrigin := {
        streams.value.log.info("Pushing commits to remote repository.")
        withDefaultGitRepo { git =>
          val cmd = git.push().setRemote("origin").setPushTags().add("refs/heads/develop").add("refs/heads/master")
          gitSshKeyAgent.value.foreach(cmd.useSshAgent)
          cmd.call()
        }
      },

      aggregate in gitReleaseCommit := false,
      aggregate in gitTag := false,

      gitCheckoutMaster := {
        streams.value.log.info("Checking out master.")
        checkout("master")
      },

      gitCheckoutDevelop := {
        streams.value.log.info("Checking out develop.")
        checkout("develop")
      },

      gitMergeDevelop := {
        streams.value.log.info("Merging develop into master.")
        withDefaultGitRepo { git =>
          val maybeDevelopBranch = git.branchList().call().asScala.find(branch => Repository.shortenRefName(branch.getName) == "develop")
          maybeDevelopBranch.map { developBranch =>
            val cmd = git.merge()
            cmd.include(developBranch)
            cmd.call()
          }.getOrElse(
            sys.error("No local branch named 'develop' exists to merge")
          )
        }
      },

      gitHeadCommitSha := ifRepo(gitIsRepository.value) { headCommitSha },

      gitLastCommitsCount := 10,

      gitLastCommits := ifRepo(gitIsRepository.value) {
        withDefaultGitRepo { git =>
          val cmd = git.log()
          cmd.setMaxCount(gitLastCommitsCount.value)
          val commits = cmd.call()
          commits.asScala.map(commit => s"$commit\t${commit.getAuthorIdent}\t${commit.getShortMessage}").toSeq
        }
      }
    )
  }

  object autoImport {
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

  def runGitCommit(commitMessage: String): Unit = {
    withDefaultGitRepo { git =>
      val cmd = git.commit().setAll(true).setMessage(commitMessage)
      cmd.call()
    }
  }

  def checkout(branch: String): Unit = {
    withDefaultGitRepo { git =>
      val cmd = git.checkout()
      cmd.setName(branch)
      cmd.call()
    }
  }

  def headCommitSha: String = Process("git rev-parse HEAD").lineStream.head

  def withDefaultGitRepo[T](f: Git => T): T = {
    auto(new RepositoryBuilder().setWorkTree(new File(".")).build()).map { repository =>
      f(new Git(repository))
    }
  }

  def ifRepo[T](isRepo: Boolean)(f: => T): Option[T] = {
    ifRepoFlat(isRepo)(Some(f))
  }

  def ifRepoFlat[T](isRepo: Boolean)(f: => Option[T]): Option[T] = {
    if (isRepo) {
      f
    } else {
      None
    }
  }
}
