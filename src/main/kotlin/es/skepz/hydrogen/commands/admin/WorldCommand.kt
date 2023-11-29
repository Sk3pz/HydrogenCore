package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getSpawn
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class WorldCommand(val core: Hydrogen): CoreCMD(core, "world", "/world <create|delete|tp|list>", 1,
    "hydrogen.command.world", true, true) {

    override fun run() {
        val player = getPlayer()!!

        val mode = args[0]

        when (mode) {
            "create" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &c/world create <&7name&c> <&7optional environment&c> <&7optional seed&c>")
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
                    player.sendMessage("&cThe world &f$name &calready exists.")
                    return
                }

                val creator = WorldCreator(name)
                creator.environment(World.Environment.valueOf(environment))

                if (seed != null) {
                    creator.seed(seed)
                }

                core.server.createWorld(creator) ?: return sendMessage(player, "&cError creating world $name.")

                // put the world into the config
                core.files.worlds["worlds.$name.environment"] = environment
                core.files.worlds["worlds.$name.auto-load"] = true

                sendMessage(player, "&7Created world &b$name&7.")
            }
            "delete" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &f/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "&cThe world &f$name &cdoesn't exist.")
                    return
                }

                // teleport players in that world to spawn
                world.players.forEach { it.teleport(getSpawn(core)) }

                core.server.unloadWorld(world, true)
                // delete the world's folder
                val worldFolder = core.server.worldContainer.resolve(name)
                worldFolder.deleteRecursively()
                core.files.worlds["worlds.$name"] = null
                sendMessage(player, "&7Deleted world &b$name&b.")
            }
            "tp" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &f/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "&cThe world &f$name &cdoesn't exist.")
                    return
                }

                player.teleport(world.spawnLocation)
                sendMessage(player, "&7Teleported to world &b$name&7.")
            }
            "list" -> {
                val worlds = core.server.worlds
                val worldNames = worlds.joinToString("&7, &b") { it.name }
                sendMessage(player, "&7Worlds: &b$worldNames&7.")
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