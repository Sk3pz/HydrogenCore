package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.delWarp
import es.skepz.hydrogen.utils.warpExists
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import java.util.ArrayList

class DelWarpCommand(val core: Hydrogen) : CoreCMD(core, "delwarp", "<red>/delwarp <<gray>name<red>>",
    1, "hydrogen.command.delwarp", true, false) {

    override fun init() {
        // not used for this function
    }

    override fun run() {
        val player = getPlayer()!!
        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "<red>Failed to delete warp: <gray>$name<red> is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        // check if the home exists
        if (!warpExists(core, name)) {
            sendMessage(player, "<red>You do not have any warps with that name!")
            return
        }

        delWarp(core, name)
        sendMessage(player, "<gray>Removed your warp!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}