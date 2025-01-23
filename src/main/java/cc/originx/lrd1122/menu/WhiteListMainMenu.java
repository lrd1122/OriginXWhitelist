package cc.originx.lrd1122.menu;

import cc.originx.lrd1122.core.ConfigManager;
import cc.originx.lrd1122.core.LangManager;
import cc.originx.lrd1122.core.PlayerDataManager;
import cc.originx.lrd1122.templete.ChatCaptureTemplate;
import cc.originx.lrd1122.templete.ChestMenuTemplate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WhiteListMainMenu {
    private final ConfigManager configManager;
    private final LangManager langManager;
    private final PlayerDataManager playerDataManager;

    private List<String> getAvailableLanguages() {
        File langFolder = new File(langManager.getPlugin().getDataFolder(), "lang");
        return Arrays.stream(langFolder.listFiles((dir, name) -> name.endsWith(".yml")))
                .map(file -> file.getName().replace(".yml", ""))
                .collect(Collectors.toList());
    }

    public void open(Player player) {
        ChestMenuTemplate.createMenu(player, langManager.getMessage("menu.main.title"),
                        "#########",
                        "#abcdefg#",
                        "#hijklmn#",
                        "#########")
                .setItem('a', ChestMenuTemplate.createItem(Material.NAME_TAG,
                                langManager.getMessage("menu.main.language.title"),
                                langManager.getMessage("menu.main.language.current", configManager.getLanguage()),
                                langManager.getMessage("menu.main.language.click-change"),
                                langManager.getMessage("menu.main.language.available", String.join(", ", getAvailableLanguages()))),
                        p -> {
                            p.closeInventory();
                            List<String> languages = getAvailableLanguages();
                            p.sendMessage(langManager.getMessage("menu.main.language.enter-choice"));
                            languages.forEach(lang ->
                                    p.sendMessage(langManager.getMessage("menu.main.language.option", lang)));

                            ChatCaptureTemplate.nextChat(p, input -> {
                                String newLang = input.trim();
                                if (languages.contains(newLang)) {
                                    configManager.setLanguage(newLang);
                                    langManager.setLanguage(newLang);
                                    p.sendMessage(langManager.getMessage("menu.main.language.changed", newLang));
                                } else {
                                    p.sendMessage(langManager.getMessage("menu.main.language.invalid"));
                                }
                                open(p);
                            });
                        })

                .setItem('b', ChestMenuTemplate.createItem(Material.BARRIER,
                                langManager.getMessage("menu.main.security.title"),
                                langManager.getMessage("menu.main.security.attempts", configManager.getMaxLoginAttempts()),
                                langManager.getMessage("menu.main.security.duration", configManager.getBlockDuration())),
                        p -> new WhiteListSecurityMenu(configManager, langManager, playerDataManager).open(p))

                .setItem('c', ChestMenuTemplate.createItem(
                                configManager.isEnableIPWhitelist() ? Material.LIME_DYE : Material.GRAY_DYE,
                                langManager.getMessage("menu.main.ip.title"),
                                langManager.getMessage("menu.main.status." + (configManager.isEnableIPWhitelist() ? "enabled" : "disabled")),
                                langManager.getMessage("menu.main.click-toggle")),
                        p -> {
                            configManager.setEnableIPWhitelist(!configManager.isEnableIPWhitelist());
                            open(p);
                        })

                .setItem('d', ChestMenuTemplate.createItem(
                                configManager.isEnableUUIDWhitelist() ? Material.LIME_DYE : Material.GRAY_DYE,
                                langManager.getMessage("menu.main.uuid.title"),
                                langManager.getMessage("menu.main.status." + (configManager.isEnableUUIDWhitelist() ? "enabled" : "disabled")),
                                langManager.getMessage("menu.main.click-toggle")),
                        p -> {
                            configManager.setEnableUUIDWhitelist(!configManager.isEnableUUIDWhitelist());
                            open(p);
                        })

                .setItem('h', ChestMenuTemplate.createItem(Material.PAPER,
                                langManager.getMessage("menu.main.list.title"),
                                langManager.getMessage("menu.main.list.count", playerDataManager.getWhitelistedNames().size()),
                                langManager.getMessage("menu.main.list.click-manage")),
                        p -> new WhiteListPlayerMenu(configManager, langManager, playerDataManager).open(p, 0))

                .setItem('#', ChestMenuTemplate.createItem(Material.BLACK_STAINED_GLASS_PANE, " "), null)
                .build();
    }
}