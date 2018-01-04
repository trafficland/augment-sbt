package com.trafficland.augmentsbt.generators

import sbt._
import sbt.Keys._
import java.io.FileWriter
import java.util.Properties

import com.trafficland.augmentsbt.utils.SourceGenerator._
import sbt.plugins.JvmPlugin

object AppInfoPlugin extends AutoPlugin {

  override def requires: Plugins = JvmPlugin

  object autoImport extends AppInfoKeys
  import autoImport._

  override lazy val projectSettings = Seq(
    appInfoPropertiesFileName := s"${name.value}-appinfo.properties",
    appInfoPropertiesFile := new File((resourceManaged in Compile).value, appInfoPropertiesFileName.value),
    appInfoPropertiesWrite := {
        streams.value.log.info(s"Writing app info properties to ${appInfoPropertiesFile.value}")
        writeAppInfoProperties(appInfoPropertiesFile.value, name.value, version.value, organizationName.value)
    },
    resourceGenerators in Compile += appInfoPropertiesWrite.taskValue,
    generateAppInfoClass := {
      val log = streams.value.log
      val generated = fromResourceTemplate(s"$appInfoClassFileName.template", organization.value, normalizedName.value)(baseDirectory.value / "src", appInfoClassFileName)(
        Seq[String => String](
          _.replace("{PACKAGE}", s"${organization.value}.${sanitizeName(normalizedName.value)}"),
          _.replace("{APPINFOPROPERTIES}", appInfoPropertiesFile.value.getName)
        )
      )
      generated.foreach(f => log.info(s"Generated $f"))
      generated
    }
  )

  def writeAppInfoProperties(targetFile: File, name: String, version: String, organizationName: String): Seq[File] = {
    val appInfoProperties = new Properties
    appInfoProperties.setProperty("name", name)
    appInfoProperties.setProperty("version", version)
    appInfoProperties.setProperty("vendor", organizationName)

    targetFile.getParentFile.mkdirs() /* main directory not necessarily created yet */
    val fileWriter = new FileWriter(targetFile)
    appInfoProperties.store(fileWriter, "")
    fileWriter.close()

    Seq(targetFile)
  }
}
