package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.ArrayList

class DelHomeCommand(val core: Hydrogen) : CoreCMD(core, "delhome", "<red>/delhome <<gray>name<red>>",
    1, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "<red>Failed to delete home: <gray>$name<red> is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        val file = UserFile(core, player)

        // check if the home exists
        if (!file.homeExists(name)) {
            sendMessage(player, "<red>You do not have any homes with that name!")
            return
        }

        file.delHome(name)
        sendMessage(player, "<gray>Removed your home!")
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = ArrayList<String>()

        if (sender !is Player) return completions

        val player = sender
        val file = UserFile(core, player)

        val homes = file.getHomes()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], homes, completions)
        }
        return completions
    }

}