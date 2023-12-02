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

class RankCommand(val core: Hydrogen) : CoreCMD(core, "rank", "&c/rank <&7create&c|&7edit&c|&7set&c|&7delete&c|&7list&c> <&7args&c>",
    1, "hydrogen.command.rank", false, true) {

    override fun run() {
        when (args[0]) {
            "create" -> {
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. /rank create <name>")
                    return
                }

                val rank = args[1]
                if (core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank already exists.")
                    return
                }

                // create the rank
                core.files.ranks["ranks.$rank.prefix"] = ""
                core.files.ranks["ranks.$rank.nameColor"] = "&8"
                core.files.ranks["ranks.$rank.chatColor"] = "&f"
                core.files.ranks["ranks.$rank.permissions"] = listOf("")
                sendMessage(sender, "&7Rank created.")
            }
            "set" -> {
                if (args.size != 3) {
                    sendMessage(sender, "&cInvalid usage. /rank set <player> <rank>")
                    return
                }
                val player = core.server.getPlayer(args[1]) ?: core.server.getOfflinePlayer(args[1])
                // get the target's user file if it exists
                val rfile = getOfflineUserFileRaw(core, player.uniqueId) ?: return sendMessage(sender, "&cThat player does not exist or has not played before!")
                val file = UserFile(core, player, rfile)

                if (sender is Player && player == sender && !checkPermission(sender, "hydrogen.command.rank.self")) {
                    sendMessage(sender, "&cYou can't set your own rank.")
                    return
                }

                val rank = args[2]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found.")
                    return
                }

                file.setRank(rank)
                sendMessage(sender, "&7Rank set.")

                if (player is Player) {
                    refreshPermissions(core, player)
                    // send a message to the player
                    sendMessage(player, "&7Your rank has been set to &b$rank")
                }
            }
            "edit" -> {
                // edit <rank> <prefix|nameColor|permissions> <value>
                if (args.size < 4) {
                    sendMessage(sender, "&cInvalid usage.")
                    return
                }

                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found. do &f/rank create <name> &cto create a new rank.")
                    return
                }

                val value = args[2]
                when (value) {
                    "prefix" -> {
                        // edit <rank> prefix <value>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val prefix = args[3]
                        core.files.ranks["ranks.$rank.prefix"] = prefix
                        sendMessage(sender, "&7Prefix set.")
                    }
                    "nameColor" -> {
                        // edit <rank> nameColor <value>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val nameColor = args[3]
                        core.files.ranks["ranks.$rank.nameColor"] = nameColor
                        sendMessage(sender, "&7Name color set.")
                    }
                    "chatColor" -> {
                        // edit <rank> chatColor <value>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val chatColor = args[3]
                        core.files.ranks["ranks.$rank.chatColor"] = chatColor
                        sendMessage(sender, "&7Chat color set.")
                    }
                    "isOp" -> {
                        // edit <rank> isOp <true|false>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val isOp = args[3].toBoolean()
                        core.files.ranks["ranks.$rank.isOp"] = isOp
                        sendMessage(sender, "&7isOp set.")
                    }
                    "permissions" -> {
                        // edit <rank> permissions <add|remove> <permission>
                        if (args.size != 5) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val action = args[3]
                        val permission = args[4]
                        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
                        when (action) {
                            "add" -> {
                                permissions.add(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "&7Permission added.")
                            }
                            "remove" -> {
                                permissions.remove(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "&7Permission removed.")
                            }
                            else -> sendMessage(sender, "&cInvalid action.")
                        }
                    }
                    else -> sendMessage(sender, "&cInvalid value.")
                }
            }
            "delete" -> {
                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found. do /rank create <name> to create a new rank.")
                    return
                }
                core.files.ranks["ranks.$rank"] = null
                sendMessage(sender, "&7Rank deleted.")

            }
            "list" -> {
                val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                sendMessage(sender, "&7Ranks: &b${ranks.joinToString("&7, &b")}")
            }
            else -> sendMessage(sender, "&cInvalid subcommand.")
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