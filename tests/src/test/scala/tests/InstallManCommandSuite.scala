package tests

import moped.annotations.Description
import moped.annotations.ExampleUsage
import moped.cli.Application
import moped.cli.Command
import moped.cli.CommandParser
import moped.commands._
import moped.testkit.MopedSuite
import org.typelevel.paiges.Doc

object InstallManCommandSuite {

  @ExampleUsage("example foobar hello world")
  @Description("foobar subcommand description")
  case class FoobarCommand(
      @Description("if true, does something") flag: Boolean = false
  ) extends Command {
    def run(): Int = 0
  }
  object FoobarCommand {
    implicit val parser: CommandParser[FoobarCommand] = CommandParser
      .derive(FoobarCommand())
  }
  val app: Application = Application
    .fromName(
      "example",
      "1.0",
      List(CommandParser[ManCommand], CommandParser[FoobarCommand])
    )
    .withTagline("my testing binary")
    .withDescription(Doc.text("An example description"))
    .withExamples(Doc.text("example hello world"))
}

class InstallManCommandSuite extends MopedSuite(InstallManCommandSuite.app) {
  // NOTE(olafur): Ignored because this test shells out to `man`, which produces
  // different results on Linux/macOS. I'm not sure how to make this test pass
  // on all computers without implementing a custom renderer of the generated
  // manpage.
  test("basic".ignore) {
    runSuccessfully(List("man", "install"))
    val manFile = manDirectory.resolve("example.1")
    println()
    val rendered = scala
      .sys
      .process
      .Process(List("man", manFile.toString()))
      .!!
      .replaceAll(".\b", "")
    assertNoDiff(
      rendered,
      """|example(1)                                                          example(1)
         |
         |
         |
         |NAME
         |       example - my testing binary
         |
         |USAGE
         |       example COMMAND [OPTIONS]
         |
         |DESCRIPTION
         |       An example description
         |
         |EXAMPLES
         |       example hello world
         |
         |COMMANDS
         |       man
         |           Manage man page installation and uninstallation
         |
         |
         |           OPTIONS:
         |
         |               --help Print this help message
         |
         |       foobar
         |           foobar subcommand description
         |
         |           EXAMPLES:
         |               example foobar hello world
         |
         |           OPTIONS:
         |
         |               --help Print this help message
         |
         |               --flag if true, does something
         |
         |
         |
         |Example Manual                    2020-09-24                        example(1)
         |""".stripMargin
    )
  }
}
