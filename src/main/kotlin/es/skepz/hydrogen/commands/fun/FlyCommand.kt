package es.skepz.hydrogen.commands.`fun`

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class FlyCommand(val core: Hydrogen) : CoreCMD(core, "fly", "<red>/fly", 0,
    "hydrogen.command.fly", false, true) {

    override fun run() {
        val player = if (args.size == 1) {
            if (!checkPermission(sender, "hydrogen.command.fly.others"))
                return sendMessage(sender, "<red>You don't have permission to allow others to fly!")
            Bukkit.getPlayer(args[0]) ?: return sendMessage(sender, "<red>Player not found!")
        } else {
            if (sender !is Player) {
                sendMessage(sender, "<red>You must be a player to use this command!")
                return
            }
            sender as Player
        }

        player.allowFlight = !player.allowFlight
        sendMessage(player, "<gray>Flying has been <aqua>${if (player.allowFlight) "enabled" else "disabled"}<gray>!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1 && checkPermission(sender, "hydrogen.command.fly.others")) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}