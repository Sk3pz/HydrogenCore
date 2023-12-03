package es.skepz.hydrogen.commands.`fun`

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class HealCommand(core: Hydrogen) : CoreCMD(core, "heal", "<red>/heal", 0,
    "hydrogen.command.heal", false, true) {

    override fun run() {
        val target = if (args.size > 0) {
            if (!checkPermission(sender, "hydrogen.command.god.others"))
                return sendMessage(sender, "<red>You do not have permission to heal other players!")
            Bukkit.getPlayer(args[0])
        } else {
            if (sender is Player) sender as Player
            else return sendMessage(sender, "<red>You must be a player to use this command!")
        }
        if (target == null) {
            sendMessage(sender, "<red>That player is not online!")
            return
        }

        target.health = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        if (sender != target) {
            sendMessage(sender, "<gray>You have <aqua>healed <aqua>${target.name}<gray>!")
        }
        sendMessage(target, "<gray>You have been <aqua>healed<gray>.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1 && checkPermission(sender, "hydrogen.command.heal.others")) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}