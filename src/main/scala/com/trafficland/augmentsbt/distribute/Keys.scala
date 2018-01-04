package com.trafficland.augmentsbt.distribute

import sbt.SettingKey

trait DistributeKeys {
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
