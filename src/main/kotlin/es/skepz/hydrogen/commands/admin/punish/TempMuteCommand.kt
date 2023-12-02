package es.skepz.hydrogen.commands.admin.punish

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.ArrayList

class TempMuteCommand(val core: Hydrogen) : CoreCMD(core, "tempmute",
    "&c/tempmute <&7name&c> <&7time&c> <&7seconds&c|&7minutes&c|&7hours&c|&7days&c|&7years&c> <&7reason&c?>", 2,
    "hydrogen.command.tempmute", false, true) {

    override fun run() {
        // get the player or offline player from the first argument
        val target = args[0]
        val senderName = if (sender is Player)
            PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName())
        else "Console"

        var time = args[1].toLongOrNull() ?: run {
            sendMessage(sender, "&cInvalid time: ${args[1]}")
            return
        }

        val rawTime = "$time ${args[2]}"

        when (args[2]) {
            "seconds", "second" -> time *= 1000
            "minutes", "minute" -> time *= 60000
            "hours", "hour" -> time *= 3600000
            "days", "day" -> time *= 86400000
            "years", "year" -> time *= 31536000000
            else -> {
                sendMessage(sender, "&cInvalid time denominator!")
                return
            }
        }

        val reason = if (args.size > 3) {
            args.removeAt(0)
            args.removeAt(0)
            args.removeAt(0)
            args.joinToString(" ")
        } else {
            "No reason provided"
        }

        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's file
        val file = UserFile(core, targetPlayer.uniqueId)

        // check the player's rank
        val rank = file.getRank()

        // check if the player's rank has isOp set, or if they have the * permission
        val isOp = core.files.ranks.cfg.getBoolean("ranks.$rank.isOp")
        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
        if ((permissions.contains("*") || isOp)
            && !sender.hasPermission("quarkcore.punish-restriction-bypass") && !sender.hasPermission("*")) {
            sender.sendMessage("&cYou cannot mute this player!")
            return
        }

        // set the player's ban status to true
        file.setMuted(reason, senderName, time)
        file.addMute()

        sendMessage(sender, "&7You have muted &b$target &7for &b$reason&a.")
        if (targetPlayer is Player) {
            sendMessage(targetPlayer, "&7You have been muted by &b$senderName&7 for &b$reason&7 for &b$rawTime&7.")
        }
        Bukkit.getLogger().severe("$senderName has muted $target for $reason for $rawTime.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        when (args.size) {
            1 -> {
                StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
            }
            3 -> {
                StringUtil.copyPartialMatches(args[2], listOf("seconds", "minutes", "hours", "days", "years"), completions)
            }

        }
        return completions
    }
}