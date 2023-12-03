package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class LookupCommand(val core: Hydrogen) : CoreCMD(core, "lookup", "<red>/lookup <<gray>player<red>>",
    1, "hydrogen.command.lookup", false, true) {

    override fun run() {
        // if the player is online, get the player otherwise attempt to get offline player
        val player = core.server.getPlayer(args[0]) ?: core.server.getOfflinePlayer(args[0])

        // get the target's user file if it exists
        val rfile = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "<red>That player does not exist or has not played before!")
        val file = UserFile(core, player, rfile)

        // get the user's file and information
        val name = player.name ?: "Unknown"
        val uuid = player.uniqueId.toString()
        val verified = file.isVerified()
        val firstJoined = file.firstJoin()
        val rank = file.getRank()
        val balance = file.getBal()
        val op = file.isOp()
        val bans = file.getBans()
        val mutes = file.getMutes()
        val kicks = file.getKicks()
        val isBanned = file.isBanned()
        val isMuted = file.isMuted()

        // send the information to the sender
        sendMessage(sender, "<gray><strikethrough>----------------------------------------\n"
                + "<aqua><bold>$name <gray>(<aqua>$uuid<gray>)\n"
                + "<gray>Verified: <aqua>$verified\n"
                + "<gray>Rank: <aqua>$rank\n"
                + "<gray>Balance: <aqua>$balance\n"
                + "<gray>OP: <aqua>$op\n"
                + "<gray>First Joined: <aqua>$firstJoined\n"
                + "<gray>Bans: <aqua>$bans\n"
                + "<gray>Mutes: <aqua>$mutes\n"
                + "<gray>Kicks: <aqua>$kicks\n"
                + "<gray>Banned: <aqua>$isBanned\n"
                + "<gray>Muted: <aqua>$isMuted\n"
                + "<gray><strikethrough>----------------------------------------")

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}