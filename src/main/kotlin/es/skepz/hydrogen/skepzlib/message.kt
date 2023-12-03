package es.skepz.hydrogen.skepzlib

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/***
 * Sends a message to a player
 * @param user: The user to send the message to
 * @param message: The message to send (will be formatted with '&' color codes)
 ***/
fun sendMessage(user: CommandSender, message: String) {
    user.sendMessage(colorize(message))
}

/***
 * Creates an action bar for a player
 * @param player: the player to see the message
 * @param message: the message to display
 ***/
fun actionbar(player: Player, message: String) {
    player.sendActionBar(colorize(message))
}

/***
 * Generate a usage string for a command
 * @param usage: the string typed after the / for the command (without the /)
 * @param argSections: a vararg of String Lists, with each list's index being its argument location
 * @return the finished usage string
 ***/
fun genUsage(usage: String, vararg argSections: List<String>): String {
    var fin = "<red>/$usage <"
    for (sec in argSections)
        for (x in 0..sec.size)
            fin += if (x != sec.size - 1) "<gray>${sec[x]}<red>|" else "<gray>${sec[x]}"
    return "$fin<red>>"
}

/***
 * send an invalid command usage message to a user
 * @param user: the user to send the message to
 * @param usage: the correct usage of the command
 ***/
fun invalidCmdUsage(user: CommandSender, usage: String) {
    sendMessage(user, "<red>Invalid Use! Usage: <gray>$usage")
}

/***
 * sends an invalid permissions message to a player
 * @param user: the user to send the message to
 ***/
fun noPerms(user: CommandSender) {
    sendMessage(user, "<red>You don't have permission to use this command!")
}

/***
 * send a message that the entered player is not a correct player
 * @param user: the user to send the message to
 ***/
fun notPlayer(user: CommandSender) {
    sendMessage(user, "<red>This player is either not online or does not exist!")
}

/***
 * sends a message to the sender that you have to be a player to run the command
 * @param user: the user to send the message to
 ***/
fun requirePlayer(user: CommandSender) {
    sendMessage(user, "<red>You must be a player to use this command!")
}

/***
 * sends a message to the console
 * @param msg: the message to send
 ***/
fun sendConsoleMessage(msg: String) {
    sendMessage(Bukkit.getConsoleSender(), msg)
}

/***
 * broadcasts a message to everyone on the server, and console
 * @param msg: the message to send
 ***/
fun serverBroadcast(msg: String) {
    Bukkit.getServer().broadcast(colorize(msg))
}

/**
 * An interactable message
 **/
class IMessage {
    private var smsg: String
    private var textComponent: Component
    /***
     * the default constructor, initialises the start message as an empty string
     ***/
    constructor() {
        smsg = ""
        textComponent = Component.text(smsg)
    }
    /***
     * The secondary constructor
     * @param start: the start / default message that will be added on to
     ***/
    constructor(start: String) {
        smsg = start
        textComponent = colorize(smsg)
    }

    /***
     * sets the tooltip for the start message, making it hoverable
     * @param toolTip: the tooltip that will display on hovering over the message
     * @return returns this object for chaining message types
     ***/
    fun setDefaultHover(toolTip: String): IMessage {
        //textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(colorize(toolTip)).create())
        textComponent = textComponent.hoverEvent(HoverEvent.showText(colorize(toolTip)))
        return this
    }

    /***
     * sets the link for the start message, making it clickable
     * @param link: the link that clicking the message will send the user(s) to
     * @return returns this object for chaining message types
     ***/
    fun setDefaultClick(link: String): IMessage {
        textComponent = textComponent.clickEvent(ClickEvent.openUrl(link))
        return this
    }

    /***
     * sets the command for the start message, making it clickable
     * @param cmd: the command to run when clicked
     * @return returns this object for chaining message types
     ***/
    fun setDefaultCmd(cmd: String): IMessage {
        textComponent = textComponent.clickEvent(ClickEvent.runCommand(cmd))
        return this
    }

