package com.trafficland.augmentsbt.generators

import sbt._
import Keys._
import java.io.FileWriter
import java.util.{Date, Properties}
import java.text.SimpleDateFormat

import com.trafficland.augmentsbt.git.GitPlugin
import GitPlugin.autoImport._
import com.trafficland.augmentsbt.utils.SourceGenerator._
import sbt.plugins.JvmPlugin

/* based on Twitter's BuildProperties:
   https://github.com/twitter/sbt-package-dist/blob/master/src/main/scala/com/twitter/sbt/BuildProperties.scala
   adapted for TrafficLand's plugins and workflow
*/
object BuildInfoPlugin extends AutoPlugin {
  import autoImport._

  override lazy val requires: Plugins = GitPlugin && JvmPlugin

  object autoImport {
    val buildInfoPropertiesFileName: SettingKey[String] = SettingKey[String](
      "build-info-properties-file-name",
      "filename used to build the buildInfoPropertiesFile setting"
    )

    val buildInfoClassFileName = "BuildInfo.scala"

    val buildInfoPropertiesWrite: TaskKey[Seq[File]] = TaskKey[Seq[File]](
      "build-info-properties-write",
      "writes various build properties to a file in managed resources"
    )

    val buildInfoPropertiesFile: SettingKey[File] = SettingKey[File](
      "build-info-properties-file",
      "path to the buildinfo.properties file (will end up as a child under managed resources)"
    )

    val generateBuildInfoClass: TaskKey[Seq[File]] = TaskKey[Seq[File]](
      "generate-build-info-class",
      "generates the BuildInfo.scala file"
    )
  }

  override lazy val projectSettings = Seq(
    buildInfoPropertiesFileName <<= name { n => s"$n-buildinfo.properties" },
    buildInfoPropertiesFile <<= (resourceManaged in Compile, buildInfoPropertiesFileName) { (d, fn) => new File(d, fn) },
    buildInfoPropertiesWrite <<= (
      streams,
      buildInfoPropertiesFile,
      gitHeadCommitSha,
      gitBranchName,
      gitLastCommits
    ) map { (out, bf, sha, branch, commits) =>
      out.log.info(s"Writing build properties to $bf")
      writeBuildProperties(System.currentTimeMillis, sha, branch, commits, bf)
    },
    resourceGenerators in Compile <+= buildInfoPropertiesWrite,
    generateBuildInfoClass <<= (streams, organization, normalizedName, baseDirectory, buildInfoPropertiesFile) map { (out, org, name, dir, file) =>
      val generated = fromResourceTemplate(s"$buildInfoClassFileName.template", org, name)(dir / "src", buildInfoClassFileName)(
        Seq[String => String](
          _.replace("{PACKAGE}", s"$org.${sanitizeName(name)}"),
          _.replace("{BUILDINFOPROPERTIES}", file.getName)
        )
      )
      generated.foreach(f => out.log.info(s"Generated $f"))
      generated
} )

  def writeBuildProperties(timestamp: Long,
                           currentRevision: Option[String],
                           branchName: Option[String],
                           lastFewCommits: Option[Seq[String]],
                           targetFile: File): Seq[File] = {
    targetFile.getParentFile.mkdirs()

    val buildProperties = new Properties
    buildProperties.setProperty("timestamp", new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(timestamp)))
    currentRevision.foreach(buildProperties.setProperty("revision", _))
    branchName.foreach(buildProperties.setProperty("branch", _))
    lastFewCommits.foreach { commits =>
      buildProperties.setProperty("commit_summary", commits.mkString("\n"))
    }

    val fileWriter = new FileWriter(targetFile)
    buildProperties.store(fileWriter, "")
    fileWriter.close()
    Seq(targetFile)
  }


}
