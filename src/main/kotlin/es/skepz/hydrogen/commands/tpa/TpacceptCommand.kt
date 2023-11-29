package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*
import kotlin.collections.HashMap

class TpacceptCommand(val core: Hydrogen) : CoreCMD(core, "tpaccept", "&c/tpaccept <player?>",
    0, "none", true, true) {

    fun teleport(target: Player, tpr: Player) {
        sendMessage(target, "&7Teleported &b${tpr.name} &7to you.")
        sendMessage(tpr, "&7Whoosh! You have been teleported to &b${target.name}&7!")
        tpr.teleport(target.location)
        playSound(tpr, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
    }

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

        // if a name is specified
        if (args.size > 0) {
            val requester = Bukkit.getPlayer(args[0])
            if (requester == null) {
                sendMessage(sender, "&cThat player either is not online or does not exist!")
                return
            }

            if (requests[requester.uniqueId] != null) {
                // teleport requester to target (sender)
                teleport(player, requester)
                core.tpaRequests.remove(requester.uniqueId)
                return
            }
            if (hereRequests[requester.uniqueId] != null) {
                // teleport target (sender) to requester
                teleport(requester, player)
                core.tpahereRequests.remove(requester.uniqueId)
                return
            }

            sendMessage(sender, "&cYou don't currently have any requests from this person. Maybe they canceled their request?")
            return
        }

        // no player specified

        if (requests.size == 1 && hereRequests.size == 0) {
            // only 1 request, handle
            val ruid = requests.keys.distinct()[0]
            // remove the request as it is being acted on
            core.tpaRequests.remove(ruid)
            val requester = Bukkit.getPlayer(ruid)
            if (requester == null) {
                // inform player
                sendMessage(sender, "&cThat player could not be found. They probably logged off.")
                return
            }
            teleport(player, requester)
            return
        }
        if (requests.size == 0 && hereRequests.size == 1) {
            // only 1 request, handle
            val ruid = hereRequests.keys.distinct()[0]
            // remove the request as it is being acted on
            core.tpahereRequests.remove(ruid)
            val requester = Bukkit.getPlayer(ruid)
            if (requester == null) {
                // inform player
                sendMessage(sender, "&cThat player could not be found. They probably logged off.")
                return
            }
            teleport(requester, player)
            return
        }

        // multiple requests, give them a list
        val imsg = IMessage("&cYou have multiple requests. Use /tpdeny <name> to specify which one.\n&7&lChoose which request to accept:\n")
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
        val completions = ArrayList<String>()

        val players = Bukkit.getServer().onlinePlayers
        val names = ArrayList<String>()
        for (p in players) {
            names.add(p.name)
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], names, completions)
        }
        return completions
    }

}