package com.trafficland.augmentsbt.debug

import com.trafficland.augmentsbt.debug.Keys._
import sbt._
import sbt.Keys._

import scala.util.Try

object DebugPlugin extends AutoPlugin {
  private def settings = Seq(
    debugPort := Try(System.getProperty("augment.debug.port").toInt).toOption,
    debugWait := Try(System.getProperty("augment.debug.wait").toBoolean).getOrElse(false),
    javaOptions <++= (debugPort, debugWait) map { (maybePort, wait) =>
      maybePort.map { port =>
        val waitFlag = if (wait) 'y' else 'n'
        s"-agentlib:jdwp=transport=dt_socket,server=y,suspend=$waitFlag,address=$port"
      }.toSeq
    }
  )

  override def projectSettings: Seq[Def.Setting[_]] = {
    Project.inTask(run)(settings)
    Project.inConfig(Test)(settings) ++
    Project.inConfig(IntegrationTest)(settings)
  }
}
