package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.ArrayList

class TpaCommand(val core: Hydrogen) : CoreCMD(core, "tpa", "&c/tpa <player>",
    1, "none", true, true) {

    override fun run() {
        // get target name
        val name = args[0]

        val target = Bukkit.getPlayer(name)
        if (target == null) {
            sendMessage(sender, "&cThat player either isn't online or doesn't exist!")
            return
        }

        val player = getPlayer()!!

        // send request message to the target
        IMessage("&7&lTeleport Request&r\n")
            .add("&b${player.name} &7Would like to teleport to you.&r\n")
            .addHoverableClickCmd("  &a&oAccept&r\n", "/tpaccept ${player.name}", "&7Allow &b${player.name} &7to teleport to your location")
            .addHoverableClickCmd("  &c&oDeny&r", "/tpdeny ${player.name}", "&7Deny &b${player.name} &7teleportation to your location")
            .send(target)


        // put the player into the request map

        // players can only have one outgoing request at a time
        if (core.tpaRequests.containsKey(player.uniqueId)) {
            sendMessage(sender, "&cCanceled your previous tpa request. (you can only have 1 outgoing request at a time.)")
        }
        core.tpaRequests[player.uniqueId] = target.uniqueId
        if (core.tpahereRequests.containsKey(player.uniqueId)) {
            core.tpahereRequests.remove(player.uniqueId)
            sendMessage(sender, "&cCanceled your previous tpahere request. (you can only have 1 outgoing request at a time.)")
        }

        sendMessage(sender, "&7You have requested to teleport to &b${target.name}&7!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getServer().onlinePlayers.map { p -> p.name }, completions)
        }
        return completions
    }

}