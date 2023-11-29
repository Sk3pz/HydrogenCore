package es.skepz.hydrogen.commands.`fun`

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD

class HatCommand(val core: Hydrogen) : CoreCMD(core, "hat", "/hat", 0,
    "hydrogen.command.hat", true, false) {

    override fun run() {
        val player = getPlayer()!!

        val item = player.inventory.itemInMainHand
        if (item.type.isAir) {
            sendMessage(sender, "&cYou must be holding something in your hand to use this command!")
            return
        }

        val helmet = player.inventory.helmet
        player.inventory.helmet = item
        player.inventory.setItemInMainHand(helmet)
        sendMessage(sender, "&7You have put &b${item.type.name}&7 on your head!")
    }
}