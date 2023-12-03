package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class ConfigsCommand(private val core: Hydrogen) : CoreCMD(core, "cfgreload", "<red>/cfgreload",
    0, "hydrogen.command.cfgrl", false, false) {
    override fun run() {
        core.files.reload()
        sendMessage(sender, "<gray>Reloaded all configs.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}