package com.trafficland.augmentsbt.tests.versionmanagement

import com.trafficland.augmentsbt.versionmanagement.{SemanticVersion, VersionManagementPlugin}
import org.scalatest.{Matchers, WordSpec}

class VersionManagementPluginUnitSpec extends WordSpec with Matchers {

  protected val snapshotVersionOfInterest = "1.0.0-SNAPSHOT"
  protected val snapshotReleaseVersionOfInterest = "1.0.0-20130611-133756"
  protected val finalReleaseVersionOfInterest = "1.0.0"

  protected val snapshotSemanticVersionOfInterest: SemanticVersion = SemanticVersion.toVersion(snapshotVersionOfInterest)
  protected val snapshotReleaseSemanticVersionOfInterest: SemanticVersion = SemanticVersion.toVersion(snapshotReleaseVersionOfInterest)
  protected val finalReleaseSemanticVersionOfInterest: SemanticVersion = SemanticVersion.toVersion(finalReleaseVersionOfInterest)

  protected val validSnapshotAppVersionLine: String = """val appVersion = "%s".toReleaseFormat""".format(snapshotVersionOfInterest)
  protected val invalidSnapshotAppVersionLine: String = """val appVersion = "2.0.0-SNAPSHOT".toReleaseFormat"""

  protected val validSnapshotReleaseAppVersionLine: String = """val appVersion = "%s".toReleaseFormat""".format(snapshotReleaseVersionOfInterest)
  protected val invalidSnapshotReleaseAppVersionLine: String = """val appVersion = "1.0.0-20130611-999999".toReleaseFormat"""
  protected val validFinalReleaseAppVersionLine: String = """val appVersion = "%s".toReleaseFormat""".format(finalReleaseVersionOfInterest)
  protected val invalidFinalReleaseAppVersionLine: String = """val appVersion = "2.0.0".toReleaseFormat"""

  protected val validSnapshotVersionLine: String = """version       := "%s".toReleaseFormat,""".format(snapshotVersionOfInterest)
  protected val invalidSnapshotVersionLine: String = """version       := "2.0.0-SNAPSHOT".toReleaseFormat,"""

  protected val validSnapshotReleaseVersionLine: String = """version       := "%s".toReleaseFormat,""".format(snapshotReleaseVersionOfInterest)
  protected val invalidSnapshotReleaseVersionLine: String = """version       := "1.0.0-20130611-999999".toReleaseFormat,"""
  protected val validFinalReleaseVersionLine: String = """version       := "%s".toReleaseFormat,""".format(finalReleaseVersionOfInterest)
  protected val invalidFinalReleaseVersionLine: String = """version       := "2.0.0".toReleaseFormat,"""

  protected val invalidLine: String = """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))"""

  protected def searchForVersionPattern(line:String, versionOfInterest:SemanticVersion): Option[String] = VersionManagementPlugin.searchForVersionPattern(line, versionOfInterest)
  protected def transformLines(originalSequenceOfLines:Seq[String], originalVersion:SemanticVersion, newVersionFormatToWrite:String) : Seq[String] = VersionManagementPlugin.transformLines(originalSequenceOfLines, originalVersion, newVersionFormatToWrite)
  
  "searchForVersionPattern" should {
      
    "return an Option with the version format if the line includes version := and the snapshot version of interest." in {
      searchForVersionPattern(validSnapshotVersionLine, snapshotSemanticVersionOfInterest) should be (Some(snapshotVersionOfInterest))
    }

    "return an Option with the version format if the line includes version := and the snapshot release version of interest." in {
      searchForVersionPattern(validSnapshotReleaseVersionLine, snapshotReleaseSemanticVersionOfInterest) should be (Some(snapshotReleaseVersionOfInterest))
    }

    "return an Option with the version format if the line includes version := and the final release version of interest." in {
      searchForVersionPattern(validFinalReleaseVersionLine, finalReleaseSemanticVersionOfInterest) should be (Some(finalReleaseVersionOfInterest))
    }

    "return an Option with the version format if the line includes appVersion := and the snapshot version of interest." in {
      searchForVersionPattern(validSnapshotAppVersionLine, snapshotSemanticVersionOfInterest) should be (Some(snapshotVersionOfInterest))
    }

    "return an Option with the version format if the line includes appVersion := and the snapshot release version of interest." in {
      searchForVersionPattern(validSnapshotReleaseAppVersionLine, snapshotReleaseSemanticVersionOfInterest) should be (Some(snapshotReleaseVersionOfInterest))
    }

    "return an Option with the version format if the line includes appVersion := and the final release version of interest." in {
      searchForVersionPattern(validFinalReleaseAppVersionLine, finalReleaseSemanticVersionOfInterest) should be (Some(finalReleaseVersionOfInterest))
    }

    "return None if the line does not include appVersion = or version :=." in {
      searchForVersionPattern(invalidLine, snapshotSemanticVersionOfInterest) should be (None)
    }
    
    "return None if the appVersion line does not contain the snapshot version of interest." in {
      searchForVersionPattern(invalidSnapshotAppVersionLine, snapshotSemanticVersionOfInterest) should be (None)
    }

    "return None if the appVersion line does not contain the snapshot release version of interest." in {
      searchForVersionPattern(invalidSnapshotReleaseAppVersionLine, snapshotReleaseSemanticVersionOfInterest) should be (None)
    }

    "return None if the appVersion line does not contain the final release version of interest." in {
      searchForVersionPattern(invalidFinalReleaseAppVersionLine, finalReleaseSemanticVersionOfInterest) should be (None)
    }

    "return None if the version line does not contain the snapshot version of interest." in {
      searchForVersionPattern(invalidSnapshotVersionLine, snapshotSemanticVersionOfInterest) should be (None)
    }

    "return None if the version line does not contain the snapshot release version of interest." in {
      searchForVersionPattern(invalidSnapshotVersionLine, snapshotReleaseSemanticVersionOfInterest) should be (None)
    }

    "return None if the version line does not contain the final release version of interest." in {
      searchForVersionPattern(invalidFinalReleaseVersionLine, finalReleaseSemanticVersionOfInterest) should be (None)
    }
  }

