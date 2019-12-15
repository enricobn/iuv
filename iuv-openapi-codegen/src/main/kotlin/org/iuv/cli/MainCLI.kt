package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class CLICommand : CliktCommand() {
    override fun run() {

    }

}

fun main(args: Array<String>) =
        CLICommand()
                .subcommands(NewProjectCommand(), OpenAPICommand(), HtmlToIUVCommand())
                .main(args)
