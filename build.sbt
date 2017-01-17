import sbt._
import KeyGenerator._
import BuildCommon._
import com.trafficland.augmentsbt.distribute.SelfDistributor.distSelf
import com.trafficland.augmentsbt.AugmentSBTKeys.remoteGitRepoPatterns

lazy val trafficlandSbtPluginProject = Project(pluginName, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    isApp                 := false,
    version               := libVersion,
    organization          := "com.trafficland",
    organizationName      := "Trafficland, Inc.",
    sbtPlugin             := true,
    scalaVersion          := "2.10.6",
    scalacOptions         := Seq("-deprecation", "-feature", "-encoding", "utf8"),
    resolvers             += "Typesafe Maven Releases" at "http://repo.typesafe.com/typesafe/releases/",
    remoteGitRepoPatterns ++= Seq("""^git@github.com:ereichert/.*\.git""".r, """^https://github.com/ereichert/.*\.git""".r),
    libraryDependencies   ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.6.0.201612231935-r",
      "com.jcraft" % "jsch.agentproxy.sshagent" % "0.0.9",
      "com.jcraft" % "jsch.agentproxy.usocket-jna" % "0.0.9"
    ),
    commands += distSelf,
    keysFile <<= (resourceManaged in Compile)(new File(_, "AugmentSBTKeys.scala")),
    generateKeysObject <<= (streams, keysFile) map { (out, targetFile) =>
      out.log.info(s"Generating $targetFile")
      out.log.info(s"keysFile: $keysFile")
      writeKeysObject(targetFile)
    },
    sourceGenerators in Compile <+= generateKeysObject,
    addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.0")
  )