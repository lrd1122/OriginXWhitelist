package cc.originx.lrd1122;

import cc.originx.lrd1122.core.*;
import cc.originx.lrd1122.events.PlayerJoinListener;
import cc.originx.lrd1122.templete.ChatCaptureTemplate;
import cc.originx.lrd1122.templete.ChestMenuTemplate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class OriginXWhitelist extends JavaPlugin {
    public static OriginXWhitelist instance;
    private ConfigManager configManager;
    private LangManager langManager;
    private PlayerDataManager playerDataManager;
    private CommandManager commandManager;
    private LoginAttemptManager loginAttemptManager;;

    @Override
    public void onEnable() {
        instance = this;
        ChestMenuTemplate.init(this);
        ChatCaptureTemplate.init(this);

        // 初始化管理器
        configManager = new ConfigManager(this);
        langManager = new LangManager(this);
        playerDataManager = new PlayerDataManager(getDataFolder());
        commandManager = new CommandManager(this, configManager, langManager, playerDataManager);
        loginAttemptManager = new LoginAttemptManager(configManager);
        Bukkit.getPluginCommand("owhitelist").setExecutor(commandManager);
        Bukkit.getPluginCommand("owhitelist").setTabCompleter(commandManager);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataManager, loginAttemptManager, langManager), this);
        getLogger().info("OriginXWhitelist has been enabled!");
    }

    @Override
    public void onDisable() {
        playerDataManager.saveData();
        getLogger().info("OriginXWhitelist has been disabled!");
    }

}