  "transformLines" should {

    "return a sequence of lines in which the version line has been transformed when it has the snapshot version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format(snapshotVersionOfInterest),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotSemanticVersionOfInterest, snapshotReleaseVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "isApp := false,",
          """name := "tl-sbt-plugins",""",
          "sbtPlugin := true,",
          """version       := "%s".toReleaseFormat(),""".format(snapshotReleaseVersionOfInterest),
          """scalaVersion := "2.9.2",""",
          """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
          """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
        )
      )
    }

    "return a sequence of lines in which the appVersion line has been transformed when it has the snapshot version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format(snapshotVersionOfInterest),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotSemanticVersionOfInterest, snapshotReleaseVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "object VQMBuild extends Build {",
          "import Dependencies._",
          "",
          """val appVersion = "%s".toReleaseFormat""".format(snapshotReleaseVersionOfInterest),
          "",
          """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
          ".settings(Play20PluginSet.plugs : _*)",
          ".configs( DatabaseTests )",
          ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
        )
      )
    }

    "return a sequence of lines in which the version line has been transformed when it has the snapshot release version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format(snapshotReleaseVersionOfInterest),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "isApp := false,",
          """name := "tl-sbt-plugins",""",
          "sbtPlugin := true,",
          """version       := "%s".toReleaseFormat(),""".format(snapshotVersionOfInterest),
          """scalaVersion := "2.9.2",""",
          """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
          """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
        )
      )
    }

    "return a sequence of lines in which the appVersion line has been transformed when it has the snapshot release version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format(snapshotReleaseVersionOfInterest),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "object VQMBuild extends Build {",
          "import Dependencies._",
          "",
          """val appVersion = "%s".toReleaseFormat""".format(snapshotVersionOfInterest),
          "",
          """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
          ".settings(Play20PluginSet.plugs : _*)",
          ".configs( DatabaseTests )",
          ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
        )
      )
    }

    "return a sequence of lines in which the version line has been transformed when it has the final release version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format(finalReleaseVersionOfInterest),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, finalReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "isApp := false,",
          """name := "tl-sbt-plugins",""",
          "sbtPlugin := true,",
          """version       := "%s".toReleaseFormat(),""".format(snapshotVersionOfInterest),
          """scalaVersion := "2.9.2",""",
          """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
          """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
        )
      )
    }

    "return a sequence of lines in which the appVersion line has been transformed when it has the final release version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format(finalReleaseVersionOfInterest),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, finalReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(
        Seq(
          "object VQMBuild extends Build {",
          "import Dependencies._",
          "",
          """val appVersion = "%s".toReleaseFormat""".format(snapshotVersionOfInterest),
          "",
          """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
          ".settings(Play20PluginSet.plugs : _*)",
          ".configs( DatabaseTests )",
          ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
        )
      )
    }

    "return the exact same sequence of lines presented when the version line does not have snapshot version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format("2.0.0-SNAPSHOT"),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotSemanticVersionOfInterest, snapshotReleaseVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

    "return the exact same sequence of lines presented when the app version line does not have snapshot version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format("2.0.0-SNAPSHOT"),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotSemanticVersionOfInterest, snapshotReleaseVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

    "return the exact same sequence of lines presented when the version line does not have snapshot release version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format("2.0.0-20130611-999999"),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

    "return the exact same sequence of lines presented when the app version line does not have snapshot release version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format("2.0.0-20130611-999999"),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, snapshotReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

    "return the exact same sequence of lines presented when the version line does not have final release version of interest." in {
      val lines = Seq(
        "isApp := false,",
        """name := "tl-sbt-plugins",""",
        "sbtPlugin := true,",
        """version       := "%s".toReleaseFormat(),""".format("2.0.0"),
        """scalaVersion := "2.9.2",""",
        """scalacOptions := Seq("-deprecation", "-encoding", "utf8"),""",
        """resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/","""
      )

      val transformedSequenceOfLines = transformLines(lines, finalReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

    "return the exact same sequence of lines presented when the app version line does not have final release version of interest." in {
      val lines = Seq(
        "object VQMBuild extends Build {",
        "import Dependencies._",
        "",
        """val appVersion = "%s".toReleaseFormat""".format("2.0.0"),
        "",
        """lazy val main = play.Project("vqmdb", appVersion, path = file("vqmdb"))""",
        ".settings(Play20PluginSet.plugs : _*)",
        ".configs( DatabaseTests )",
        ".settings( inConfig(DatabaseTests)(Defaults.testTasks) : _* )"
      )

      val transformedSequenceOfLines = transformLines(lines, finalReleaseSemanticVersionOfInterest, snapshotVersionOfInterest)

      transformedSequenceOfLines should equal(lines)
    }

  }
}

