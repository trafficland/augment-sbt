package com.trafficland.augmentsbt.distribute

import sbt._

object SelfDistributor {

  val distSelf: Command = Command.command("distSelf",
    "Compiles and packages the tl-sbt-plugins jar and then places it in project/lib",
    "Compiles and packages the tl-sbt-plugins jar and then places it in project/lib so the plugin itself can make use of its own functionality")
  { (state: State) =>
    val extracted = Project.extract(state)
    val pluginName = extracted.get(sbt.Keys.name)
    val pluginVersion = extracted.get(sbt.Keys.version)
    val libDir = file("project") / "lib"
    IO.delete(IO.listFiles(libDir, FileFilter.globFilter(s"$pluginName*jar")))
    IO.createDirectory(libDir)
    extracted.runTask(sbt.Keys.`package` in Compile, state) match {
      case (_, pkg) =>
        // no version name for this jar to keep source control happy without requiring to use --force
        // and remembering every time changes are made
        val fileName = s"$pluginName-self-referencing-$pluginVersion.jar"
        IO.move(pkg, libDir / fileName)
        state.log.info(s"Moved jar file to project/lib/$fileName")
      case _ =>
        state.fail
    }
    state
  }
}