package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getSpawn
import org.bukkit.Sound
import org.bukkit.command.CommandSender

class SpawnCommand(val core: Hydrogen) : CoreCMD(core, "spawn", "&c/spawn", 0,
    "none", true, false) {

    override fun run() {
        val player = getPlayer()!!

        player.teleport(getSpawn(core))
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
        sendMessage(player, "&7Woosh! You have been teleported to spawn!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}