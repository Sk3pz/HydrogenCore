package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD

class BackCommand(val core: Hydrogen) : CoreCMD(core, "back", "&c/back", 0, "hydrogen.command.back", true, false) {

        override fun run() {
            val player = getPlayer()!!
            if (core.backLocations.containsKey(player.uniqueId)) {
                player.teleport(core.backLocations[player.uniqueId]!!)
                core.backLocations.remove(player.uniqueId)
            } else {
                sendMessage(sender, "&cYou don't have a location to go back to!")
            }
        }

}