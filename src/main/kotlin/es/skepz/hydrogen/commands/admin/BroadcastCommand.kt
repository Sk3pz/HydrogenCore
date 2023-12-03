package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.serverBroadcast
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD

class BroadcastCommand(val core: Hydrogen) : CoreCMD(core, "broadcast", "<red>/broadcast <<gray>message<red>>", 1,
    "hydrogen.command.broadcast", false, false) {

    override fun run() {
        val message = args.joinToString(" ")
        serverBroadcast(message)
    }

}