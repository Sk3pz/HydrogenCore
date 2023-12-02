package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class PayCommand(val core: Hydrogen) : CoreCMD(core, "pay", "&c/pay <&7player&c> <&7amount&c>",
    2, "none", true, true) {

    override fun run() {
        val player = getPlayer() ?: return
        val target = args[0]

        val moneySymbol = core.files.getMoneySymbol()

        // get the player if they exist
        val targetPlayer = core.server.getPlayer(target) ?:
            return sendMessage(sender, "&cThat player does not exist or is not online!")

        // get the amount specified by the user
        var amount = args[1].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount.")

        if (args.size == 2 && (args[1] == "blocks" || args[1] == "block" || args[1] == "b")) {
            amount *= 9
        }

        // get the player's user file
        val file = UserFile(core, player)

        // get the target's user file
        val targetFile = UserFile(core, targetPlayer)

        val balance = file.getBal()

        if (amount > balance) {
            return sendMessage(sender, "&cYou do not have enough money to do that.")
        }

        targetFile.addToBal(amount)
        file.rmFromBal(amount)
        sendMessage(sender, "&7You have sent &b$target &3$moneySymbol&b$amount&7.")
        sendMessage(targetPlayer, "&7You have received &3$moneySymbol&b$amount&7 from &b${player.name}&7.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }
}