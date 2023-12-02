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

class SetHomeCommand(val core: Hydrogen) : CoreCMD(core, "sethome", "&c/sethome <&7name&c>",
    1, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val loc = player.location
        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "&cFailed to set home: &f$name&c is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        val file = UserFile(core, player)

        // check if the home exists and if the user wants to overwrite it
        if (file.homeExists(name)) {
            val home = file.getHome(name)!!
            sendConfirmMsg(core, player, "You already have a home named &b$name&7! Do you want to overwrite it?") { confirm ->
                if (!confirm) {
                    sendMessage(player, "&cHome not overwritten!")
                    return@sendConfirmMsg
                }
                file.setHome(name, loc)
                sendMessage(player, "&7You have overwritten &b$name&7!\n" +
                        "&7The previous location was in &b${home.world.name} &7at: &7x:&b ${home.x} &7y:&b ${home.y} &7z:&b ${home.z}")
                playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
            }
            return
        }

        file.setHome(name, loc)
        sendMessage(player, "&7New home set!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}