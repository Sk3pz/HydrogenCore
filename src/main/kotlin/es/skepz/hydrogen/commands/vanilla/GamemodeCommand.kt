package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class GamemodeCommand(val core: Hydrogen) : CoreCMD(core, "gamemode", "&c/gamemode <&7gm&c> <&7player?&c>", 1,
    "hydrogen.command.fly", false, true) {

    override fun run() {
        var other = false
        val target = if (args.size == 2) {
            if (!checkPermission(sender, "hydrogen.command.gamemode.others")) {
                sendMessage(sender, "&cYou do not have permission to change other players' gamemode!")
                return
            }
            other = true
            Bukkit.getPlayer(args[1]) ?: return sendMessage(sender, "&cInvalid player!")
        } else { getPlayer()!! }

        val mode = args[0].lowercase()
        val gm = when (mode) {
            "0", "s", "survival" -> GameMode.SURVIVAL
            "1", "c", "creative" -> GameMode.CREATIVE
            "2", "a", "adventure" -> GameMode.ADVENTURE
            "3", "sp", "spectator" -> GameMode.SPECTATOR
            else -> return sendMessage(sender, "&cInvalid gamemode! Valid gamemodes: &7survival&c, &7creative&c, &7adventure&c, &7spectator&c.")
        }

        target.gameMode = gm
        if (other) {
            sendMessage(sender, "&7Successfully changed &b${target.name}&7's gamemode to &b${gm.name.lowercase()}&7!")
        } else {
            sendMessage(sender, "&7Successfully changed your gamemode to &b${gm.name.lowercase()}&7!")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        when (args.size) {
            1 -> {
                StringUtil.copyPartialMatches(args[0], listOf("survival", "creative", "adventure", "spectator"), completions)
            }
            2 -> {
                if (checkPermission(sender, "hydrogen.command.gamemode.others")) {
                    StringUtil.copyPartialMatches(args[1], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
                }
            }
        }
        return completions
    }
}