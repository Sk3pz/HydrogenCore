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

class HomeCommand(val core: Hydrogen) : CoreCMD(core, "home", "&c/home <&7name&c>",
    0, "none", true, true) {

    override fun run() {
        val player = getPlayer()!!
        val file = UserFile(core, player)

        if (args.size == 0) {
            val homes = file.getHomes()
            if (homes.isEmpty()) {
                sendMessage(sender, "&cYou do not have any homes set!")
                return;
            }
            var homelist = "&7Homes (${homes.size}): "
            for (x in homes.indices) {
                val h = homes[x]
                homelist = homelist.plus("&b$h")
                if (x != homes.size - 1) {
                    homelist = homelist.plus("&7, ")
                }
            }
            sendMessage(sender, homelist)
            return
        }

        val name = args[0]

        for (c in name) {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9' && c != '_') {
                sendMessage(player, "&cFailed to get home: &f$name&c is not a valid name! must only contain a-z, 0-9 or underscores!")
                return
            }
        }

        // check if the home exists
        if (!file.homeExists(name)) {
            sendMessage(player, "&cYou do not have a home named &f$name&c! Set one with /sethome <&7$name&c>")
            return
        }

        val home = file.getHome(name) ?: return sendMessage(player, "&cFailed to recall home location from your file. Please contact an admin if this persists!")
        player.teleport(home)
        sendMessage(sender, "&7Woosh! You have been teleported to &b$name&7!")
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