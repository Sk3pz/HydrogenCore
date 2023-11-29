package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.genUsage
import es.skepz.hydrogen.skepzlib.invalidCmdUsage
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.bukkit.util.StringUtil
import java.util.ArrayList

class MetaCommand(val core: Hydrogen) : CoreCMD(core, "meta", "&c/meta <&7name&c|&7lore&c|&7unbreakable&c|&7trim&c> <&7data&c>",
    1, "hydrogen.command.meta", true, true) {

    override fun run() {
        val modeRaw = args[0].lowercase()
        args.removeAt(0)
        val player = getPlayer()!!

        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            sendMessage(sender, "&cYou must be holding something in your hand to use this command!")
            return;
        }
        val meta = item.itemMeta

        when (modeRaw) {
            "unbreakable" -> { // /meta unbreakable
                meta.isUnbreakable = !meta.isUnbreakable
                sendMessage(sender, "&7Item is now ${if (meta.isUnbreakable) "&aunbreakable" else "&cbreakable"}")
            }
            "upgrade" -> { // /meta upgrade
                item.type = when (item.type) {
                    Material.DIAMOND_HELMET -> Material.NETHERITE_HELMET
                    Material.DIAMOND_CHESTPLATE -> Material.NETHERITE_CHESTPLATE
                    Material.DIAMOND_LEGGINGS -> Material.NETHERITE_LEGGINGS
                    Material.DIAMOND_BOOTS -> Material.NETHERITE_BOOTS
                    Material.DIAMOND_AXE -> Material.NETHERITE_AXE
                    Material.DIAMOND_PICKAXE -> Material.NETHERITE_PICKAXE
                    Material.DIAMOND_SWORD -> Material.NETHERITE_SWORD
                    Material.DIAMOND_SHOVEL -> Material.NETHERITE_SHOVEL
                    Material.DIAMOND_HOE -> Material.NETHERITE_HOE
                    else -> {
                        sendMessage(sender, "&cYou can only upgrade diamond tools and armor!")
                        return
                    }
                }
                sendMessage(sender, "&7Item upgraded!")
            }
            "trim" -> { // /meta trim <trim name> <material>
                // check if item is armor
                if (!item.type.name.endsWith("_HELMET") && !item.type.name.endsWith("_CHESTPLATE") && !item.type.name.endsWith("_LEGGINGS") && !item.type.name.endsWith("_BOOTS")) {
                    sendMessage(sender, "&cYou can only upgrade armor!")
                    return
                }

                val ameta = meta as ArmorMeta

                if (args[0] == "remove") {
                    ameta.trim = null
                    sendMessage(sender, "&7Trim removed!")
                    return
                }

                if (args.size < 2) {
                    invalidUse()
                    return
                }

                val trimName = when (args[0]) {
                    "sentry" -> TrimPattern.SENTRY
                    "dune" -> TrimPattern.DUNE
                    "coast" -> TrimPattern.COAST
                    "wild" -> TrimPattern.WILD
                    "ward" -> TrimPattern.WARD
                    "eye" -> TrimPattern.EYE
                    "vex" -> TrimPattern.VEX
                    "tide" -> TrimPattern.TIDE
                    "snout" -> TrimPattern.SNOUT
                    "rib" -> TrimPattern.RIB
                    "spire" -> TrimPattern.SPIRE
                    "wayfinder" -> TrimPattern.WAYFINDER
                    "shaper" -> TrimPattern.SHAPER
                    "silence" -> TrimPattern.SILENCE
                    "raiser" -> TrimPattern.RAISER
                    "host" -> TrimPattern.HOST
                    else -> {
                        sendMessage(sender, "&cInvalid trim name!")
                        return
                    }
                }
                val material: TrimMaterial = when (args[1]) {
                    "iron" -> TrimMaterial.IRON
                    "gold" -> TrimMaterial.GOLD
                    "diamond" -> TrimMaterial.DIAMOND
                    "netherite" -> TrimMaterial.NETHERITE
                    "amethyst" -> TrimMaterial.AMETHYST
                    "copper" -> TrimMaterial.COPPER
                    "emerald" -> TrimMaterial.EMERALD
                    "lapis" -> TrimMaterial.LAPIS
                    "quartz" -> TrimMaterial.QUARTZ
                    "redstone" -> TrimMaterial.REDSTONE
                    else -> {
                        sendMessage(sender, "&cInvalid trim material!")
                        return
                    }
                }

                ameta.trim = ArmorTrim(material, trimName)
                sendMessage(sender, "&7Trim updated!")
            }
            "name" -> { // /meta name <name>
                if (args.size < 1) {
                    // reset the items name to the default
                    meta.displayName(null)
                    return
                }
                val data = colorize(args.joinToString(" "))
                meta.displayName(Component.text(data))
                sendMessage(sender, "&7Item name set to: &f$data")
            }
            "lore" -> { // /meta lore <lore>
                if (args.size < 1) {
                    // reset the items lore to the default
                    meta.lore(null)
                    return
                }
                val data = args.joinToString(" ")
                val list = mutableListOf<Component>()
                val lore = data.split("&n")
                for (line in lore) {
                    list.add(Component.text(colorize(line)))
                }
                meta.lore(list)
                sendMessage(sender, "&7Item lore set to: &f$data")
            }
            else -> {
                invalidUse()
                return
            }
        }
        item.itemMeta = meta
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = ArrayList<String>()

        when (args.size) {
            1 -> StringUtil.copyPartialMatches(args[0], listOf("name", "lore", "unbreakable", "trim", "upgrade"), completions)
            2 -> {
                if (args[0] == "trim") {
                    StringUtil.copyPartialMatches(args[1], listOf("sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout", "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host"), completions)
                }
            }
            3 -> {
                if (args[0] == "trim") {
                    StringUtil.copyPartialMatches(args[2], listOf("iron", "gold", "diamond", "netherite", "amethyst", "copper", "emerald", "lapis", "quartz", "redstone"), completions)
                }
            }
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], listOf("name", "lore", "unbreakable", "trim", "upgrade"), completions)
        }

        return completions
    }

}