package com.trafficland.augmentsbt.rpm

import com.typesafe.sbt.SbtNativePackager.Universal
import sbt._
import sbt.Keys._
import com.trafficland.augmentsbt.generators.ConfigurationDirectory.autoImport._
import com.trafficland.augmentsbt.play20.Play20Plugin

object CentOSPlayRPMPlugin extends AutoPlugin {
  override def requires: Plugins = CentOSRPMPlugin && Play20Plugin

  override def projectSettings = Seq(
    // conf files are deployed by fabric and should not be included in the RPM
    // ideally, we would manipulate the playExternalizedResources key here however we cannot access it without
    // depending on the play plugin which we're trying to avoid as it would tie the sbt plugin to the play version
    mappings in Universal := (mappings in Universal).value.filterNot { case (src, _) =>
      confDirectory.value.relativize(src).isDefined
    }
  )
}
