package es.skepz.hydrogen.commands.admin.punish

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class BanCommand(val core: Hydrogen) : CoreCMD(core, "ban", "&c/ban <&7name&c> <&7reason&c?>", 1,
    "hydrogen.command.ban", false, true) {

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

        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's file
        val file = getOfflineUserFileRaw(core, targetPlayer.uniqueId)
            ?: return sendMessage(sender, "&cThat player has never joined the server!")

        // check the player's rank
        val rank = file.getRank()

        // check if the player's rank has isOp set, or if they have the * permission
        val isOp = core.files.ranks.cfg.getBoolean("ranks.$rank.isOp")
        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
        if ((permissions.contains("*") || isOp)
            && !sender.hasPermission("quarkcore.punish-restriction-bypass") && !sender.hasPermission("*")) {
            sender.sendMessage("&cYou cannot ban this player!")
            return
        }

        // set the player's ban status to true
        file.setBanned(reason, senderName)
        file.addBan()

        // kick the player if they are online
        if (targetPlayer is Player) {
            targetPlayer.kick(Component.text(
                colorize("&cYou are banned from this server!\n" +
                    "&cReason: &f$reason\n" +
                    "&cBanned by: &f$senderName\n" +
                    "&cThis ban is permanent.")
            ))
        }

        targetPlayer.banPlayer("&cYou are banned from this server!\n" +
                "&cReason: &f$reason\n" +
                "&cBanned by: &f$senderName\n" +
                "&cThis ban is permanent.")
        
        sendMessage(sender, "&7You have banned &b$target &7for &b$reason&7.")
        Bukkit.getLogger().severe("$senderName has banned $target for $reason")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}