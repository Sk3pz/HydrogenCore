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

class LookupCommand(val core: Hydrogen) : CoreCMD(core, "lookup", "&c/lookup <&7player&c>",
    1, "hydrogen.command.lookup", false, true) {

    override fun run() {
        // if the player is online, get the player otherwise attempt to get offline player
        val player = core.server.getPlayer(args[0]) ?: core.server.getOfflinePlayer(args[0])

        // get the target's user file if it exists
        val rfile = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "&cThat player does not exist or has not played before!")
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
        sendMessage(sender, "&7&m----------------------------------------\n"
                + "&b&l$name &7(&b$uuid&7)\n"
                + "&7Verified: &b$verified\n"
                + "&7Rank: &b$rank\n"
                + "&7Balance: &b$balance\n"
                + "&7OP: &b$op\n"
                + "&7First Joined: &b$firstJoined\n"
                + "&7Bans: &b$bans\n"
                + "&7Mutes: &b$mutes\n"
                + "&7Kicks: &b$kicks\n"
                + "&7Banned: &b$isBanned\n"
                + "&7Muted: &b$isMuted\n"
                + "&7&m----------------------------------------")

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}