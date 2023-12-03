package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DeopCommand(val core: Hydrogen) : CoreCMD(core, "deop", "<red>/deop <<gray>player<red>>", 1,
    "hydrogen.command.deop", false, true) {

    override fun run() {
        val target = Bukkit.getPlayer(args[0]) ?: Bukkit.getOfflinePlayer(args[0])
        val file = getOfflineUserFileRaw(core, target.uniqueId) ?: return sendMessage(sender, "<red>${target.name} has never joined the server.")

        if (!target.isOp) {
            return sendMessage(sender, "<red>${target.name} is not a server operator.")
        }

        target.isOp = false
        file.setOp(false)

        sendMessage(sender, "<gray>${target.name} is no longer server operator.")
        if (sender != core.server.consoleSender) {
            sendMessage(core.server.consoleSender, "<aqua>${sender.name} <gray>has removed operator status from <aqua>${target.name}<gray>.")
        }
        if (target is Player) {
            sendMessage(target, "<gray>You are no longer server operator.")
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