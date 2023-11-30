package es.skepz.hydrogen.commands.`fun`

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class FeedCommand(core: Hydrogen) : CoreCMD(core, "feed", "&c/feed", 0,
    "hydrogen.command.feed", false, true) {

    override fun run() {
        val target = if (args.size > 0) {
            if (!checkPermission(sender, "hydrogen.command.feed.others"))
                return sendMessage(sender, "&cYou do not have permission to feed other players!")
            Bukkit.getPlayer(args[0])
        } else {
            if (sender is Player) sender as Player
            else return sendMessage(sender, "&cYou must be a player to use this command!")
        }
        if (target == null) {
            sendMessage(sender, "&cThat player is not online!")
            return
        }

        target.foodLevel = 20
        target.saturation = 20f
        if (sender != target) {
            sendMessage(sender, "&7You have &bfed &b${target.name}&7!")
        }
        sendMessage(target, "&7You have been &bfed&7. Yum!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1 && checkPermission(sender, "hydrogen.command.feed.others")) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}