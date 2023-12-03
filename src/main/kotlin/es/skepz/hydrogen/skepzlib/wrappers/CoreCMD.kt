package es.skepz.hydrogen.skepzlib.wrappers

import es.skepz.hydrogen.skepzlib.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.ArrayList

abstract class CoreCMD protected constructor(
    private val plugin: JavaPlugin,
    private val cmd: String,
    private val usage: String,
    private val argc: Int,
    var permission: String,
    private var onlyPlayer: Boolean,
    private var tabCom: Boolean
) : CommandExecutor, TabCompleter {

    lateinit var sender: CommandSender
    lateinit var args: ArrayList<String>
    private var helpMessage: String = "<gray>Usage: <red>$usage"

    open fun init() {}
    abstract fun run()
    open fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> { return listOf() }

    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String> {
        return registerTabComplete(sender, args)
    }

    private val isPlayer: Boolean
        get() = sender is Player

    fun getPlayer(): Player? {
        return if (isPlayer) sender as Player else null
    }

    fun register() {

        val command = plugin.getCommand(cmd)
            ?: return

        command.setExecutor(this)

        if (tabCom) {
            command.tabCompleter = this
        }
    }

    fun invalidUse() {
        invalidCmdUsage(sender, usage)
    }

    // Will check permissions and for player
    // then execute run()
    override fun onCommand(s: CommandSender, cmd: Command, alias: String, args: Array<String>): Boolean {

        this.args = ArrayList(listOf(*args)) // put args into arraylist
        sender = s // set the sender

        if (args.size == 1 && args[0] == "?") { // if user is running the help function
            sendMessage(sender, helpMessage)
            return true
        }

        if (onlyPlayer && sender !is Player) { // if command requires sender to be a player, run the check
            requirePlayer(sender)
            return true
        }

        if (permission != "none" && !checkPermission(sender, permission)) { // check the sender for the required permission
            noPerms(sender)
            return true
        }

        if (args.size < argc) { // check if there is a correct amount of arguments
            invalidCmdUsage(sender, usage)
            return true
        }


        run() // run the main code

        return true
    }

}