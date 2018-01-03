package com.trafficland.augmentsbt.distribute

import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport.{Linux, daemonUser}
import com.typesafe.sbt.packager.rpm.RpmPlugin.autoImport.{rpmDaemonLogFile, rpmVendor}
import sbt._
import com.trafficland.augmentsbt.rpm.RPMPlugin

object StartupScriptPlugin extends AutoPlugin {
  import autoImport._

  override def requires = RPMPlugin

  object autoImport {
    val startScriptMainArguments: SettingKey[Seq[String]] = SettingKey[Seq[String]](
      "start-script-main-arguments",
      "arguments passed to the main class")

    val startScriptJavaOptions: SettingKey[Seq[String]] = SettingKey[Seq[String]](
      "start-script-java-options",
      "option pairs for the java executable (with -D flag if necessary, i.e., \"-Dsome.value=1337\")")

    val startScriptConfigFileName: SettingKey[String] = SettingKey[String](
      "start-script-config-file-name",
      "configuration file name passed as -Dconfig.file system setting")

    val loggingConfigFileName: SettingKey[Option[String]] = SettingKey[Option[String]](
      "logging-config-file",
      "Logback configuration file in the conf directory")
  }

  override lazy val projectSettings = Seq(
    startScriptJavaOptions := Seq.empty,
    startScriptMainArguments := Seq.empty,
    daemonUser in Linux := "coreservices",
    daemonStdoutLogFile := Some("stdout.log"),
    defaultLinuxLogsLocation := s"/var/log/${rpmVendor.value}",
    startScriptConfigFileName := "prod.conf",
    loggingConfigFileName := Some("logback.xml"),
    executableScriptName := "start",
    bashScriptExtraDefines ++= {
      val loggingArgOpt = loggingConfigFileName.value.map { log =>
        s"-Dlogback.configurationFile=$$app_home/../conf/$log"
      }
      val configArg = s"-Dconfig.file=$$app_home/../conf/${startScriptConfigFileName.value}"
      val javaArgs = startScriptJavaOptions.value ++ loggingArgOpt :+ configArg

      val addJavaArgs = javaArgs.map(arg => s"addJava $arg")
      val addMainArgs = startScriptMainArguments.value.map(arg => s"addApp $arg")

      addMainArgs ++ addJavaArgs
    }
  )
}
