package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.meta.Damageable

class RepairCommand(core: Hydrogen) : CoreCMD(core, "repair", "&c/repair",
    0, "hydrogen.command.repair", true, true) {

    override fun run() {
        val player = getPlayer()!!
        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            sendMessage(sender, "&cYou must be holding something in your hand to use this command!")
            return;
        }

        if (item.itemMeta !is Damageable) {
            sendMessage(sender, "&cThis item cannot be repaired!")
            return;
        }

        val meta = item.itemMeta as Damageable

        meta.damage = 0
        item.itemMeta = meta
        sendMessage(sender, "&7Item repaired!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }
}