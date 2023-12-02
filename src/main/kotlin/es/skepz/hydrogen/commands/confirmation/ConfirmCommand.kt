package es.skepz.hydrogen.commands.confirmation

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class ConfirmCommand(val core: Hydrogen) : CoreCMD(core, "core_confirm", "&c/core_confirm",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val callback = core.confirmMap[player.uniqueId] ?: return sendMessage(player, "&cYou have nothing to confirm.")
            callback(true)
        core.confirmMap.remove(player.uniqueId)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}