package es.skepz.hydrogen.commands.`fun`

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class SmiteCommand(val core: Hydrogen) : CoreCMD(core, "smite", "&c/smite <&7player&c>", 1,
    "hydrogen.command.smite", false, true) {

    override fun run() {
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            sendMessage(sender, "&cThat player is not online!")
            return
        }
        target.world.strikeLightning(target.location)
        sendMessage(sender, "&7Thou hath smitten &b${target.name}&7!")
        sendMessage(target, "&7Thou hath been smitten&7!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}