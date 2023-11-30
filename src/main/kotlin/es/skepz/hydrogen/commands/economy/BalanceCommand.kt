package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.command.CommandSender

class BalanceCommand(val core: Hydrogen) : CoreCMD(core, "balance", "/balance",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val file = getUserFile(core, player)

        val moneySymbol = core.files.getMoneySymbol()

        sendMessage(sender, "&7Your balance is &3$moneySymbol&b${file.getBal()}&7.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}