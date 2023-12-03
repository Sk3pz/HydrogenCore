package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class EcoCommand(val core: Hydrogen) : CoreCMD(core, "eco", "<red>/eco <<gray>give<red>|<gray>take<red>|<gray>set<red>|<gray>check<red>> <<gray>player<red>> <<gray>amount<red>>",
    2, "hydrogen.command.eco", false, true) {

    override fun run() {
        val mode = args[0]
        val target = args[1]

        // get the player if they exist, if not attempt to get offline player
        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's user file if it exists
        val file = getOfflineUserFileRaw(core, targetPlayer.uniqueId) ?:
            return sendMessage(sender, "<red>That player does not exist or has not played before!")

        val moneySymbol = core.files.getMoneySymbol()
        
        when (mode) {
            "give" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "<red>Invalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "<red>Invalid amount.")
                file.cfg.set("balance", file.cfg.getInt("balance") + amount)
                file.save()
                sendMessage(sender, "<gray>You have given <aqua>$target <dark_aqua>$moneySymbol<aqua>$amount<gray>.")
                if (targetPlayer is Player) {
                    sendMessage(targetPlayer, "<gray>You have been given <dark_aqua>$moneySymbol<aqua>$amount <gray>by <aqua>${sender.name}<gray>.")
                }
            }
            "take" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "<red>Invalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "<red>Invalid amount.")
                file.cfg.set("balance", file.cfg.getInt("balance") - amount)
                file.save()
                sendMessage(sender, "<gray>You have taken <dark_aqua>$moneySymbol<aqua>$amount <gray>from <aqua>$target<gray>.")
                if (targetPlayer is Player) {
                    sendMessage(targetPlayer, "<gray>You have had <dark_aqua>$moneySymbol<aqua>$amount <gray>taken by <aqua>${sender.name}<gray>.")
                }
            }
            "set" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "<red>Invalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "<red>Invalid amount.")
                file.cfg.set("balance", amount)
                file.save()
                sendMessage(sender, "<gray>You have set <aqua>$target<gray>'s balance to <dark_aqua>$moneySymbol<aqua>$amount<gray>.")
                if (targetPlayer is Player) {
                    sendMessage(targetPlayer, "<gray>Your balance has been set to <dark_aqua>$moneySymbol<aqua>$amount <gray>by <aqua>${sender.name}<gray>.")
                }
            }
            "check" -> {
                sendMessage(sender, "<gray>$target<gray>'s balance: <dark_aqua>$moneySymbol<aqua>${file.cfg.getInt("balance")}<gray>.")
            }
            else -> {
                sendMessage(sender, "<red>Invalid mode. Valid modes: give, take, set.")
            }
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], listOf("set", "give", "take", "check"), completions)
        } else if (args.size == 2) {
            val players = org.bukkit.Bukkit.getServer().onlinePlayers
            val names = java.util.ArrayList<String>()
            for (p in players) {
                names.add(p.name)
            }
            StringUtil.copyPartialMatches(args[1], names, completions)
        }

        return completions
    }
}