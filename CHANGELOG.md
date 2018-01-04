# Change Log

### 1.2.0
+ Upgrade to SBT 1.0 and Scala 2.12
+ Remove key generation.
+ Move tasks to camelCase.

### 1.1.3
+ Remove unneeded PackageManagementPlugin.

### 1.1.1

+ Fix missing conf directory in PRM.

### 1.1.0

+ Add TestPlugin. Enables IntegrationTest config, adds testcommon shared test code source directory.
+ Fix resource generator plugin dependencies.
+ Move toVersion implicit into VersionManagementPlugin's autoImport so projects no longer need to import it manually.Deprecate the original location.
+ Require StartupScriptPlugin in CentOSRPMPlugin so non-play projects get startup script options by default.
+ Include customized RPM scripts. Adds ability to specify if you want to add/remove the daemon user and group.

### 1.0.1

+ Added support for cross builds to the release tasks. Specifically, the publish tasks will publish all cross builds now.
+ Added new key `scriptTemplates`, which allows RPM scripts to be specified. 
  The related keys from SBT Native Packager are no longer used as they don't allow for outright replacement of 
  the scripts.
+ Added new key `manageDaemonAccounts` which by default sets a linux script replacement. 
  In conjunction with the new script templates, this allows the user to specify if the daemon user and group should be 
  managed by the scripts. Defaults to false. 
+ StartupScriptPlugin is now required by CentOSRPMPlugin and transitively StandardPluginSet as well.
+ Deprecate top level toVersion implicit, moving the functionality into the VersionManagementPlugin's autoImport. 
  Projects using the .sbt format no longer need to import the implicit to use it.
+ Make generator plugins require the JvmPlugin so JvmPlugin doesn't override our resourceGenerator settings.
+ Introduce TestPlugin, add to StandardPluginSet. This enables the IntegrationTest config and adds a testcommon
  source directory shared between tests and integration tests. 

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