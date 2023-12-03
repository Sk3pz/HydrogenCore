package es.skepz.hydrogen.verification.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.checkVerification
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import es.skepz.hydrogen.utils.refreshPermissions
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class VerifyCommand(val core: Hydrogen) : CoreCMD(core, "verify", "<red>/verify <<gray>player<red>>", 1,
    "hydrogen.command.verify", false, true) {

    override fun run() {
        // get the player, offline if needed
        val player = core.server.getPlayer(args[0]) ?: core.server.getOfflinePlayer(args[0])

        // get the file
        val file = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "<red>That player has never joined the server!")

        if (file.isVerified()) return sendMessage(sender, "<red>That player is already verified!")

        file.setVerified(true)
        sendMessage(sender, "<gray>Player <aqua>${player.name} <gray>has been verified!")

        if (player is Player) {
            refreshPermissions(core, player)
            sendMessage(player, "<gray>You have been verified!")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}