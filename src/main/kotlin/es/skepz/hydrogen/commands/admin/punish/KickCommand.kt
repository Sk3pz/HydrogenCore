package es.skepz.hydrogen.commands.admin.punish

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.ArrayList

class KickCommand(val core: Hydrogen) : CoreCMD(core, "kick", "<red>/kick <<gray>name<red>> <<gray>reason<red>?>", 1,
    "hydrogen.command.kick", false, true) {

    override fun run() {
        // get the player or offline player from the first argument
        val target = args[0]
        val senderName = if (sender is Player)
            PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName())
        else "Console"

        val reason = if (args.size > 1) {
            args.removeAt(0)
            args.joinToString(" ")
        } else {
            "No reason provided"
        }

        val targetPlayer = core.server.getPlayer(target) ?: return sendMessage(sender, "<red>That player is not online!")

        // get the target's file
        val file = UserFile(core, targetPlayer)

        // check the player's rank
        val rank = file.getRank()

        // check if the player's rank has isOp set, or if they have the * permission
        val isOp = core.files.ranks.cfg.getBoolean("ranks.$rank.isOp")
        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
        if ((permissions.contains("*") || isOp)
            && !sender.hasPermission("quarkcore.punish-restriction-bypass") && !sender.hasPermission("*")) {
            sender.sendMessage("<red>You cannot kick this player!")
            return
        }

        // add a kick to the user's file
        file.addKick()

        // kick the player if they are online
        targetPlayer.kick(colorize("<red>You are kicked from this server!\n" +
                            "<red>Reason: <gray>$reason\n" +
                            "<red>Kicked by: <gray>$senderName\n"))

        sendMessage(sender, "<gray>You have kicked <aqua>$target <gray>for <aqua>$reason<gray>.")
        Bukkit.getLogger().severe("$senderName has kicked $target for $reason")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}