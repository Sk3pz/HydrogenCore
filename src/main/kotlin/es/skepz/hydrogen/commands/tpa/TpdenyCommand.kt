package es.skepz.hydrogen.commands.tpa

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.*
import kotlin.collections.HashMap

class TpdenyCommand(val core: Hydrogen) : CoreCMD(core, "tpdeny", "<red>/tpdeny <<gray>player<red>?>",
    0, "none", true, true) {

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
                sendMessage(sender, "<red>That player either is not online or does not exist!")
                return
            }

            if (requests[requester.uniqueId] != null) {
                // teleport requester to target (sender)
                sendMessage(sender, "<gray>You have denied <aqua>${requester.name}<gray>'s request to teleport to you.")
                sendMessage(requester, "<aqua>${player.name} <red>has denied your request to teleport to them.")
                core.tpaRequests.remove(requester.uniqueId)
                return
            }
            if (hereRequests[requester.uniqueId] != null) {
                // teleport target (sender) to requester
                sendMessage(sender, "<gray>You have denied <aqua>${requester.name}<gray>'s request to teleport you to them.")
                sendMessage(requester, "<aqua>${player.name} <red>has denied your request to teleport them to you.")
                core.tpahereRequests.remove(requester.uniqueId)
                return
            }

            sendMessage(sender, "<red>You don't currently have any requests from this person. Maybe they canceled their request?")
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
                sendMessage(sender, "<red>That player could not be found. They probably logged off.")
                return
            }
            sendMessage(sender, "<gray>You have denied <aqua>${requester.name}<gray>'s request to teleport to you.")
            sendMessage(requester, "<aqua>${player.name} <red>has denied your request to teleport to them.")
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
                sendMessage(sender, "<red>That player could not be found. They probably logged off.")
                return
            }
            sendMessage(sender, "<gray>You have denied <aqua>${requester.name}<gray>'s request to teleport you to them.")
            sendMessage(requester, "<aqua>${player.name} <red>has denied your request to teleport them to you.")
            return
        }

        // multiple requests, give them a list
        val imsg = IMessage("<red>You have multiple requests. Use /tpdeny <name> to specify which one.\n<gray><bold>Choose which request to deny:\n")
        if (requests.isNotEmpty()) {
            imsg.add(" <gray>> Requesting to teleport to you:\n")
            var x = 0
            for ((r, _) in requests) {
                val p = Bukkit.getPlayer(r)
                if (p == null) {
                    core.tpaRequests.remove(r)
                    continue
                }
                imsg.add(" | <aqua>${p.name} ")
                    .addHoverableClickCmd("<green>Accept ", "/tpaccept ${p.name}", "<gray>Accept the request from <aqua>${p.name}")
                    .addHoverableClickCmd("<red>Deny", "/tpdeny ${p.name}", "<gray>Deny the request from <aqua>${p.name}")
                if (x != requests.size - 1) {
                    imsg.add("\n")
                }
                x++
            }
        }
        if (hereRequests.isNotEmpty()) {
            imsg.add(" <gray>> Requesting to teleport you to them:\n")
            var x = 0
            for ((r, _) in hereRequests) {
                val p = Bukkit.getPlayer(r)
                if (p == null) {
                    core.tpahereRequests.remove(r)
                    continue
                }
                imsg.add(" | <aqua>${p.name} ")
                    .addHoverableClickCmd("<green>Accept ", "/tpaccept ${p.name}", "<gray>Accept the request from <aqua>${p.name}")
                    .addHoverableClickCmd("<red>Deny", "/tpdeny ${p.name}", "<gray>Deny the request from <aqua>${p.name}")
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