package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import java.util.ArrayList

class DelHomeCommand(val core: Hydrogen) : CoreCMD(core, "delhome", "&c/delhome <&7name&c>",
    1, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "&cFailed to delete home: &f$name&c is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        val file = getUserFile(core, player)

        // check if the home exists
        if (!file.homeExists(name)) {
            sendMessage(player, "&cYou do not have any homes with that name!")
            return
        }

        file.delHome(name)
        sendMessage(player, "&7Removed your home!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}