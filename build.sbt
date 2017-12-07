import sbt._
import KeyGenerator._
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
    publishMavenStyle                     := false,
    bintrayRepository                     := "sbt-plugins",
    bintrayOrganization                   := Some("trafficland"),
    scalaVersion                          := "2.10.6",
    scalacOptions                         := Seq("-deprecation", "-feature", "-encoding", "utf8"),
    resolvers                             += "Typesafe Maven Releases" at "http://repo.typesafe.com/typesafe/releases/",
    remoteGitRepoPatterns                 ++= Seq("""^git@github.com:trafficland/.*\.git""".r, """^https://github.com/trafficland/.*\.git""".r),
    libraryDependencies                   ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.6.0.201612231935-r",
      "com.jcraft" % "jsch.agentproxy.sshagent" % "0.0.9",
      "com.jcraft" % "jsch.agentproxy.usocket-jna" % "0.0.9",
      "org.eclipse.jgit" % "org.eclipse.jgit.pgm" % "4.5.0.201609210915-r" exclude("javax.jms", "jms")
                                                                           exclude("com.sun.jdmk", "jmxtools")
                                                                           exclude("com.sun.jmx", "jmxri")
    ),
    commands                              += distSelf,
    keysFile                              <<= (sourceDirectory in Compile)(new File(_, "scala/com/trafficland/augmentsbt/AugmentSBTKeys.scala")),
    generateKeysObject                    <<= (streams, keysFile) map { (out, targetFile) =>
      out.log.info(s"Generating $targetFile")
      out.log.info(s"keysFile: $keysFile")
      writeKeysObject(targetFile)
    },
    sourceGenerators in Compile           <+= generateKeysObject,
    addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.0")
  )
