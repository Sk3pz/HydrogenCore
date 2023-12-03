package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.ArrayList

class TpaCommand(val core: Hydrogen) : CoreCMD(core, "tpa", "<red>/tpa <<gray>player<red>>",
    1, "none", true, true) {

    override fun run() {
        // get target name
        val name = args[0]

        val target = Bukkit.getPlayer(name)
        if (target == null) {
            sendMessage(sender, "<red>That player either isn't online or doesn't exist!")
            return
        }

        val player = getPlayer()!!

        // send request message to the target
        IMessage("<gray><bold>Teleport Request<reset>\n")
            .add("<aqua>${player.name} <gray>Would like to teleport to you.<reset>\n")
            .addHoverableClickCmd("  <green>&oAccept<reset>\n", "/tpaccept ${player.name}", "<gray>Allow <aqua>${player.name} <gray>to teleport to your location")
            .addHoverableClickCmd("  <red>&oDeny<reset>", "/tpdeny ${player.name}", "<gray>Deny <aqua>${player.name} <gray>teleportation to your location")
            .send(target)


        // put the player into the request map

        // players can only have one outgoing request at a time
        if (core.tpaRequests.containsKey(player.uniqueId)) {
            sendMessage(sender, "<red>Canceled your previous tpa request. (you can only have 1 outgoing request at a time.)")
        }
        core.tpaRequests[player.uniqueId] = target.uniqueId
        if (core.tpahereRequests.containsKey(player.uniqueId)) {
            core.tpahereRequests.remove(player.uniqueId)
            sendMessage(sender, "<red>Canceled your previous tpahere request. (you can only have 1 outgoing request at a time.)")
        }

        sendMessage(sender, "<gray>You have requested to teleport to <aqua>${target.name}<gray>!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}