    /***
     * appends a normal string to the message
     * @param msg: the message to be added
     * @return returns this object for chaining message types
     ***/
    fun add(msg: String): IMessage {
        textComponent = textComponent.append(colorize(msg))
        return this
    }

    /***
     * adds a string with a tooltip to the message
     * @param msg: the message to be added
     * @param toolTip: the tooltip to display on hovering over the message
     * @return returns this object for chaining message types
     ***/
    fun addHoverable(msg: String, toolTip: String): IMessage {
        var tc = colorize(msg)
        tc = tc.hoverEvent(HoverEvent.showText(colorize(toolTip)))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * adds a message which will run a command upon being clicked
     * @param msg: the message that can be clicked
     * @param command: the command to be run on click
     * @return returns this object for chaining message types
     ***/
    fun addClickCmd(msg: String, command: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.runCommand(command))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * adds a message which will suggest a command upon being clicked
     * @param msg: the message that can be clicked
     * @param command: the command to be suggested on click
     * @return returns this object for chaining message types
     */
    fun addSuggestCmd(msg: String, command: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.suggestCommand(command))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * adds a message that will copy a string to the clipboard upon being clicked
     * @param msg: the message to be displayed
     * @param copy: the string to be copied to the clipboard
     * @return returns this object for chaining message types
     ***/
    fun addCopyClick(msg: String, copy: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.copyToClipboard(copy))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * a message that will run a command upon being clicked and will display a tooltip upon hovering over it
     * @param msg: the message to be displayed
     * @param command: the command to be run on click
     * @param toolTip:
     * @return returns this object for chaining message types
     ***/
    fun addHoverableClickCmd(msg: String, command: String, toolTip: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.runCommand(command))
        tc = tc.hoverEvent(HoverEvent.showText(colorize(toolTip)))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * add a message that will open a link when clicked
     * @param msg: the message to be added
     * @param link: the link to be opened on click
     * @return returns this object for chaining message types
     ***/
    fun addLink(msg: String, link: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.openUrl(link))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * add a clickable link message with a tooltip
     * @param msg: the message to display
     * @param link: the link to be opened on click
     * @param toolTip: the tooltip to be displayed
     * @return returns this object for chaining message types
     ***/
    fun addHoverableLink(msg: String, link: String, toolTip: String): IMessage {
        var tc = colorize(msg)
        tc = tc.clickEvent(ClickEvent.openUrl(link))
        tc = tc.hoverEvent(HoverEvent.showText(colorize(toolTip)))
        textComponent = textComponent.append(tc)
        return this
    }

    /***
     * adds a newline (also possible with \n anywhere)
     * @return returns this object for chaining message types
     ***/
    fun newLine(): IMessage {
        add("\n")
        return this
    }

    /***
     * send the full message to a player
     * @param player: the player to send the message to
     * @return returns this object for chaining message types
     ***/
    fun send(player: Player): IMessage {
        player.sendMessage(textComponent)
        return this
    }

    /***
     * send the full message to all players on the server
     * @return returns this object for chaining message types
     ***/
    fun broadcast(): IMessage {
        Bukkit.getServer().broadcast(textComponent)
        return this
    }
}

/***
 * Make an informational message with a tooltip
 * @param user: The player to send it to
 * @param title: the title
 * @param subtitle: a short description
 * @param toolTip: what to be displayed on hover
 ***/
fun info(user: Player, title: String, subtitle: String, toolTip: String) {
    IMessage("<dark_aqua>$title").newLine()
            .add("<aqua>$subtitle")
            .newLine().newLine()
            .addHoverable(" <gray>&o[?] Hover over this for more information", toolTip)
            .send(user)
}

/***
 * Make an error message with a tooltip
 * @param user: The player to send it to
 * @param title: the title
 * @param subtitle: a short description
 * @param toolTip: what to be displayed on hover
 ***/
fun invalid(user: Player, title: String, subtitle: String, toolTip: String) {
    IMessage("<red>$title").newLine()
            .add("<gray>$subtitle")
            .newLine().newLine()
            .addHoverable(" <gray>&o[?] Hover over this for more information", toolTip)
            .send(user)
}