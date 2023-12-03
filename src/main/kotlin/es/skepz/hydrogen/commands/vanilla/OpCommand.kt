package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class OpCommand(val core: Hydrogen) : CoreCMD(core, "op", "<red>/op <<gray>player<red>>", 1,
    "hydrogen.command.op", false, true) {

    override fun run() {
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            sendMessage(sender, "<red>Player not found.")
            return
        }
        target.isOp = true

        val file = UserFile(core, target)
        file.setOp(true)

        sendMessage(sender, "<gray>${target.name} is now a server operator.")
        if (sender != core.server.consoleSender) {
            sendMessage(core.server.consoleSender, "<gray>${sender.name} has made ${target.name} a server operator.")
        }
        sendMessage(target, "<gray>You are now a server operator.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}