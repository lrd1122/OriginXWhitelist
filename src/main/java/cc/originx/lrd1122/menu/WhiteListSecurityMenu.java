package cc.originx.lrd1122.menu;

import cc.originx.lrd1122.OriginXWhitelist;
import cc.originx.lrd1122.core.ConfigManager;
import cc.originx.lrd1122.core.LangManager;
import cc.originx.lrd1122.core.PlayerDataManager;
import cc.originx.lrd1122.templete.ChatCaptureTemplate;
import cc.originx.lrd1122.templete.ChestMenuTemplate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WhiteListSecurityMenu {
    private final ConfigManager configManager;
    private final LangManager langManager;
    private final PlayerDataManager playerDataManager;

    public void open(Player player) {
        ChestMenuTemplate.createMenu(player, langManager.getMessage("menu.security.title"),
                        "#########",
                        "#abcdefg#",
                        "#hijklmn#",
                        "####B####")
                .setItem('a', ChestMenuTemplate.createItem(Material.REPEATER,
                                langManager.getMessage("menu.security.attempts.title"),
                                langManager.getMessage("menu.security.attempts.current", configManager.getMaxLoginAttempts()),
                                langManager.getMessage("menu.security.attempts.description"),
                                langManager.getMessage("menu.security.click-edit")),
                        p -> {
                            p.closeInventory();
                            p.sendMessage(langManager.getMessage("menu.security.attempts.enter-new"));
                            ChatCaptureTemplate.nextChat(p, msg -> {
                                try {
                                    int attempts = Integer.parseInt(msg);
                                    if (attempts < 1) {
                                        p.sendMessage(langManager.getMessage("menu.security.attempts.must-positive"));
                                    } else {
                                        configManager.setMaxLoginAttempts(attempts);
                                        p.sendMessage(langManager.getMessage("menu.security.save-success"));
                                    }
                                } catch (NumberFormatException e) {
                                    p.sendMessage(langManager.getMessage("menu.security.invalid-number"));
                                }
                                open(p);
                            });
                        })

                .setItem('b', ChestMenuTemplate.createItem(Material.CLOCK,
                                langManager.getMessage("menu.security.duration.title"),
                                langManager.getMessage("menu.security.duration.current", configManager.getBlockDuration()),
                                langManager.getMessage("menu.security.duration.description"),
                                langManager.getMessage("menu.security.click-edit")),
                        p -> {
                            p.closeInventory();
                            p.sendMessage(langManager.getMessage("menu.security.duration.enter-new"));
                            ChatCaptureTemplate.nextChat(p, msg -> {
                                try {
                                    int duration = Integer.parseInt(msg);
                                    if (duration < 1) {
                                        p.sendMessage(langManager.getMessage("menu.security.duration.must-positive"));
                                    } else {
                                        configManager.setBlockDuration(duration);
                                        p.sendMessage(langManager.getMessage("menu.security.save-success"));
                                    }
                                } catch (NumberFormatException e) {
                                    p.sendMessage(langManager.getMessage("menu.security.invalid-number"));
                                }
                                open(p);
                            });
                        })

                .setItem('h', ChestMenuTemplate.createItem(Material.BARRIER,
                                langManager.getMessage("menu.security.clear.title"),
                                langManager.getMessage("menu.security.clear.description")),
                        p -> {
                            OriginXWhitelist.instance.getLoginAttemptManager().clearBlocks();
                            p.sendMessage(langManager.getMessage("menu.security.clear.success"));
                            open(p);
                        })

                .setItem('B', ChestMenuTemplate.createItem(Material.ARROW,
                                langManager.getMessage("menu.common.back.title"),
                                langManager.getMessage("menu.common.back.lore")),
                        p -> new WhiteListMainMenu(configManager, langManager, playerDataManager).open(p))

                .setItem('#', ChestMenuTemplate.createItem(Material.BLACK_STAINED_GLASS_PANE, " "), null)
                .build();
    }
}