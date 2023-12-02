package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.addDiamonds
import es.skepz.hydrogen.utils.getUserFile
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.StringUtil

class WithdrawCommand(val core: Hydrogen) : CoreCMD(core, "withdraw", "&c/withdraw <&7amount&c|&7all&c>",
    1, "none", true, true) {

    // TODO: full inventory = lose money

    override fun run() {
        if (!core.files.config.cfg.getBoolean("economy.money-is-diamonds"))
            return sendMessage(sender, "&cThis command is not enabled! (The economy is not based on diamonds)")

        val player = getPlayer()!!

        val moneySymbol = core.files.getMoneySymbol()

        val file = getUserFile(core, player)

        val bal = file.getBal()

        if (args[0] == "all") {
            if (bal == 0) return sendMessage(sender, "&cYou don't have any money!")

            var blocks = bal / 9
            var diamonds = bal % 9

            if (args.size == 2) {
                when (args[1]) {
                    "diamonds", "d", "diamond" -> {
                        diamonds += blocks * 9
                        blocks = 0
                    }
                }
            }

            file.setBal(0)

            val (unaddedBlocks, unaddedDiamonds) = addDiamonds(core, player, blocks, diamonds)

            if (unaddedBlocks > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedBlocks &cblocks as your inventory was too full!")
                file.addToBal(unaddedBlocks * 9)
            }

            if (unaddedDiamonds > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedDiamonds &cdiamonds as your inventory was too full!")
                file.addToBal(unaddedDiamonds)
            }

            sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b${bal - unaddedDiamonds - (unaddedBlocks * 9)}&7! Balance: &3$moneySymbol&b${file.getBal()}&7")

            return
        }

        val amount = args[0].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount!")
        if (args.size == 2 && (args[1] == "blocks" || args[1] == "b" || args[1] == "block")) {
            val total = amount * 9
            if (total > bal) return sendMessage(sender, "&cYou don't have enough money!")
            file.setBal(bal - total)
            val (unaddedBlocks, _) = addDiamonds(core, player, amount, 0)

            if (unaddedBlocks > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedBlocks &cblocks as your inventory was too full!")
                file.addToBal(unaddedBlocks * 9)
            }

            sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b${total - (unaddedBlocks * 9)}&7! Balance: &3$moneySymbol&b${file.getBal()}&7")
        } else {
            if (amount > bal) return sendMessage(sender, "&cYou don't have enough money!")
            file.setBal(bal - amount)
            val (_, unaddedDiamonds) = addDiamonds(core, player, 0, amount)

            if (unaddedDiamonds > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedDiamonds &cdiamonds as your inventory was too full!")
                file.addToBal(unaddedDiamonds)
            }

            val totalChanged = amount - unaddedDiamonds

            sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b$totalChanged&7! Balance: &3$moneySymbol&b${file.getBal()}&7")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        when (args.size) {
            1 -> StringUtil.copyPartialMatches(args[0], listOf("all"), completions)
            2 -> StringUtil.copyPartialMatches(args[1], listOf("blocks", "diamonds"), completions)
        }
        return completions
    }

}