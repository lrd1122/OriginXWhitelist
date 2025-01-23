package cc.originx.lrd1122.core;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    // Config values with defaults
    private String language;
    private int maxLoginAttempts;
    private int blockDuration;
    private String kickMessage;
    private boolean enableIPWhitelist;
    private boolean enableUUIDWhitelist;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Load all config values
        language = config.getString("language", "en_US");
        maxLoginAttempts = config.getInt("security.max-login-attempts", 5);
        blockDuration = config.getInt("security.block-duration", 3600);
        kickMessage = config.getString("messages.kick-message", "§cYou are not whitelisted!");
        enableIPWhitelist = config.getBoolean("whitelist.enable-ip", true);
        enableUUIDWhitelist = config.getBoolean("whitelist.enable-uuid", true);
    }

    public void saveConfig() {
        // Save all config values
        config.set("language", language);
        config.set("security.max-login-attempts", maxLoginAttempts);
        config.set("security.block-duration", blockDuration);
        config.set("messages.kick-message", kickMessage);
        config.set("whitelist.enable-ip", enableIPWhitelist);
        config.set("whitelist.enable-uuid", enableUUIDWhitelist);

        plugin.saveConfig();
    }

    public void setLanguage(String language) {
        this.language = language;
        saveConfig();
    }

    public void setMaxLoginAttempts(int maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
        saveConfig();
    }

    public void setBlockDuration(int blockDuration) {
        this.blockDuration = blockDuration;
        saveConfig();
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
        saveConfig();
    }

    public void setEnableIPWhitelist(boolean enableIPWhitelist) {
        this.enableIPWhitelist = enableIPWhitelist;
        saveConfig();
    }

    public void setEnableUUIDWhitelist(boolean enableUUIDWhitelist) {
        this.enableUUIDWhitelist = enableUUIDWhitelist;
        saveConfig();
    }
}