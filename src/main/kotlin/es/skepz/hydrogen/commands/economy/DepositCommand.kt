package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

class DepositCommand(val core: Hydrogen) : CoreCMD(core, "deposit", "&c/deposit <&7amount&c|&7all&c>",
    1, "none", true, true) {

    override fun run() {
        if (core.files.config.cfg.getBoolean("economy.money-is-diamonds"))
            return sendMessage(sender, "&cThis command is not enabled! (The economy is not based on diamonds)")

        val player = getPlayer()!!

        val moneySymbol = core.files.getMoneySymbol()

        var foundBlocks = 0
        var foundDiamonds = 0

        for (item in player.inventory.storageContents) {
            if (item == null) continue
            if (item.type == Material.DIAMOND) foundDiamonds += item.amount
            if (item.type == Material.DIAMOND_BLOCK) foundBlocks += item.amount
        }

        val file = getUserFile(core, player)

        if (args[0] == "all") {
            val total = (foundBlocks * 9) + foundDiamonds

            if (total == 0) return sendMessage(sender, "&cYou don't have any diamonds or diamond blocks!")

            player.inventory.remove(ItemStack(Material.DIAMOND_BLOCK, foundBlocks))
            player.inventory.remove(ItemStack(Material.DIAMOND_BLOCK, foundBlocks))

            file.addToBal(total)
            sendMessage(sender, "&7You have deposited &3$moneySymbol&b$total&7!")
            return
        }

        if (args.size != 2) {
            return sendMessage(sender, "&cInvalid usage! &7/withdraw amount <&7blocks&c|&7diamonds&c>")
        }

        val amount = args[0].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount!")
        val type = args[1]
        when (type) {
            "blocks" -> {
                if (foundBlocks < amount) return sendMessage(sender, "&cYou don't have enough diamond blocks!")
                player.inventory.remove(ItemStack(Material.DIAMOND_BLOCK, amount))
                val total = amount * 9
                file.addToBal(total)
                sendMessage(sender, "&7You have deposited &3$moneySymbol&b$total&7!")
            }
            "diamonds" -> {
                if (foundDiamonds < amount) return sendMessage(sender, "&cYou don't have enough diamonds!")
                player.inventory.remove(ItemStack(Material.DIAMOND, amount))
                file.addToBal(amount)
                sendMessage(sender, "&7You have deposited &3$moneySymbol&b$amount&7!")
            }
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