package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DeopCommand(val core: Hydrogen) : CoreCMD(core, "deop", "&c/deop <&7player&c>", 1,
    "hydrogen.command.deop", false, true) {

    override fun run() {
        val target = Bukkit.getPlayer(args[0]) ?: Bukkit.getOfflinePlayer(args[0])
        val file = getOfflineUserFileRaw(core, target.uniqueId) ?: return sendMessage(sender, "&c${target.name} has never joined the server.")

        if (!target.isOp) {
            return sendMessage(sender, "&c${target.name} is not a server operator.")
        }

        target.isOp = false
        file.setOp(false)

        sendMessage(sender, "&7${target.name} is no longer server operator.")
        if (sender != core.server.consoleSender) {
            sendMessage(core.server.consoleSender, "&b${sender.name} &7has removed operator status from &b${target.name}&7.")
        }
        if (target is Player) {
            sendMessage(target, "&7You are no longer server operator.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], core.server.operators.map { p -> p.name }, completions)
        }
        return completions
    }
}