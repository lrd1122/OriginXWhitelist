[CENTER][SIZE=6][COLOR=#4a90e2]️ OriginX Whitelist[/COLOR][/SIZE]

[SIZE=4]A high-performance, feature-rich Minecraft whitelist plugin with advanced features[/SIZE][/CENTER]

[SIZE=5][COLOR=#2ecc71]✨ Key Features[/COLOR][/SIZE]
[LIST]
[*]Three whitelist modes: Player Name, IP Address, UUID
[*]Full multilingual support (English and Chinese)
[*]Built-in anti-DDoS protection
[*]GUI management interface
[*]Asynchronous processing for optimal performance
[/LIST]

[SIZE=5][COLOR=#e74c3c]✨ Commands[/COLOR][/SIZE]
[CODE]
⌨️ /owhitelist - Open GUI management interface
➕ /owhitelist add <type> <value> - Add to whitelist
➖ /owhitelist remove <type> <value> - Remove from whitelist
 /owhitelist list [type] - View whitelist entries
[/CODE]

[SIZE=5][COLOR=#9b59b6]✨ Permissions[/COLOR][/SIZE]
[CODE] originxwhitelist.admin - Admin access to all commands[/CODE]

[SIZE=5][COLOR=#f1c40f]✨ GUI Features[/COLOR][/SIZE]
[SPOILER="Click to view GUI features"]
[B] Main Menu[/B]
[LIST]
[*]Language Settings
[*]️ Anti-DDoS Configuration
[*]IP Whitelist Toggle
[*]UUID Whitelist Toggle
[*]⚙️ Whitelist Management
[/LIST]

[B] Whitelist Management[/B]
[LIST]
[*]➕ Add entries (Name/IP/UUID)
[*]View existing entries
[*]❌ One-click removal
[/LIST]

[B] Security Settings[/B]
[LIST]
[*]Configure max login attempts
[*]⏱️ Set ban duration
[*]️ Clear temporary bans
[/LIST]
[/SPOILER]

[SIZE=5][COLOR=#1abc9c]✨ Configuration[/COLOR][/SIZE]
[CODE=YAML]
#  Language Configuration
language: "en_US"  # Supports en_US, zh_CN

#  Security Settings
security:
  max-login-attempts: 5  # Maximum login attempts
  block-duration: 3600   # Ban duration (seconds)

# ️ Whitelist Options
whitelist:
  enable-ip: true   # Enable IP whitelist
  enable-uuid: true # Enable UUID whitelist
[/CODE]

[SIZE=5][COLOR=#3498db]✨ Multilingual Support[/COLOR][/SIZE]
[LIST]
[*]Complete EN/CN support
[*]Auto-detect system language
[*]In-game language switching
[*]Custom language file support
[/LIST]

[SIZE=5][COLOR=#e74c3c]✨ Anti-DDoS Features[/COLOR][/SIZE]
[LIST]
[*]Login attempt limiting
[*]⚡ Automatic temporary bans
[*]⚙️ Configurable ban duration
[*]GUI-based unban system
[/LIST]

[SIZE=5][COLOR=#27ae60]✨ Installation[/COLOR][/SIZE]
[LIST=1]
[*]Download plugin JAR
[*]Place in plugins folder
[*]Restart server
[*]⚙️ Configure using /owhitelist
[/LIST]

[SIZE=5][COLOR=#e67e22]✨ Important Notes[/COLOR][/SIZE]
[LIST]
[*]Ensure admins have originxwhitelist.admin permission
[*]Review config file for initial setup
[*]Supports hot-reload of language files
[/LIST]

[SIZE=5][COLOR=#9b59b6]✨ Support[/COLOR][/SIZE]
[LIST]
[*]Submit GitHub issues
[*]Join our Discord
[*]Comment on this post
[/LIST]
[CENTER][SIZE=4][COLOR=#7f8c8d]Made with ❤️ for the Minecraft Community[/COLOR][/SIZE][/CENTER]