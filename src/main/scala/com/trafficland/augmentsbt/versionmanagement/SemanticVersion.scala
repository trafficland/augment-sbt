package com.trafficland.augmentsbt.versionmanagement

import SemanticVersionConstants._

object SemanticVersionConstants {

  lazy val SNAPSHOT = "SNAPSHOT"
  lazy val HYPHENSNAPSHOT: String = "-%s".format(SNAPSHOT)

}

trait Snapshotable {

  def isSnapshot(version:String): Boolean = {
    version.matches("""^(\d+\.){2}\d+(-\d{8}-\d{6})$""") ||
      version.matches("""^(\d+\.){2}\d+(%s)$""".format(HYPHENSNAPSHOT))
  }

}

object SemanticVersion extends Snapshotable {

  def stripSnapshot(originalVersion:String): String = {
    if (!isSnapshot(originalVersion)) throw InvalidSnapshotVersionFormatException(originalVersion)

    if (originalVersion endsWith HYPHENSNAPSHOT) {
      originalVersion.replaceAll(HYPHENSNAPSHOT, "")
    } else {
      originalVersion.replaceAll("""(-\d{8}-\d{6})""", "")
    }
  }

  def toVersion(originalVersion:String) : SemanticVersion = {
    require(originalVersion.length > 0, "version string must be greater than 0 characters in length")
    require(originalVersion.matches("^(.+)\\.(.+)\\.(.+)"), s"""version string must contain the pattern ^(.+)\\.(.+)\\.(.+) (version string is: "$originalVersion")""")

    if (isSnapshot(originalVersion)) {
      originalVersion.split("\\.") match {
        case Array(major, minor, patch) =>
          val patchAndSnapshot = patch.split("-", 2)
          SemanticVersion(Integer.parseInt(major),
            Integer.parseInt(minor),
            Integer.parseInt(patchAndSnapshot(0)),
            patchAndSnapshot(1)
          )
      }
    } else {
      originalVersion.split("\\.") match {
        case Array(major, minor, patch) =>
          SemanticVersion(Integer.parseInt(major),
            Integer.parseInt(minor),
            Integer.parseInt(patch),
            ""
          )
      }
    }
  }
}

case class SemanticVersion(major: Int, minor: Int, patch: Int, snapshot:String = SNAPSHOT) extends Snapshotable {

  def incPatch() = Some(copy(patch = patch + 1))

  def incMinor() = Some(copy(minor = minor + 1, patch = 0))

  def incMajor() = Some(copy(major = major + 1, minor = 0, patch = 0))

  def toFinal: SemanticVersion = {
    copy(snapshot = "")
  }

  def toSnapshot: SemanticVersion = copy(snapshot = SNAPSHOT)

  override def toString: String = {
    val versionAsString = "%s.%s.%s".format(major, minor, patch)
    if (snapshot.isEmpty) {
      versionAsString
    } else {
      versionAsString + HYPHENSNAPSHOT
    }
  }

  def toReleaseFormat: String = {
    if (isSnapshot) {
      if (snapshot == SNAPSHOT) {
        import java.{util => ju}
        val sf = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")
        sf.setTimeZone(ju.TimeZone.getTimeZone("UTC"))
        "%s.%s.%s-%s".format(major, minor, patch, sf.format(new ju.Date()))
      } else {
        "%s.%s.%s-%s".format(major, minor, patch, snapshot)
      }
    } else {
      toString()
    }
  }

  def isSnapshot : Boolean = isSnapshot(this.toString())
}
