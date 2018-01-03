import sbt._
import scala.sys.process._

object KeyGenerator {

    val generateKeysObject: TaskKey[Seq[File]] = TaskKey[Seq[File]]("generate-keys-object", "Generates the Keys Object which aliases all the plugins' keys in one object.")
    val keysFile: SettingKey[File] = SettingKey[File]("keys-file", "Keys file to generate")

    def writeKeysObject(targetFile: File): Seq[File] = {
      targetFile.getParentFile.mkdirs()
      IO.write(targetFile, "bin/generate-keys".!!)
      Seq(targetFile)
    }
}