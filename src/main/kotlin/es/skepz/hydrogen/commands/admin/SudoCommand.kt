package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD

class SudoCommand(val core: Hydrogen) : CoreCMD(core, "sudo", "/sudo <command>",
    1, "hydrogen.command.sudo", true, false) {

    override fun run() {
        val command = args.joinToString(" ")
        core.server.dispatchCommand(sender, command)
        sendMessage(sender, "&7Command &b$command &7executed as console!")
    }

}