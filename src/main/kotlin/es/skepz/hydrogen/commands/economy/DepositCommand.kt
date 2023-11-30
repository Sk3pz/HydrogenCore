package es.skepz.hydrogen.commands.economy

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.addDiamonds
import es.skepz.hydrogen.utils.getUserFile
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.StringUtil

class DepositCommand(val core: Hydrogen) : CoreCMD(core, "deposit", "&c/deposit <&7amount&c|&7all&c>",
    1, "none", true, true) {

    fun removeBlocks(player: Player, amt: Int): Int {
        var amount = amt
        var removed = 0
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type == Material.AIR) continue
            if (slot.type == Material.DIAMOND_BLOCK) {
                if (slot.amount > amount) {
                    slot.amount -= amount
                    removed += amount
                    break
                } else {
                    amount -= slot.amount
                    removed += slot.amount
                    slot.amount = 0
                }
            }
        }
        return removed
    }

    fun removeDiamonds(player: Player, amt: Int): Int {
        var amount = amt
        var removed = 0
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type == Material.AIR) continue
            if (slot.type == Material.DIAMOND) {
                if (slot.amount > amount) {
                    slot.amount -= amount
                    removed += amount
                    break
                } else {
                    amount -= slot.amount
                    removed += slot.amount
                    slot.amount = 0
                }
            }
        }
        return removed
    }

    override fun run() {
        if (!core.files.config.cfg.getBoolean("economy.money-is-diamonds"))
            return sendMessage(sender, "&cThis command is not enabled! (The economy is not based on diamonds)")

        val player = getPlayer()!!

        val moneySymbol = core.files.getMoneySymbol()

        var foundBlocks = 0
        var foundDiamonds = 0

        for (item in player.inventory.storageContents) {
            if (item == null) continue
            if (item.type == Material.DIAMOND)
                foundDiamonds += item.amount
            if (item.type == Material.DIAMOND_BLOCK)
                foundBlocks += item.amount
        }

        val file = getUserFile(core, player)

        if (args[0] == "all") {
            val total = (foundBlocks * 9) + foundDiamonds

            if (total == 0) return sendMessage(sender, "&cYou don't have any diamonds or diamond blocks!")

            removeBlocks(player, foundBlocks)
            removeDiamonds(player, foundDiamonds)

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
            "blocks", "b", "block" -> {
                if (foundBlocks < amount) return sendMessage(sender, "&cYou don't have enough diamond blocks!")
                removeBlocks(player, amount)
                val total = amount * 9
                file.addToBal(total)
                sendMessage(sender, "&7You have deposited &3$moneySymbol&b$total&7!")
            }
            "diamonds", "d", "diamond" -> {
                if ((foundDiamonds + (foundBlocks * 9)) < amount) return sendMessage(sender, "&cYou don't have enough diamonds!")
                if (foundDiamonds < amount) {
                    val blocksToBreakDown = (amount - foundDiamonds) / 9
                    val remainder = (amount - foundDiamonds) % 9
                    removeBlocks(player, blocksToBreakDown + if (remainder > 0) 1 else 0)
                    if (remainder > 0) {
                        addDiamonds(core, player, 0, remainder)
                    }
                }

                removeDiamonds(player, amount)
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