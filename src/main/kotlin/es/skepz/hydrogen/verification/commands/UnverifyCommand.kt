package es.skepz.hydrogen.verification.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class UnverifyCommand(val core: Hydrogen) : CoreCMD(core, "unverify", "/unverify <player>", 1,
    "hydrogen.command.unverify", false, true) {

    override fun run() {
        // get the player, offline if needed
        val player = core.server.getPlayer(args[0]) ?: core.server.getOfflinePlayer(args[0])

        // get the file
        val file = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "&cThat player has never joined the server!")

        if (!file.isVerified()) return sendMessage(sender, "&cThat player is not verified!")

        file.setVerified(false)
        sendMessage(sender, "&7Player &b${player.name} &7has been unverified!")
        if (player is Player) {
            sendMessage(player, "&7You have been unverified!")
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