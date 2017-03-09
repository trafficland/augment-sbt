package com.trafficland.augmentsbt.testing

import sbt._
import sbt.Keys.{sourceDirectory, unmanagedSourceDirectories}
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Configuration, Def, Defaults, IntegrationTest, Test, inConfig}

object TestPlugin extends AutoPlugin {
  import autoImport._

  override def requires: Plugins = JvmPlugin

  object autoImport {
    val TestCommon: Configuration = config("testcommon").extend(Test)
  }

  override def projectConfigurations: Seq[Configuration] = Seq(IntegrationTest)

  override def projectSettings: Seq[Def.Setting[_]] =
    Defaults.itSettings ++
    Seq(
      sourceDirectory in TestCommon := (sourceDirectory in Test).value.getParentFile / "testcommon",
      sourceDirectory in IntegrationTest := (sourceDirectory in Test).value.getParentFile / "it"
    ) ++
    inConfig(IntegrationTest)(testCommonSettings) ++
    inConfig(Test)(testCommonSettings)

  lazy val testCommonSettings = Seq(
    unmanagedSourceDirectories ++= unmanagedSourceDirectories.value.map(rebase(sourceDirectory.value, (sourceDirectory in TestCommon).value)).flatten
  )
}
