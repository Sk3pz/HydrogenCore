package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import es.skepz.hydrogen.utils.refreshPermissions
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class PermissionCommand(val core: Hydrogen) : CoreCMD(core, "permission", "&c/permission <&7add&c|&7remove&c|&7list&c> <&7player&c>",
    2, "hydrogen.command.permission", false, true) {

    override fun run() {
        val mode = args[0]
        val target = Bukkit.getPlayer(args[1]) ?: Bukkit.getOfflinePlayer(args[1])

        if (sender is Player && sender == target && !sender.hasPermission("hydrogen.command.permission.self")) {
            return sendMessage(sender, "&cYou can't modify your own permissions!")
        }

        // get the target's user file if it exists
        val rfile = getOfflineUserFileRaw(core, target.uniqueId) ?: return sendMessage(sender, "&cThat player does not exist or has not played before!")
        val file = UserFile(core, target, rfile)

        when (mode) {
            "add" -> {
                if (args.size != 3) return sendMessage(sender, "&cInvalid usage! Do &7/permission add <&7player&c> <&7permission&c>!")
                val permission = args[2]
                file.addPerm(permission)
                sendMessage(sender, "&7Added permission &b$permission &7to &b${target.name}&7!")
                if (target is Player) {
                    refreshPermissions(core, target)
                    sendMessage(target, "&7Your permissions have been updated!")
                }
            }
            "remove" -> {
                if (args.size != 3) return sendMessage(sender, "&cInvalid usage! Do &7/permission remove <&7player&c> <&7permission&c>!")
                val permission = args[2]
                file.removePerm(permission)
                sendMessage(sender, "&7Removed permission &b$permission &7from &b${target.name}&7!")
                if (target is Player) {
                    refreshPermissions(core, target)
                    sendMessage(target, "&7Your permissions have been updated!")
                }
            }
            "list" -> {
                sendMessage(sender, "&b${target.name}&7's permissions: &b${file.getPerms().joinToString("&7, &b")}")
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