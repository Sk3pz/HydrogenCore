package es.skepz.hydrogen.utils

import es.skepz.hydrogen.Hydrogen
import es.skepz.hydrogen.files.UserFile
import es.skepz.hydrogen.skepzlib.IMessage
import es.skepz.hydrogen.skepzlib.colorize
import es.skepz.hydrogen.skepzlib.playSound
import es.skepz.hydrogen.skepzlib.wrappers.CFGFile
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun sendConfirmMsg(core: Hydrogen, player: Player, msg: String, callback: (confirm: Boolean) -> Unit) {
    playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 0.2f)
    IMessage("\n<gold><bold>Confirmation Required<reset>\n")
        .add("<gray>$msg\n")
        .add("<gray>&oType or click on one of the following options:\n")
        .addHoverableClickCmd("  <green>&oConfirm<reset>\n", "/core_confirm ${player.name}", "<gray>Confirm the action")
        .addHoverableClickCmd("  <red>&oCancel<reset>", "/core_cancel ${player.name}", "<gray>Cancel the action")
        .add("\n")
        .send(player)
//    sendMessage(player,"<gray><bold>$msg")
//    sendMessage(player, "<gray>Type <green>Confirm <gray>or <red>cancel<gray>.")
    core.confirmMap[player.uniqueId] = callback
}

fun getOfflineUserFileRaw(core: Hydrogen, uuid: UUID): UserFile? {
    // check if the user file exists in core.dataFolder.toString() + "/users/"
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        return null
    }

    val raw = CFGFile(core, uuid.toString(), "users")
    return UserFile(core, uuid, raw)
}

fun forceGetUserFile(core: Hydrogen, uuid: UUID): CFGFile {
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        file.createNewFile()
    }

    return CFGFile(core, uuid.toString(), "users")
}

fun getBlocksCanBeAdded(core: Hydrogen, player: Player): Int {
    var total = 0
    for (slot in player.inventory.storageContents) {
        if (slot == null || slot.type == Material.AIR) {
            total += 64
            continue
        }
        if (slot.type == Material.DIAMOND_BLOCK && slot.itemMeta.persistentDataContainer.has(core.diamondKey, PersistentDataType.INTEGER)) {
            total += 64 - slot.amount
        }
    }
    return total
}

fun getDiamondsCanBeAdded(core: Hydrogen, player: Player): Int {
    var total = 0
    for (slot in player.inventory.storageContents) {
        if (slot == null || slot.type == Material.AIR) {
            total += 64
            continue
        }
        if (slot.type == Material.DIAMOND && slot.itemMeta.persistentDataContainer.has(core.diamondKey, PersistentDataType.INTEGER)) {
            total += 64 - slot.amount
        }
    }
    return total
}

fun addDiamonds(core: Hydrogen, player: Player, blocks: Int, diamonds: Int): Pair<Int, Int> {
    //val moneySymbol = core.files.getMoneySymbol()

    val maxBlocks = getBlocksCanBeAdded(core, player)

    var blocksToAdd = blocks
    var unaddedBlocks = 0

    if (blocks > maxBlocks) {
        blocksToAdd = maxBlocks
        unaddedBlocks = blocks - maxBlocks
    }

    val blockItem = ItemStack(Material.DIAMOND_BLOCK, blocksToAdd)
//    val bmeta = blockItem.itemMeta
//    bmeta.displayName(Component.text(colorize("<aqua><bold>Blocks")))
//    bmeta.lore(listOf(
//        Component.text(colorize("<gray>Backed by the <aqua>Hydrogen <gray>economy!")),
//        Component.text(colorize("<gray>Value: <dark_aqua>$moneySymbol<aqua>9 <gray>/ block")),
//        Component.text(colorize("<yellow>Certified Shiny!"))))
//    bmeta.persistentDataContainer.set(core.diamondKey, PersistentDataType.INTEGER, 1)
//    blockItem.itemMeta = bmeta

    player.inventory.addItem(blockItem)

    val maxDiamonds = getDiamondsCanBeAdded(core, player)
    var diamondsToAdd = diamonds
    var unaddedDiamonds = 0

    if (diamonds > maxDiamonds) {
        diamondsToAdd = maxDiamonds
        unaddedDiamonds = diamonds - maxDiamonds
    }

    val diamondItem = ItemStack(Material.DIAMOND, diamondsToAdd)
//    val dmeta = diamondItem.itemMeta
//    dmeta.displayName(Component.text(colorize("<aqua><bold>Diamonds")))
//    dmeta.lore(listOf(
//        Component.text(colorize("<gray>Backed by the <aqua>Hydrogen <gray>economy!")),
//        Component.text(colorize("<gray>Value: <dark_aqua>$moneySymbol<aqua>1 <gray>/ item")),
//        Component.text(colorize("<yellow>Certified Shiny!"))))
//    dmeta.persistentDataContainer.set(core.diamondKey, PersistentDataType.INTEGER, 1)
//    diamondItem.itemMeta = dmeta

    player.inventory.addItem(diamondItem)

    return Pair(unaddedBlocks, unaddedDiamonds)
}