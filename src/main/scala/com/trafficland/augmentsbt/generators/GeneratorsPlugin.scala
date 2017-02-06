package com.trafficland.augmentsbt.generators

import sbt._

object GeneratorsPlugin extends AutoPlugin {
  override lazy val requires: Plugins =
    AppInfoPlugin &&
    BuildInfoPlugin &&
    LogbackConfigurationPlugin
}
