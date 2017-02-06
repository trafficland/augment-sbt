package com.trafficland.augmentsbt.generators

import sbt.Keys
import java.io.File
import com.trafficland.augmentsbt.utils.SourceGenerator._

trait FileGenerator {

  def generate(out: Keys.TaskStreams, templateFileName: String, modifications: Seq[String => String], targetFile: File): Seq[File] = {
    targetFile.getParentFile.mkdirs()
    val generated = fromResourceTemplate(templateFileName)(targetFile)(modifications)
    generated.foreach(f => out.log.info(s"Generated $f"))
    generated
  }

  protected def normalizedNameModification(normalizedName: String): Seq[String => String] = {
    Seq[String => String](_.replace("{NAME}", normalizedName))
  }
}
