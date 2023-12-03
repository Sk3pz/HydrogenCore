package es.skepz.hydrogen.commands.admin

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.checkPermission
import es.skepz.hydrogen.skepzlib.sendMessage
import es.skepz.hydrogen.skepzlib.wrappers.CoreCMD
import es.skepz.hydrogen.utils.getOfflineUserFileRaw
import es.skepz.hydrogen.utils.refreshPermissions
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class RankCommand(val core: Hydrogen) : CoreCMD(core, "rank", "<red>/rank <<gray>create<red>|<gray>edit<red>|<gray>set<red>|<gray>delete<red>|<gray>list<red>> <<gray>args<red>>",
    1, "hydrogen.command.rank", false, true) {

    override fun run() {
        when (args[0]) {
            "create" -> {
                if (args.size != 2) {
                    sendMessage(sender, "<red>Invalid usage. /rank create <name>")
                    return
                }

                val rank = args[1]
                if (core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "<red>Rank already exists.")
                    return
                }

                // create the rank
                core.files.ranks["ranks.$rank.prefix"] = ""
                core.files.ranks["ranks.$rank.nameColor"] = "<dark_gray>"
                core.files.ranks["ranks.$rank.chatColor"] = "<gray>"
                core.files.ranks["ranks.$rank.permissions"] = listOf("")
                sendMessage(sender, "<gray>Rank created.")
            }
            "set" -> {
                if (args.size != 3) {
                    sendMessage(sender, "<red>Invalid usage. /rank set <player> <rank>")
                    return
                }
                val player = core.server.getPlayer(args[1]) ?: core.server.getOfflinePlayer(args[1])
                // get the target's user file if it exists
                val rfile = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "<red>That player does not exist or has not played before!")
                val file = UserFile(core, player, rfile)

                if (sender is Player && player == sender && !checkPermission(sender, "hydrogen.command.rank.self")) {
                    sendMessage(sender, "<red>You can't set your own rank.")
                    return
                }

                val rank = args[2]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "<red>Rank not found.")
                    return
                }

                file.setRank(rank)
                sendMessage(sender, "<gray>Rank set.")

                if (player is Player) {
                    refreshPermissions(core, player)
                    // send a message to the player
                    sendMessage(player, "<gray>Your rank has been set to <aqua>$rank")
                }
            }
            "edit" -> {
                // edit <rank> <prefix|nameColor|permissions> <value>
                if (args.size < 4) {
                    sendMessage(sender, "<red>Invalid usage.")
                    return
                }

                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "<red>Rank not found. do <gray>/rank create <name> <red>to create a new rank.")
                    return
                }

                val value = args[2]
                when (value) {
                    "prefix" -> {
                        // edit <rank> prefix <value>
                        if (args.size != 4) {
                            sendMessage(sender, "<red>Invalid usage.")
                            return
                        }
                        val prefix = args[3]
                        core.files.ranks["ranks.$rank.prefix"] = prefix
                        sendMessage(sender, "<gray>Prefix set.")
                    }
                    "nameColor" -> {
                        // edit <rank> nameColor <value>
                        if (args.size != 4) {
                            sendMessage(sender, "<red>Invalid usage.")
                            return
                        }
                        val nameColor = args[3]
                        core.files.ranks["ranks.$rank.nameColor"] = nameColor
                        sendMessage(sender, "<gray>Name color set.")
                    }
                    "chatColor" -> {
                        // edit <rank> chatColor <value>
                        if (args.size != 4) {
                            sendMessage(sender, "<red>Invalid usage.")
                            return
                        }
                        val chatColor = args[3]
                        core.files.ranks["ranks.$rank.chatColor"] = chatColor
                        sendMessage(sender, "<gray>Chat color set.")
                    }
                    "isOp" -> {
                        // edit <rank> isOp <true|false>
                        if (args.size != 4) {
                            sendMessage(sender, "<red>Invalid usage.")
                            return
                        }
                        val isOp = args[3].toBoolean()
                        core.files.ranks["ranks.$rank.isOp"] = isOp
                        sendMessage(sender, "<gray>isOp set.")
                    }
                    "permissions" -> {
                        // edit <rank> permissions <add|remove> <permission>
                        if (args.size != 5) {
                            sendMessage(sender, "<red>Invalid usage.")
                            return
                        }
                        val action = args[3]
                        val permission = args[4]
                        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
                        when (action) {
                            "add" -> {
                                permissions.add(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "<gray>Permission added.")
                            }
                            "remove" -> {
                                permissions.remove(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "<gray>Permission removed.")
                            }
                            else -> sendMessage(sender, "<red>Invalid action.")
                        }
                    }
                    else -> sendMessage(sender, "<red>Invalid value.")
                }
            }
            "delete" -> {
                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "<red>Rank not found. do /rank create <name> to create a new rank.")
                    return
                }
                core.files.ranks["ranks.$rank"] = null
                sendMessage(sender, "<gray>Rank deleted.")

            }
            "list" -> {
                val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                sendMessage(sender, "<gray>Ranks: <aqua>${ranks.joinToString("<gray>, <aqua>")}")
            }
            else -> sendMessage(sender, "<red>Invalid subcommand.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val names = when (args.size) {
            1 -> {
                listOf("create", "edit", "set", "delete", "list")
            }
            2 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "set" -> {
                        core.server.onlinePlayers.map { it.name }
                    }
                    "delete" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            3 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val rank = args[1]
                        if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                            emptyList()
                        } else {
                            listOf("prefix", "nameColor", "permissions")
                        }
                    }
                    "set" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "delete" -> emptyList()
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            4 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val rank = args[1]
                        if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                            emptyList()
                        } else {
                            val value = args[2]
                            when (value) {
                                "prefix" -> emptyList()
                                "nameColor" -> emptyList()
                                "permissions" -> listOf("add", "remove")
                                else -> emptyList()
                            }
                        }
                    }
                    "set" -> emptyList()
                    "delete" -> emptyList()
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }

        StringUtil.copyPartialMatches(args.last(), names, completions)

        return completions
    }
}