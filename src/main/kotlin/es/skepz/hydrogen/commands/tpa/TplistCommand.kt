package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.collections.HashMap

class TplistCommand(val core: Hydrogen) : CoreCMD(core, "tplist", "&c/tplist",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!

        // get all current tpa requests involving the user
        val requests = HashMap<UUID, UUID>()
        val hereRequests = HashMap<UUID, UUID>()
        for ((r, t) in core.tpaRequests) {
            if (t == player.uniqueId) {
                requests[r] = t
            }
        }
        for ((r, t) in core.tpahereRequests) {
            if (t == player.uniqueId) {
                hereRequests[r] = t
            }
        }

        // multiple requests, give them a list
        val imsg = IMessage("&7&lYour tpa requests:\n")
        if (requests.isNotEmpty()) {
            imsg.add(" &7> Requesting to teleport to you:\n")
            var x = 0
            for ((r, _) in requests) {
                val p = Bukkit.getPlayer(r)
                if (p == null) {
                    core.tpaRequests.remove(r)
                    continue
                }
                imsg.add(" | &b${p.name} ")
                    .addHoverableClickCmd("&aAccept ", "/tpaccept ${p.name}", "&7Accept the request from &b${p.name}")
                    .addHoverableClickCmd("&cDeny", "/tpdeny ${p.name}", "&7Deny the request from &b${p.name}")
                if (x != requests.size - 1) {
                    imsg.add("\n")
                }
                x++
            }
        }
        if (hereRequests.isNotEmpty()) {
            imsg.add(" &7> Requesting to teleport you to them:\n")
            var x = 0
            for ((r, _) in hereRequests) {
                val p = Bukkit.getPlayer(r)
                if (p == null) {
                    core.tpahereRequests.remove(r)
                    continue
                }
                imsg.add(" | &b${p.name} ")
                    .addHoverableClickCmd("&aAccept ", "/tpaccept ${p.name}", "&7Accept the request from &b${p.name}")
                    .addHoverableClickCmd("&cDeny", "/tpdeny ${p.name}", "&7Deny the request from &b${p.name}")
                if (x != hereRequests.size - 1) {
                    imsg.add("\n")
                }
                x++
            }
        }
        imsg.send(player)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return ArrayList()
    }

}