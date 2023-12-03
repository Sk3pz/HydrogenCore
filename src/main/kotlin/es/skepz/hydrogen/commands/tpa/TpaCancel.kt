package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class TpaCancel(val core: Hydrogen) : CoreCMD(core, "tpcancel", "<red>/tpcancel",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer() ?: return
        if (core.tpaRequests.containsKey(player.uniqueId)) {
            val target = Bukkit.getPlayer(core.tpaRequests[player.uniqueId]!!)
            if (target != null) {
                sendMessage(target, "<aqua>${player.name} <gray>has canceled their request to teleport to you.")
                sendMessage(sender, "<gray>You have canceled your request to teleport to <aqua>${target.name}<gray>.")
            } else {
                sendMessage(sender, "<gray>You have canceled your request.")
            }
            core.tpaRequests.remove(player.uniqueId)
            return
        }
        if (core.tpahereRequests.containsKey(player.uniqueId)) {
            val target = Bukkit.getPlayer(core.tpahereRequests[player.uniqueId]!!)
            if (target != null) {
                sendMessage(target, "<aqua>${player.name} <gray>has canceled their request to teleport you to them.")
                sendMessage(sender, "<gray>You have canceled your request to teleport <aqua>${target.name} <gray>to you.")
            } else {
                sendMessage(sender, "<gray>You have canceled your request.")
            }
            core.tpaRequests.remove(player.uniqueId)
            return
        }

        sendMessage(sender, "<red>You dont have any outgoing requests.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}