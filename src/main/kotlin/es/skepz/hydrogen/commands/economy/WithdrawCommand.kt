package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.addDiamonds
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class WithdrawCommand(val core: Hydrogen) : CoreCMD(core, "withdraw", "<red>/withdraw <<gray>amount<red>|<gray>all<red>>",
    1, "none", true, true) {

    // TODO: full inventory = lose money

    override fun run() {
        if (!core.files.config.cfg.getBoolean("economy.money-is-diamonds"))
            return sendMessage(sender, "<red>This command is not enabled! (The economy is not based on diamonds)")

        val player = getPlayer()!!

        val moneySymbol = core.files.getMoneySymbol()

        val file = UserFile(core, player)

        val bal = file.getBal()

        if (args[0] == "all") {
            if (bal == 0) return sendMessage(sender, "<red>You don't have any money!")

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
                sendMessage(sender, "<red>You couldn't withdraw <aqua>$unaddedBlocks <red>blocks as your inventory was too full!")
                file.addToBal(unaddedBlocks * 9)
            }

            if (unaddedDiamonds > 0) {
                sendMessage(sender, "<red>You couldn't withdraw <aqua>$unaddedDiamonds <red>diamonds as your inventory was too full!")
                file.addToBal(unaddedDiamonds)
            }

            sendMessage(sender, "<gray>You have withdrawn <dark_aqua>$moneySymbol<aqua>${bal - unaddedDiamonds - (unaddedBlocks * 9)}<gray>! Balance: <dark_aqua>$moneySymbol<aqua>${file.getBal()}<gray>")

            return
        }

        val amount = args[0].toIntOrNull() ?: return sendMessage(sender, "<red>Invalid amount!")
        if (args.size == 2 && (args[1] == "blocks" || args[1] == "b" || args[1] == "block")) {
            val total = amount * 9
            if (total > bal) return sendMessage(sender, "<red>You don't have enough money!")
            file.setBal(bal - total)
            val (unaddedBlocks, _) = addDiamonds(core, player, amount, 0)

            if (unaddedBlocks > 0) {
                sendMessage(sender, "<red>You couldn't withdraw <aqua>$unaddedBlocks <red>blocks as your inventory was too full!")
                file.addToBal(unaddedBlocks * 9)
            }

            sendMessage(sender, "<gray>You have withdrawn <dark_aqua>$moneySymbol<aqua>${total - (unaddedBlocks * 9)}<gray>! Balance: <dark_aqua>$moneySymbol<aqua>${file.getBal()}<gray>")
        } else {
            if (amount > bal) return sendMessage(sender, "<red>You don't have enough money!")
            file.setBal(bal - amount)
            val (_, unaddedDiamonds) = addDiamonds(core, player, 0, amount)

            if (unaddedDiamonds > 0) {
                sendMessage(sender, "<red>You couldn't withdraw <aqua>$unaddedDiamonds <red>diamonds as your inventory was too full!")
                file.addToBal(unaddedDiamonds)
            }

            val totalChanged = amount - unaddedDiamonds

            sendMessage(sender, "<gray>You have withdrawn <dark_aqua>$moneySymbol<aqua>$totalChanged<gray>! Balance: <dark_aqua>$moneySymbol<aqua>${file.getBal()}<gray>")
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