package com.trafficland.augmentsbt.play20

import com.typesafe.sbt.packager.Keys._
import sbt._
import com.trafficland.augmentsbt.distribute.StartupScriptPlugin
import StartupScriptPlugin.autoImport._

object Play20Plugin extends AutoPlugin {

  override def requires = StartupScriptPlugin

  override val projectSettings = Seq(
    bashScriptExtraDefines += "addJava -Dplay.server.pidfile.path=/dev/null",
    bashScriptExtraDefines ++= loggingConfigFileName.value.map { loggerConfig =>
        s"addJava -Dlogger.file=conf/$loggerConfig" :: Nil
      }.getOrElse(Nil)
  )
}