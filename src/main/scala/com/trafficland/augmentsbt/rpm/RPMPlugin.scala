package com.trafficland.augmentsbt.rpm

import com.typesafe.sbt.packager.rpm.{RpmKeys, RpmPlugin}
import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.linux.LinuxSymlink

import scala.collection._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.SbtNativePackager._
import com.trafficland.augmentsbt.rpm.Keys._

object RPMPlugin extends AutoPlugin with RpmKeys {

  override def requires = RpmPlugin

  override lazy val projectSettings =
    Seq(
      name in Rpm <<= name apply { n => n },
      linuxPackageSymlinks <<= (installationDirectory, rpmVendor, name in Rpm) map { (installationDir, v, n) =>
          val vendorName = s"$v/$n"
          Seq(
            LinuxSymlink(s"$installationDir/logs", s"/var/log/$vendorName"),
            LinuxSymlink(s"/etc/$vendorName", s"$installationDir/conf")
          )
      },

      version in Rpm <<= version apply { v => v.replace("-", "") },
      rpmLicense := Some("Proprietary"),
      rpmGroup := Some("Applications/Services"),
      rpmRelease := "1",
      linuxUserAndGroup := ("nobody", "nobody"),
      vendorDirectory <<= rpmVendor apply { rv => "/opt/" + rv },
      installationDirectory <<= (vendorDirectory, destinationDirectory) apply { (vd, dd) => vd + Path.sep + dd },
      destinationDirectory <<= (name in Rpm) apply { n => n }
    )
}