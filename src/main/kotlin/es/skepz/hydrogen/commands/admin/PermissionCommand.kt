package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class PermissionCommand(val core: Hydrogen) : CoreCMD(core, "permission", "/permission <&7add&c|&7remove&c|&7list&c> <&7player&c>",
    2, "hydrogen.command.permission", false, true) {

    override fun run() {
        val mode = args[0]
        val target = Bukkit.getPlayer(args[1]) ?: return sendMessage(sender, "&cInvalid player!")

        if (sender is Player && sender == target && !sender.hasPermission("hydrogen.command.permission.self")) {
            return sendMessage(sender, "&cYou can't modify your own permissions!")
        }

        val file = getUserFile(core, target)

        when (mode) {
            "add" -> {
                if (args.size != 3) return sendMessage(sender, "&cInvalid usage! Do &7/permission add <&7player&c> <&7permission&c>!")
                val permission = args[2]
                file.addPerm(permission)
                sendMessage(sender, "&aAdded permission &7$permission &ato &7${target.name}&a!")
            }
            "remove" -> {
                if (args.size != 3) return sendMessage(sender, "&cInvalid usage! Do &7/permission remove <&7player&c> <&7permission&c>!")
                val permission = args[2]
                file.removePerm(permission)
                sendMessage(sender, "&aRemoved permission &7$permission &afrom &7${target.name}&a!")
            }
            "list" -> {
                val permissions = target.effectivePermissions.map { p -> p.permission }
                sendMessage(sender, "&7${target.name}&a's permissions: &7${permissions.joinToString("&a, &7")}")
            }
            else -> {
                sendMessage(sender, "&cInvalid mode! Valid modes: &7add&c, &7remove&c, &7list")
            }
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        when (args.size) {
            1 -> StringUtil.copyPartialMatches(args[0], listOf("add", "remove", "list"), completions)
            2 -> StringUtil.copyPartialMatches(args[1], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}