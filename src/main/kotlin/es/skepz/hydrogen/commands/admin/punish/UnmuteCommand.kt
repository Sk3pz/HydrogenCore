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

class UnmuteCommand(val core: Hydrogen) : CoreCMD(core, "unmute", "/unmute <name>", 1,
    "hydrogen.command.unmute", false, true) {

    override fun run() {
        // get the player or offline player from the first argument
        val target = args[0]
        val senderName = if (sender is Player)
            PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName())
        else "Console"

        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's file
        val file = UserFile(core, targetPlayer.uniqueId)

        // check if the player is muted
        if (!file.isMuted()) {
            sendMessage(sender, "&c$target is not muted!")
            return
        }

        // set the player's ban status to true
        file.setUnmuted()

        sendMessage(sender, "&7You have unmuted &b$target.")
        if (targetPlayer is Player) {
            sendMessage(targetPlayer, "&7You have been unmuted by &b$senderName.")
        }
        Bukkit.getLogger().severe("$senderName has unmuted $target")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}