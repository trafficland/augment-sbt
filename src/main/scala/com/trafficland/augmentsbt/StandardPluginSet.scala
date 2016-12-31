package com.trafficland.augmentsbt

import sbt._
import com.trafficland.augmentsbt.generators.GeneratorsPlugin
import com.trafficland.augmentsbt.packagemanagement.PackageManagementPlugin
import com.trafficland.augmentsbt.releasemanagement.ReleaseManagementPlugin
import com.trafficland.augmentsbt.scalaconfiguration.ScalaConfigurationPlugin

object StandardPluginSet extends AutoPlugin {
  override def requires: Plugins = PackageManagementPlugin &&
    ReleaseManagementPlugin &&
    ScalaConfigurationPlugin &&
    GeneratorsPlugin
}
