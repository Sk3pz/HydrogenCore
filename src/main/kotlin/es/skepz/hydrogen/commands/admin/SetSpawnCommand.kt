package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.setSpawn
import org.bukkit.command.CommandSender
import java.util.ArrayList

class SetSpawnCommand(val core: Hydrogen) : CoreCMD(core, "setspawn", "&c/setspawn",
    0, "hydrogen.command.setspawn", true, false) {

    override fun init() {
        // not used for this function
    }

    override fun run() {
        val loc = getPlayer()!!.location

        setSpawn(core, loc)
        sendMessage(sender, "&7Spawn set to your position.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}