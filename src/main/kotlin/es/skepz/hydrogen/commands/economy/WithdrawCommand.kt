package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getUserFile
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

class WithdrawCommand(val core: Hydrogen) : CoreCMD(core, "withdraw", "&c/withdraw <&7amount&c|&7all&c>",
    1, "none", true, true) {

    // TODO: remove item meta on release
    fun addDiamonds(player: Player, blocks: Int, diamonds: Int) {
        val moneySymbol = core.files.getMoneySymbol()
        val blockItem = ItemStack(Material.DIAMOND_BLOCK, blocks)
        val bmeta = blockItem.itemMeta
        bmeta.displayName(Component.text(colorize("&bBlocks")))
        bmeta.lore(listOf(Component.text(colorize("&7Backed by the &b&lHydrogen &7economy!")),
            Component.text(colorize("&7Value: &3$moneySymbol&b9 &7/ block")),
            Component.text(colorize("&eCertified Shiny!"))))
        blockItem.itemMeta = bmeta
        val diamondItem = ItemStack(Material.DIAMOND_BLOCK, diamonds)
        val dmeta = diamondItem.itemMeta
        dmeta.displayName(Component.text(colorize("&bDiamonds")))
        dmeta.lore(listOf(Component.text(colorize("&7Backed by the &b&lHydrogen &7economy!")),
            Component.text(colorize("&7Value: &3$moneySymbol&b1 &7/ item")),
            Component.text(colorize("&eCertified Shiny!"))))
        diamondItem.itemMeta = dmeta

        player.inventory.addItem(diamondItem)
        player.inventory.addItem(blockItem)
    }

    override fun run() {
        if (core.files.config.cfg.getBoolean("economy.money-is-diamonds"))
            return sendMessage(sender, "&cThis command is not enabled! (The economy is not based on diamonds)")

        val player = getPlayer()!!

        val moneySymbol = core.files.getMoneySymbol()

        val file = getUserFile(core, player)

        val bal = file.getBal()

        if (args[0] == "all") {
            if (bal == 0) return sendMessage(sender, "&cYou don't have any money!")
            val blocks = bal / 9
            val diamonds = bal % 9

            file.setBal(0)

            addDiamonds(player, blocks, diamonds)
            sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b$bal&7!")

            return
        }

        if (args.size != 2) {
            return sendMessage(sender, "&cInvalid usage! &7/withdraw amount <&7blocks&c|&7diamonds&c>")
        }

        val amount = args[0].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount!")
        val type = args[1]
        when (type) {
            "blocks" -> {
                val total = amount * 9
                if (total > bal) return sendMessage(sender, "&cYou don't have enough money!")
                file.setBal(bal - total)
                addDiamonds(player, amount, 0)
                sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b$total&7!")
            }
            "diamonds" -> {
                if (amount > bal) return sendMessage(sender, "&cYou don't have enough money!")
                file.setBal(bal - amount)
                addDiamonds(player, 0, amount)
                sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b$amount&7!")
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