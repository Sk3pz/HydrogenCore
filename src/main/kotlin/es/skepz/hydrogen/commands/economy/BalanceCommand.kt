package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class BalanceCommand(val core: Hydrogen) : CoreCMD(core, "balance", "<red>/balance",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val file = UserFile(core, player)

        val moneySymbol = core.files.getMoneySymbol()

        sendMessage(sender, "<gray>Your balance is <dark_aqua>$moneySymbol<aqua>${file.getBal()}<gray>.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}