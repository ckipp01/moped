package tests

import moped.annotations.Description
import moped.cli.Application
import moped.cli.Command
import moped.cli.CommandParser

@Description("Print the console screen width")
final case class TerminalsCommand(app: Application = Application.default)
    extends Command {
  def run(): Int = {
    app.out.println(app.terminal.screenWidth())
    0
  }
}

object TerminalsCommand {
  implicit val parser: CommandParser[TerminalsCommand] = CommandParser
    .derive(TerminalsCommand())
}
