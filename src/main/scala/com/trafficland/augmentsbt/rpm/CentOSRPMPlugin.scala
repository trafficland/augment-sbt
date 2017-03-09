package com.trafficland.augmentsbt.rpm

import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.ServerLoader
import com.typesafe.sbt.SbtNativePackager.Rpm
import com.trafficland.augmentsbt.distribute.StartupScriptPlugin

import scala.collection.Seq
import com.trafficland.augmentsbt.rpm.Keys._

object CentOSRPMPlugin extends AutoPlugin {
  import autoImport._

  override def requires: Plugins = RPMPlugin && StartupScriptPlugin

  object autoImport {
    val scriptsDirectory: SettingKey[File] = SettingKey[File]("scripts-directory")
  }

  override def projectSettings = Seq(
    scriptsDirectory <<= baseDirectory apply { bd => bd / "scripts" },
    defaultLinuxInstallLocation := vendorDirectory.value,
    rpmBrpJavaRepackJars := true, // Upstream issue: Setting this to true disables repacking of jars, contrary to its name
    serverLoading in Rpm := ServerLoader.Systemd
  )
}
