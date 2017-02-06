package com.trafficland.augmentsbt.tests.releasemanagement

import com.trafficland.augmentsbt.releasemanagement.ReleaseManagementPlugin
import org.scalatest.WordSpec
import org.scalatest.Matchers._

class ReleaseManagementPluginUnitSpec extends WordSpec {

  "ReleaseManagementPlugin.remoteRepoCorrect" should {

    "return true for a HTTPS formatted github URL that is specified." in {
      val regexes = Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r)

      ReleaseManagementPlugin.remoteRepoCorrect(regexes, "https://github.com/trafficland/augment_sbt.git") should be (true)
    }

    "return true for a GIT formatted github URL that is specified." in {
      val regexes = Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r)

      ReleaseManagementPlugin.remoteRepoCorrect(regexes, "git@github.com:trafficland/augment_sbt.git") should be (true)
    }

    "return false for a GIT formatted github URL that isn't specified." in {
      val regexes = Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r)

      ReleaseManagementPlugin.remoteRepoCorrect(regexes, "git@github.com:somegithubname/augment_sbt.git") should be (false)
    }

    "return false for a HTTPS formatted github URL that isn't specified." in {
      val regexes = Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r)

      ReleaseManagementPlugin.remoteRepoCorrect(regexes, "https://github.com/somegithubname/augment_sbt.git") should be (false)
    }
  }
}