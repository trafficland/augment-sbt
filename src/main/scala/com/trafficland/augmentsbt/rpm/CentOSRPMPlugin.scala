package com.trafficland.augmentsbt.rpm

import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.SbtNativePackager.Rpm
import com.trafficland.augmentsbt.distribute.StartupScriptPlugin

import scala.collection.Seq
import com.trafficland.augmentsbt.AugmentSBTKeys._
import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader

object CentOSRPMPlugin extends AutoPlugin {

  override def requires: Plugins = RPMPlugin && StartupScriptPlugin

  object autoImport extends CentOSRPMKeys
  import autoImport._

  override def projectSettings = Seq(
    scriptsDirectory := baseDirectory.value / "scripts" ,
    defaultLinuxInstallLocation := vendorDirectory.value,
    rpmBrpJavaRepackJars := true, // Upstream issue: Setting this to true disables repacking of jars, contrary to its name
    serverLoading in Rpm := Some(ServerLoader.Systemd)
  )
}
