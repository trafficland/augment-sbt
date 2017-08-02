package com.trafficland.augmentsbt.debug

import sbt.SettingKey

object Keys {
  val debugPort: SettingKey[Option[Int]] = SettingKey[Option[Int]]("debugPort", "The port for forked JVMs to listen to for debugger connections")
  val debugWait: SettingKey[Boolean] = SettingKey[Boolean]("debugWait", "Whether to wait for a debugger to connect to the forked JVM before executing")
}
