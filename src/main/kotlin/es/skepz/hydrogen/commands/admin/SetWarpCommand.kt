package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getWarp
import es.skepz.hydrogen.utils.sendConfirmMsg
import es.skepz.hydrogen.utils.setWarp
import es.skepz.hydrogen.utils.warpExists
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import java.util.ArrayList

class SetWarpCommand(val core: Hydrogen) : CoreCMD(core, "setwarp", "<red>/setwarp <<gray>name<red>> <<gray>permission?<red>>",
    1, "hydrogen.command.setwarp", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val loc = player.location
        val name = args[0]

        val permission = if (args.size > 1) args[1] else null

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "<red>Failed to set warp: <gray>$name<red> is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        // check if the warp exists and if the user wants to overwrite it
        if (warpExists(core, name)) {
            sendConfirmMsg(core, player, "A warp named <aqua>$name<gray> already exists! Do you want to overwrite it?") { confirm ->
                if (!confirm) {
                    sendMessage(player, "<red>Warp not overwritten!")
                    return@sendConfirmMsg
                }
                setWarp(core, name, loc, permission)
                sendMessage(player, "<gray>Warp <aqua>$name<gray> has been overwritten!")
                playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
            }
            return
        }

        setWarp(core, name, loc, permission)
        sendMessage(player, "<gray>New warp set!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}