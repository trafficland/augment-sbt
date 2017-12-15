package com.trafficland.augmentsbt

import sbt._
import com.trafficland.augmentsbt.generators.GeneratorsPlugin
import com.trafficland.augmentsbt.releasemanagement.ReleaseManagementPlugin
import com.trafficland.augmentsbt.scalaconfiguration.ScalaConfigurationPlugin
import com.trafficland.augmentsbt.testing.TestPlugin
import com.typesafe.sbt.packager.archetypes.systemloader.SystemdPlugin

object StandardPluginSet extends AutoPlugin {
  override def requires: Plugins =
    ReleaseManagementPlugin &&
    SystemdPlugin &&
    ScalaConfigurationPlugin &&
    GeneratorsPlugin &&
    TestPlugin
}
