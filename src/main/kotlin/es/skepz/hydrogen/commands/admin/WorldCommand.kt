package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getSpawn
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class WorldCommand(val core: Hydrogen): CoreCMD(core, "world", "<red>/world <<gray>create<red>|<gray>delete<red>|<gray>tp<red>|<gray>list<red>>", 1,
    "hydrogen.command.world", true, true) {

    override fun run() {
        val player = getPlayer()!!

        val mode = args[0]

        when (mode) {
            "create" -> {
                if (args.size < 2) {
                    sendMessage(player, "<red>Usage: <red>/world create <<gray>name<red>> <<gray>optional environment<red>> <<gray>optional seed<red>>")
                    return
                }
                val name = args[1]
                val environment = if (args.size == 3) {
                    args[2]
                } else {
                    "NORMAL"
                }

                val seed = if (args.size == 4) {
                    args[3].toLong()
                } else {
                    null
                }

                val world = core.server.getWorld(name)
                if (world != null) {
                    player.sendMessage("<red>The world <gray>$name <red>already exists.")
                    return
                }

                val creator = WorldCreator(name)
                creator.environment(World.Environment.valueOf(environment))

                if (seed != null) {
                    creator.seed(seed)
                }

                core.server.createWorld(creator) ?: return sendMessage(player, "<red>Error creating world $name.")

                // put the world into the config
                core.files.worlds["worlds.$name.environment"] = environment
                core.files.worlds["worlds.$name.auto-load"] = true

                sendMessage(player, "<gray>Created world <aqua>$name<gray>.")
            }
            "delete" -> {
                if (args.size < 2) {
                    sendMessage(player, "<red>Usage: <gray>/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "<red>The world <gray>$name <red>doesn't exist.")
                    return
                }

                // teleport players in that world to spawn
                world.players.forEach { it.teleport(getSpawn(core)) }

                core.server.unloadWorld(world, true)
                // delete the world's folder
                val worldFolder = core.server.worldContainer.resolve(name)
                worldFolder.deleteRecursively()
                core.files.worlds["worlds.$name"] = null
                sendMessage(player, "<gray>Deleted world <aqua>$name<aqua>.")
            }
            "tp" -> {
                if (args.size < 2) {
                    sendMessage(player, "<red>Usage: <gray>/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "<red>The world <gray>$name <red>doesn't exist.")
                    return
                }

                player.teleport(world.spawnLocation)
                sendMessage(player, "<gray>Teleported to world <aqua>$name<gray>.")
            }
            "list" -> {
                val worlds = core.server.worlds
                if (worlds.isEmpty()) {
                    sendMessage(player, "<red>There are no worlds to list.")
                    return
                }
                val worldNames = worlds.joinToString("<gray>, <aqua>") { it.name }
                sendMessage(player, "<gray>Worlds: <aqua>$worldNames<gray>.")
            }
        }

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val options = when (args.size) {
            1 -> {
                listOf("create", "delete", "tp", "list")
            }
            2 -> {
                when (args[0]) {
                    "create", "delete", "tp" -> {
                        core.server.worlds.map { it.name }
                    }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }

        StringUtil.copyPartialMatches(args.last(), options, completions)

        return completions
    }

}