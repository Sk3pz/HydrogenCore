package es.skepz.hydrogen.commands.confirmation

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class CancelCommand(val core: Hydrogen) : CoreCMD(core, "core_cancel", "&c/core_cancel",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        if (core.confirmMap.contains(player.uniqueId)) {
            core.confirmMap.remove(player.uniqueId)?.let { it(false) }
        } else {
            return sendMessage(player, "&cYou have nothing to confirm.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}