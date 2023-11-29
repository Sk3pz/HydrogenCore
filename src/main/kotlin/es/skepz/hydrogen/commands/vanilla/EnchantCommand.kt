package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.*
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.ArrayList

class EnchantCommand(val core: Hydrogen) : CoreCMD(core, "enchant", "&c/enchant <&7enchantment&c> <&7level&c>",
    1, "hydrogen.command.enchant", true, true) {

    override fun run() {
        val enchantmentRaw = args[0].lowercase()
        val player = getPlayer()!!

        if (enchantmentRaw == "list") {
            val list = IMessage("&7Enchantments").newLine()
            val enchs = getEnchs()
            for (x in enchs.indices) {
                val ench = enchs[x]
                val tt = getEnchToolTip(getEnch(ench)!!)
                if (x == enchs.size-1) {
                    list.addHoverable("&b$ench&7.", tt)
                } else {
                    list.addHoverable("&b$ench&7, ", tt)
                }
            }
            list.newLine().newLine()
                .addHoverable(" &7&o[?] Hover over each enchantment to see more information!", "Hover over each enchantment to see more information!")
                .send(player)
            return
        }

        if (args.size != 2) {
            sendMessage(sender, "&cInvalid usage! Do &7/enchant list&c to see a list of enchantments!")
            return
        }

        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            sendMessage(sender, "&cYou must be holding something in your hand to use this command!")
            return;
        }

        val enchantment = getEnch(enchantmentRaw) ?: return sendMessage(sender, "&cInvalid Enchantment! Do &7/enchant list&c to see a list of enchantments!")

        val valueRaw = args[1]
        for (c in valueRaw) {
            if (c !in '0'..'9') {
                return sendMessage(sender, "&7$valueRaw&c is not a valid number!")
            }
        }
        val parsed = valueRaw.toInt()

        val meta = item.itemMeta
        meta.addEnchant(enchantment, parsed, true)
        item.itemMeta = meta

        sendMessage(sender, "&7Enchanted your item!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = ArrayList<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], getEnchs(), completions)
        }

        return completions
    }

}