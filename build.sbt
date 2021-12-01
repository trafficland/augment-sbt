import sbt._
import BuildCommon._
import com.trafficland.augmentsbt.distribute.SelfDistributor.distSelf
import com.trafficland.augmentsbt.AugmentSBTKeys.remoteGitRepoPatterns

lazy val trafficlandSbtPluginProject = Project(pluginName, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    isApp                                 := false,
    sbtPlugin                             := true,
    version                               := libVersion,
    organization                          := "com.trafficland",
    organizationName                      := "Trafficland, Inc.",
    description                           := "A set of opinionated SBT plugins for common build and release tasks.",
    licenses                              += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    githubTokenSource                     := TokenSource.GitConfig("github.token"),
    githubRepository                      := "augment-sbt",
    githubOwner                           := "trafficland",
    scalaVersion                          := "2.12.13",
    scalacOptions                         := Seq("-deprecation", "-feature", "-encoding", "utf8"),
    resolvers                             += "Typesafe Maven Releases" at "https://repo.typesafe.com/typesafe/releases/",
    remoteGitRepoPatterns                 ++= Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r),
    libraryDependencies                   ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.4" % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.6.0.201612231935-r",
      "com.jcraft" % "jsch.agentproxy.sshagent" % "0.0.9",
      "com.jcraft" % "jsch.agentproxy.usocket-jna" % "0.0.9",
      "org.eclipse.jgit" % "org.eclipse.jgit.pgm" % "4.5.0.201609210915-r" exclude("javax.jms", "jms")
                                                                           exclude("com.sun.jdmk", "jmxtools")
                                                                           exclude("com.sun.jmx", "jmxri")
    ),
    commands                              += distSelf,
    addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.6.1"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.4")
  )
