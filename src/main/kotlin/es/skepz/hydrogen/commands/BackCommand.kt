package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Sound

class BackCommand(val core: Hydrogen) : CoreCMD(core, "back", "<red>/back",
    0, "hydrogen.command.back", true, false) {

        override fun run() {
            val player = getPlayer()!!
            if (core.backLocations.containsKey(player.uniqueId)) {
                player.teleport(core.backLocations[player.uniqueId]!!)
                core.backLocations.remove(player.uniqueId)

                sendMessage(sender, "<gray>Teleported to your last location!")
                playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
            } else {
                sendMessage(sender, "<red>You don't have anywhere to go back to!")
            }
        }

}