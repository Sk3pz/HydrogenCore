package es.skepz.hydrogen

import es.skepz.hydrogen.commands.*
import es.skepz.hydrogen.commands.admin.*
import es.skepz.hydrogen.commands.admin.punish.*
import es.skepz.hydrogen.commands.confirmation.CancelCommand
import es.skepz.hydrogen.commands.confirmation.ConfirmCommand
import es.skepz.hydrogen.commands.economy.BalanceCommand
import es.skepz.hydrogen.commands.economy.PayCommand
import es.skepz.hydrogen.commands.`fun`.FlyCommand
import es.skepz.hydrogen.commands.`fun`.HatCommand
import es.skepz.hydrogen.commands.admin.RepairCommand
import es.skepz.hydrogen.commands.tpa.*
import es.skepz.hydrogen.commands.vanilla.EnchantCommand
import es.skepz.hydrogen.commands.vanilla.GamemodeCommand
import es.skepz.hydrogen.commands.vanilla.OpCommand
import es.skepz.hydrogen.events.*
import es.skepz.hydrogen.files.ServerFiles
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.utils.reloadLogin
import es.skepz.hydrogen.utils.reloadLogout
import es.skepz.hydrogen.verification.commands.UnverifyCommand
import es.skepz.hydrogen.verification.commands.VerifyCommand
import es.skepz.hydrogen.verification.events.VerificationEvents
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/*
    * TODO
    *  - lastLogoff is showing december 31st 1969
 */

class Hydrogen : JavaPlugin() {

    val files = ServerFiles(this)
    val userFiles = mutableMapOf<UUID, UserFile>()

    val tpaRequests = HashMap<UUID, UUID>()
    val tpahereRequests = HashMap<UUID, UUID>()

    val confirmMap = HashMap<UUID, (confirm: Boolean) -> Unit>()

    override fun onEnable() {

        // Register commands
        registerTpaCommands()
        registerAdminCommands()
        registerVanillaCommands()
        registerFunCommands()
        registerCommands()

        // Register events
        registerEvents()

        // load all worlds in files.worlds; does not worry about the main world as the server loads it automatically
        files.worlds.cfg.getConfigurationSection("worlds")?.getKeys(false)?.forEach { worldName ->
            val worldscfg = files.worlds.cfg
            if (!worldscfg.getBoolean("worlds.$worldName.auto-load")) return@forEach
            val world = server.getWorld(worldName)
            if (world == null) {
                val creator = WorldCreator(worldName)
                val environment = worldscfg.getString("worlds.$worldName.environment") ?: "NORMAL"
                creator.environment(World.Environment.valueOf(environment))
                server.createWorld(creator)
            }
        }

        // loop through online players and create their user files
        server.onlinePlayers.forEach { player ->
            reloadLogin(this, player)
            userFiles[player.uniqueId] = UserFile(this, player)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
        server.onlinePlayers.forEach { player ->
            reloadLogout(this, player)
        }
    }

    private fun registerEvents() {
        EventPlayerJoin(this).register()
        EventPlayerQuit(this).register()
        EventPlayerChat(this).register()
        EventPlayerDie(this).register()

        VerificationEvents(this).register()
    }

    private fun registerCommands() {
        CancelCommand(this).register()
        ConfirmCommand(this).register()

        RulesCommand(this).register()
        SpawnCommand(this).register()
        WarpCommand(this).register()

        BalanceCommand(this).register()
        PayCommand(this).register()

        HomeCommand(this).register()
        SetHomeCommand(this).register()
        DelHomeCommand(this).register()
    }

    private fun registerVanillaCommands() {
        EnchantCommand(this).register()
        OpCommand(this).register()
        GamemodeCommand(this).register()
    }

    private fun registerAdminCommands() {
        BanCommand(this).register()
        KickCommand(this).register()
        MuteCommand(this).register()
        TempBanCommand(this).register()
        TempMuteCommand(this).register()
        UnbanCommand(this).register()
        UnmuteCommand(this).register()

        ConfigsCommand(this).register()
        SetWarpCommand(this).register()
        DelWarpCommand(this).register()
        EcoCommand(this).register()
        MetaCommand(this).register()
        RankCommand(this).register()
        SetSpawnCommand(this).register()
        WorldCommand(this).register()
        PermissionCommand(this).register()
        LookupCommand(this).register()
        RepairCommand(this).register()

        VerifyCommand(this).register()
        UnverifyCommand(this).register()
    }

    private fun registerFunCommands() {
        FlyCommand(this).register()
        HatCommand(this).register()
    }

    private fun registerTpaCommands() {
        TpaCommand(this).register()
        TphereCommand(this).register()
        TpaCancel(this).register()
        TpacceptCommand(this).register()
        TpdenyCommand(this).register()
        TplistCommand(this).register()
    }
}