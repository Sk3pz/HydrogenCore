# Hydrogen
A minecraft core for all your survival server needs

![Welcome Message Example](./screenshots/welcome.png)

## Features:
### Toggleable verification system
- Don't allow players to interact with your server unless a verified player verifies them
- Can be disabled or enabled at any time in the config files
### Chat Handling:
- Formatting and coloring (with permissions)
- Configurable filter
### Permissions System:
- Fully customizable ranks and permissions
- Completely configurable
### Commands:
**more information can be found about these commands by running them ingame with no arguments*  
**Admin:**
- `/broadcast` - Broadcast a message to all players
- `/cfgreload` - Reload the config files
- `/eco` - Manage the economy
- `/lookup` - Lookup a player's information on the server
- `/meta` - Change and item's metadata (i.e. name, lore, etc)
- `/permission` - Manage an individual player's permissions
- `/rank` - Manage ranks
- `/repair` - Repair an item or items
- `/setspawn` - Set the spawn point for the server
- `/setwarp` - set a warp for players to access with an optional permission
- `/delwarp` - Delete a warp
- `/sudo` - Run a command as console
- `/world` - Create, manage, teleport to, and delete worlds
- `/ban` - Ban a player
- `/tempban` - Ban a player
- `/unban` - Unban a player
- `/kick` - Kick a player
- `/mute` - Mute a player
- `/tempmute` - Mute a player
- `/unmute` - Unmute a player  
**Economy:**
- `/balance` - Check your balance
- `/pay` - Pay another player
- `/deposit` - Deposit diamonds into your account (requires is_money_diamonds to be true in the config.yml)
- `/withdraw` - Withdraw diamonds from your account (requires is_money_diamonds to be true in the config.yml)  
**Fun:**
- `/feed` - Feed yourself or another player
- `/heal` - Heal yourself or another player
- `/fly` - Toggle fly mode for yourself or another player
- `/god` - Toggle god mode for yourself or another player
- `/hat` - Wear an item as a hat
- `/smite` - Smite a player  
**TPA:** *Teleportation with other players with consent*
- `/tpa` - Request to teleport to another player
- `/tpaccept` - Accept a teleport request (you can also click the accept button in chat)
- `/tpdeny` - Deny a teleport request (you can also click the deny button in chat)
- `/tpahere` - Request another player to teleport to you
- `/tplist` - List all teleport requests directed towards you  
**Overwritten Vanilla Commands:**
- `/op` - Make a player an operator
- `/deop` - Remove a player's operator status
- `/enchant` - Enchant an item, with no limits
- `/gamemode` - Change a player's gamemode  
**Homes:**
- `/sethome` - Set a home
- `/home` - Teleport to a home (will list homes if no name is given)
- `/delhome` - Delete a home  
**Misc:**
- `/back` - Teleport back to your last location
- `/spawn` - Teleport to the spawn point
- `/warp` - Teleport to a warp
- `/rules` - Show the server rules