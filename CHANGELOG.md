# Change Log

### 1.0.1

+ Added support for cross builds to the release tasks. Specifically, the publish tasks will publish all cross builds now.

### 1.0.0

+ Update SBT to 0.13.12
+ Move to AutoPlugins
+ Remove Play plugin as dependency. Projects must include the version they want manually now.
+ Git plugin uses JGit instead of shelling out to Git shell. Requires openssh keyagent.
+ releaseFinal takes the intended release version as an argument and validates it against the version in the SBT build definition.
+ Stability and sanity check improvements to release process.
+ Use more of sbt native packager for building RPMs.
+ RPMs work with CentOS 7 & systemd.
+ Initial commit of augmentsbt. Includes work from the TrafficLand private SBT plugins.