package es.skepz.hydrogen.commands.vanilla

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.*
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.ArrayList

class EnchantCommand(val core: Hydrogen) : CoreCMD(core, "enchant", "<red>/enchant <<gray>enchantment<red>> <<gray>level<red>>",
    1, "hydrogen.command.enchant", true, true) {

    override fun run() {
        val enchantmentRaw = args[0].lowercase()
        val player = getPlayer()!!

        if (enchantmentRaw == "list") {
            val list = IMessage("<gray>Enchantments").newLine()
            val enchs = getEnchs()
            for (x in enchs.indices) {
                val ench = enchs[x]
                val tt = getEnchToolTip(getEnch(ench)!!)
                if (x == enchs.size-1) {
                    list.addHoverable("<aqua>$ench<gray>.", tt)
                } else {
                    list.addHoverable("<aqua>$ench<gray>, ", tt)
                }
            }
            list.newLine().newLine()
                .addHoverable(" <gray>&o[?] Hover over each enchantment to see more information!", "Hover over each enchantment to see more information!")
                .send(player)
            return
        }

        if (args.size != 2) {
            sendMessage(sender, "<red>Invalid usage! Do <gray>/enchant list<red> to see a list of enchantments!")
            return
        }

        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            sendMessage(sender, "<red>You must be holding something in your hand to use this command!")
            return;
        }

        val enchantment = getEnch(enchantmentRaw) ?: return sendMessage(sender, "<red>Invalid Enchantment! Do <gray>/enchant list<red> to see a list of enchantments!")

        val valueRaw = args[1]
        for (c in valueRaw) {
            if (c !in '0'..'9') {
                return sendMessage(sender, "<gray>$valueRaw<red> is not a valid number!")
            }
        }
        val parsed = valueRaw.toInt()

        val meta = item.itemMeta
        meta.addEnchant(enchantment, parsed, true)
        item.itemMeta = meta

        sendMessage(sender, "<gray>Enchanted your item!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = ArrayList<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], getEnchs(), completions)
        }

        return completions
    }

}