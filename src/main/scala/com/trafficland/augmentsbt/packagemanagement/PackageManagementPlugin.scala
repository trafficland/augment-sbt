package com.trafficland.augmentsbt.packagemanagement

import sbt._
import Keys._

object PackageManagementPlugin extends AutoPlugin {

  override lazy val projectSettings = Seq(
    artifactName := { (_: ScalaVersion, _: ModuleID, artifact: Artifact) =>
      artifact.name + "-" + artifact.`type` + "." + artifact.extension
    },
    artifact in packageBin in Compile <<= (artifact in packageBin in Compile, version) apply ( (old, ver) => {
      val newName = old.name + "-" + ver
      Artifact(newName, old.`type`, old.extension, old.classifier, old.configurations, old.url)
    }),
    artifact in packageSrc in Compile <<= (artifact in packageSrc in Compile, version) apply ( (old, ver) => {
      val newName = old.name + "-" + ver
      Artifact(newName, old.`type`, old.extension, old.classifier, old.configurations, old.url)
    }),
    artifact in packageDoc in Compile <<= (artifact in packageDoc in Compile, version) apply ( (old, ver) => {
      val newName = old.name + "-" + ver
      Artifact(newName, old.`type`, old.extension, old.classifier, old.configurations, old.url)
    })
  )
}