package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.sendConfirmMsg
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import java.util.ArrayList

class SetHomeCommand(val core: Hydrogen) : CoreCMD(core, "sethome", "<red>/sethome <<gray>name<red>>",
    1, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val loc = player.location
        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "<red>Failed to set home: <gray>$name<red> is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        val file = UserFile(core, player)

        // check if the home exists and if the user wants to overwrite it
        if (file.homeExists(name)) {
            val home = file.getHome(name)!!
            sendConfirmMsg(core, player, "You already have a home named <aqua>$name<gray>! Do you want to overwrite it?") { confirm ->
                if (!confirm) {
                    sendMessage(player, "<red>Home not overwritten!")
                    return@sendConfirmMsg
                }
                file.setHome(name, loc)
                sendMessage(player, "<gray>You have overwritten <aqua>$name<gray>!\n" +
                        "<gray>The previous location was in <aqua>${home.world.name} <gray>at: <gray>x:<aqua> ${home.x} <gray>y:<aqua> ${home.y} <gray>z:<aqua> ${home.z}")
                playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
            }
            return
        }

        file.setHome(name, loc)
        sendMessage(player, "<gray>New home set!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}