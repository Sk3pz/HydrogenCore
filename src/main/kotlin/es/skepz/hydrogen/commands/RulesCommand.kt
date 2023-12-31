package es.skepz.hydrogen.commands

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class RulesCommand(val core: Hydrogen) : CoreCMD(core, "rules", "<red>/rules", 0,
    "none", true, false) {

    override fun run() {
        val player = getPlayer()!!

        // get the rules
        val rules = core.files.rules.cfg.getStringList("rules")

        // send the rules to the player
        val listDisplay = rules.joinToString("\n") { "<gray>- <gray>$it" }

        sendMessage(player, "<gray><bold>Rules:\n$listDisplay")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}