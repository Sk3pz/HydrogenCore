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

class PermissionCommand(val core: Hydrogen) : CoreCMD(core, "permission", "<red>/permission <<gray>add<red>|<gray>remove<red>|<gray>list<red>> <<gray>player<red>>",
    2, "hydrogen.command.permission", false, true) {

    override fun run() {
        val mode = args[0]
        val target = Bukkit.getPlayer(args[1]) ?: Bukkit.getOfflinePlayer(args[1])

        if (sender is Player && sender == target && !sender.hasPermission("hydrogen.command.permission.self")) {
            return sendMessage(sender, "<red>You can't modify your own permissions!")
        }

        // get the target's user file if it exists
        val rfile = getOfflineUserFileRaw(core, target.uniqueId) ?: return sendMessage(sender, "<red>That player does not exist or has not played before!")
        val file = UserFile(core, target, rfile)

        when (mode) {
            "add" -> {
                if (args.size != 3) return sendMessage(sender, "<red>Invalid usage! Do <gray>/permission add <<gray>player<red>> <<gray>permission<red>>!")
                val permission = args[2]
                file.addPerm(permission)
                sendMessage(sender, "<gray>Added permission <aqua>$permission <gray>to <aqua>${target.name}<gray>!")
                if (target is Player) {
                    refreshPermissions(core, target)
                    sendMessage(target, "<gray>Your permissions have been updated!")
                }
            }
            "remove" -> {
                if (args.size != 3) return sendMessage(sender, "<red>Invalid usage! Do <gray>/permission remove <<gray>player<red>> <<gray>permission<red>>!")
                val permission = args[2]
                file.removePerm(permission)
                sendMessage(sender, "<gray>Removed permission <aqua>$permission <gray>from <aqua>${target.name}<gray>!")
                if (target is Player) {
                    refreshPermissions(core, target)
                    sendMessage(target, "<gray>Your permissions have been updated!")
                }
            }
            "list" -> {
                sendMessage(sender, "<aqua>${target.name}<gray>'s permissions: <aqua>${file.getPerms().joinToString("<gray>, <aqua>")}")
            }
            else -> {
                sendMessage(sender, "<red>Invalid mode! Valid modes: <gray>add<red>, <gray>remove<red>, <gray>list")
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