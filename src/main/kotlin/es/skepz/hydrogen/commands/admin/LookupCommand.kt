package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class LookupCommand(val core: Hydrogen) : CoreCMD(core, "lookup", "&c/lookup <&7player&c>",
    1, "hydrogen.command.lookup", true, true) {

    override fun run() {
        // if the player is online, get the player otherwise attempt to get offline player
        val player = Bukkit.getPlayer(args[0]) ?: Bukkit.getOfflinePlayer(args[0])
        if (!player.hasPlayedBefore()) {
            sendMessage(sender, "&cThat player has never played before!")
            return
        }

        // get the user's file and information
        val file = UserFile(core, player.uniqueId)
        val name = player.name ?: "Unknown"
        val uuid = player.uniqueId.toString()
        val firstJoined = file.firstJoin()
        val lastLogin = file.lastLogin()
        val lastLogoff = file.lastLogoff()
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
                + "&7Rank: &b$rank\n"
                + "&7Balance: &b$balance\n"
                + "&7OP: &b$op\n"
                + "&7First Joined: &b$firstJoined\n"
                + "&7Last Login: &b$lastLogin\n"
                + "&7Last Logoff: &b$lastLogoff\n"
                + "&7Bans: &b$bans\n"
                + "&7Mutes: &b$mutes\n"
                + "&7Kicks: &b$kicks\n"
                + "&7Banned: &b$isBanned\n"
                + if (isBanned) {
                    val banTime = file.banTime()
                    val banTimeStr = if (banTime == -1L) {
                        "Permanent"
                    } else {
                        "$banTime seconds"
                    }
                    ("&7Ban Reason: &b${file.banReason()}\n"
                            + "&7Ban Sender: &b${file.banSender()}\n"
                            + "&7Ban Time: &b$banTimeStr\n"
                            + "&7Ban Start: &b${file.banStart()}\n")
                } else ""
                + "&7Muted: &b$isMuted\n"
                + if (isMuted) {
                    val muteTime = file.muteTime()
                    val muteTimeStr = if (muteTime == -1L) {
                        "Permanent"
                    } else {
                        "$muteTime seconds"
                    }
                    ("&7Muted Reason: &b${file.muteReason()}\n"
                        + "&7Muted Sender: &b${file.muteSender()}\n"
                        + "&7Muted Time: &b$muteTimeStr\n"
                        + "&7Muted Start: &b${file.muteStart()}\n")
                } else ""
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