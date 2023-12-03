package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD

class SudoCommand(val core: Hydrogen) : CoreCMD(core, "sudo", "<red>/sudo <<gray>command<red>>",
    1, "hydrogen.command.sudo", true, false) {

    override fun run() {
        val command = args.joinToString(" ")
        core.server.dispatchCommand(core.server.consoleSender, command)
        sendMessage(sender, "<gray>Command <aqua>$command <gray>executed as console!")
    }

}