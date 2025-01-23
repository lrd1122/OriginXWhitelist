# 🛡️ OriginX Whitelist

A high-performance, feature-rich Minecraft whitelist plugin with support for player name, IP, and UUID-based whitelisting.

## ✨ Key Features

- 🔒 Three whitelist modes: Player Name, IP Address, UUID
- 🌍 Full multilingual support (English and Chinese)
- 🛡️ Built-in anti-DDoS protection
- 📊 GUI management interface
- ⚡ Asynchronous processing for optimal performance

## 📝 Commands

- `/owhitelist` - Open GUI management interface
- `/owhitelist add <type> <value>` - Add to whitelist
- `/owhitelist remove <type> <value>` - Remove from whitelist
- `/owhitelist list [type]` - View whitelist entries

## 🔧 Permissions

- `originxwhitelist.admin` - Admin access to all commands

## 🎯 GUI Features

### Main Menu
- Language Settings
- Anti-DDoS Configuration
- IP Whitelist Toggle
- UUID Whitelist Toggle
- Whitelist Management

### Whitelist Management
- Add entries (Name/IP/UUID)
- View existing entries
- One-click removal

### Security Settings
- Configure max login attempts
- Set ban duration
- Clear temporary bans

## 📋 Configuration

```yaml
language: "en_US"  # Supports en_US, zh_CN
security:
  max-login-attempts: 5  # Maximum login attempts
  block-duration: 3600   # Ban duration (seconds)
whitelist:
  enable-ip: true   # Enable IP whitelist
  enable-uuid: true # Enable UUID whitelist
```

## 🌐 Multilingual Support

- Complete EN/CN support
- Auto-detect system language
- In-game language switching
- Custom language file support

## 🚀 Anti-DDoS Features

- Login attempt limiting
- Automatic temporary bans
- Configurable ban duration
- GUI-based unban system

## 📦 Installation

1. Download plugin JAR
2. Place in plugins folder
3. Restart server
4. Configure using `/owhitelist`

## 🔍 Important Notes

- Ensure admins have `originxwhitelist.admin` permission
- Review config file for initial setup
- Supports hot-reload of language files

## 💡 Support

For issues or suggestions:
- Submit GitHub issues
- Join our Discord
- Comment on this post

---