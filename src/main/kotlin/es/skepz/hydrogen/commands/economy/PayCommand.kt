package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class PayCommand(val core: Hydrogen) : CoreCMD(core, "pay", "/pay <player> <amount>",
    2, "none", true, true) {

    override fun run() {
        val player = getPlayer() ?: return
        val target = args[0]

        // get the player if they exist
        val targetPlayer = core.server.getPlayer(target) ?:
            return sendMessage(sender, "&cThat player does not exist or is not online!")

        // get the amount specified by the user
        val amount = args[1].toLongOrNull() ?: return sendMessage(sender, "&cInvalid amount.")

        // get the player's user file
        val file = getUserFile(core, player)

        // get the target's user file
        val targetFile = getUserFile(core, targetPlayer)

        val balance = file.getBal()

        if (amount > balance) {
            return sendMessage(sender, "&cYou do not have enough money to do that.")
        }

        targetFile.addToBal(amount)
        file.rmFromBal(amount)
        sendMessage(sender, "&7You have sent &b$target &7$&b$amount&7.")
        sendMessage(targetPlayer, "&7You have received $&b$amount&7 from &b${player.name}&7.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val players = org.bukkit.Bukkit.getServer().onlinePlayers
        val names = java.util.ArrayList<String>()
        for (p in players) {
            names.add(p.name)
        }
        StringUtil.copyPartialMatches(args[0], names, completions)

        return completions
    }
}