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
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.StringUtil

class WithdrawCommand(val core: Hydrogen) : CoreCMD(core, "withdraw", "&c/withdraw <&7amount&c|&7all&c>",
    1, "none", true, true) {

    fun getBlocksCanBeAdded(player: Player): Int {
        var total = 0
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type == Material.AIR) {
                total += 64
                continue
            }
            if (slot.type == Material.DIAMOND_BLOCK && slot.itemMeta.persistentDataContainer.has(core.diamondKey, PersistentDataType.INTEGER)) {
                total += 64 - slot.amount
            }
        }
        return total
    }

    fun getDiamondsCanBeAdded(player: Player): Int {
        var total = 0
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type == Material.AIR) {
                total += 64
                continue
            }
            if (slot.type == Material.DIAMOND && slot.itemMeta.persistentDataContainer.has(core.diamondKey, PersistentDataType.INTEGER)) {
                total += 64 - slot.amount
            }
        }
        return total
    }

    fun addDiamonds(player: Player, blocks: Int, diamonds: Int): Pair<Int, Int> {
        val maxDiamonds = getDiamondsCanBeAdded(player)
        val maxBlocks = getBlocksCanBeAdded(player)

        var blocksToAdd = blocks
        var unaddedBlocks = 0

        var diamondsToAdd = diamonds
        var unaddedDiamonds = 0

        if (blocks > maxBlocks) {
            blocksToAdd = maxBlocks
            unaddedBlocks = blocks - maxBlocks
        }

        if (diamonds > maxDiamonds) {
            diamondsToAdd = maxDiamonds
            unaddedDiamonds = diamonds - maxDiamonds
        }

        val moneySymbol = core.files.getMoneySymbol()
        val blockItem = ItemStack(Material.DIAMOND_BLOCK, blocksToAdd)
        val bmeta = blockItem.itemMeta
        bmeta.displayName(Component.text(colorize("&bBlocks")))
        bmeta.lore(listOf(Component.text(colorize("&7Backed by the &b&lHydrogen &7economy!")),
            Component.text(colorize("&7Value: &3$moneySymbol&b9 &7/ block")),
            Component.text(colorize("&eCertified Shiny!"))))
        bmeta.persistentDataContainer.set(core.diamondKey, PersistentDataType.INTEGER, 1)
        blockItem.itemMeta = bmeta
        val diamondItem = ItemStack(Material.DIAMOND_BLOCK, diamondsToAdd)
        val dmeta = diamondItem.itemMeta
        dmeta.displayName(Component.text(colorize("&bDiamonds")))
        dmeta.lore(listOf(Component.text(colorize("&7Backed by the &b&lHydrogen &7economy!")),
            Component.text(colorize("&7Value: &3$moneySymbol&b1 &7/ item")),
            Component.text(colorize("&eCertified Shiny!"))))
        dmeta.persistentDataContainer.set(core.diamondKey, PersistentDataType.INTEGER, 1)
        diamondItem.itemMeta = dmeta

        player.inventory.addItem(diamondItem)
        player.inventory.addItem(blockItem)

        return Pair(unaddedBlocks, unaddedDiamonds)
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

            val (unaddedBlocks, unaddedDiamonds) = addDiamonds(player, blocks, diamonds)

            if (unaddedBlocks > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedBlocks &cblocks as your inventory was too full!")
                file.addToBal(unaddedBlocks * 9)
            }

            if (unaddedDiamonds > 0) {
                sendMessage(sender, "&cYou couldn't withdraw &b$unaddedDiamonds &cdiamonds as your inventory was too full!")
                file.addToBal(unaddedDiamonds)
            }

            sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b${bal - unaddedDiamonds - (unaddedBlocks * 9)}&7!")

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
                val (unaddedBlocks, _) = addDiamonds(player, amount, 0)

                if (unaddedBlocks > 0) {
                    sendMessage(sender, "&cYou couldn't withdraw &b$unaddedBlocks &cblocks as your inventory was too full!")
                    file.addToBal(unaddedBlocks * 9)
                }

                sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b${total - (unaddedBlocks * 9)}&7!")
            }
            "diamonds" -> {
                if (amount > bal) return sendMessage(sender, "&cYou don't have enough money!")
                file.setBal(bal - amount)
                val (_, unaddedDiamonds) = addDiamonds(player, 0, amount)

                if (unaddedDiamonds > 0) {
                    sendMessage(sender, "&cYou couldn't withdraw &b$unaddedDiamonds &cdiamonds as your inventory was too full!")
                    file.addToBal(unaddedDiamonds)
                }

                val totalChanged = amount - unaddedDiamonds

                sendMessage(sender, "&7You have withdrawn &3$moneySymbol&b$totalChanged&7!")
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