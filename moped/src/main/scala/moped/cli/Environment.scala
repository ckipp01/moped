package moped.cli

import java.io.BufferedReader
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.{util => ju}

import scala.collection.JavaConverters._

import dataclass.data
import dev.dirs.ProjectDirectories

@data
class Environment(
    dataDirectory: Path,
    cacheDirectory: Path,
    preferencesDirectory: Path,
    workingDirectory: Path = Paths.get(System.getProperty("user.dir")),
    homeDirectory: Path = Paths.get(System.getProperty("user.home")),
    standardOutput: PrintStream = Console.out,
    standardError: PrintStream = Console.err,
    standardInput: BufferedReader = Console.in,
    systemProperties: ju.Properties = System.getProperties(),
    exit: Int => Nothing = Environment.defaultSystemExit(_),
    environmentVariables: collection.Map[String, String] =
      System.getenv().asScala,
    clock: Clock = Clock.systemDefaultZone(),
    console: java.io.Console = System.console()
) {

  /**
   * Returns true if the key has is equal to "true" in environment variables or
   * system properties.
   */
  def isSettingTrue(key: String): Boolean =
    "true".equalsIgnoreCase(environmentVariables.getOrElse(key, "false")) ||
      "true".equalsIgnoreCase(systemProperties.getProperty(key, "false"))

  /**
   * Returns true if the key is defined in the environment variables or system
   * properties, regardless of value.
   */
  def isSettingPresent(key: String): Boolean =
    environmentVariables.contains(key) || systemProperties.contains(key)

  val isCI: Boolean = isSettingTrue("CI")
  val isColorEnabled: Boolean =
    !isSettingPresent("NO_COLOR") && !isSettingPresent("INSIDE_EMACS")
  val isProgressBarEnabled: Boolean =
    isColorEnabled && !isCI && !isSettingPresent("NO_PROGRESS_BAR")
  def withProjectDirectories(dirs: ProjectDirectories): Environment =
    this
      .withDataDirectory(Paths.get(dirs.dataDir))
      .withCacheDirectory(Paths.get(dirs.cacheDir))
      .withPreferencesDirectory(Paths.get(dirs.preferenceDir))
}

object Environment {
  val default: Environment = fromName("moped")
  def fromName(name: String): Environment =
    fromProjectDirectories(ProjectDirectories.fromPath("moped"))
  def fromProjectDirectories(dirs: ProjectDirectories): Environment =
    Environment(
      dataDirectory = Paths.get(dirs.dataDir),
      cacheDirectory = Paths.get(dirs.cacheDir),
      preferencesDirectory = Paths.get(dirs.preferenceDir)
    )

  def defaultSystemExit(code: Int): Nothing = {
    System.exit(code)
    throw new IllegalStateException(
      s"System.exit($code) failed to exit the process"
    )
  }
}
