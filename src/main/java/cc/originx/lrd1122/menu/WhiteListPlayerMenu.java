package cc.originx.lrd1122.menu;

import cc.originx.lrd1122.core.ConfigManager;
import cc.originx.lrd1122.core.LangManager;
import cc.originx.lrd1122.core.PlayerDataManager;
import cc.originx.lrd1122.templete.ChatCaptureTemplate;
import cc.originx.lrd1122.templete.ChestMenuTemplate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class WhiteListPlayerMenu {
    private static final char[] PLAYER_SLOTS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u'
    };

    private final ConfigManager configManager;
    private final LangManager langManager;
    private final PlayerDataManager playerDataManager;

    public void open(Player player, int page) {
        List<String> whitelistedPlayers = new ArrayList<>(playerDataManager.getWhitelistedNames());
        List<String> whitelistedIPs = new ArrayList<>(playerDataManager.getWhitelistedIPs());
        List<UUID> whitelistedUUIDs = new ArrayList<>(playerDataManager.getWhitelistedUUIDs());

        int totalItems = whitelistedPlayers.size() + whitelistedIPs.size() + whitelistedUUIDs.size();
        int totalPages = (totalItems - 1) / PLAYER_SLOTS.length + 1;

        ChestMenuTemplate.MenuBuilder builder = ChestMenuTemplate.createMenu(player, langManager.getMessage("menu.whitelist.title"),
                "#########",
                "#abcdefg#",
                "#hijklmn#",
                "#opqrstu#",
                "#B#N#P###");

        builder.setItem('#', ChestMenuTemplate.createItem(Material.BLACK_STAINED_GLASS_PANE, " "), null);

        builder.setItem('B', ChestMenuTemplate.createItem(Material.ARROW,
                        langManager.getMessage("menu.whitelist.back.title"),
                        langManager.getMessage("menu.whitelist.back.lore")),
                p -> new WhiteListMainMenu(configManager, langManager, playerDataManager).open(p));

        builder.setItem('N', ChestMenuTemplate.createItem(Material.EMERALD,
                        langManager.getMessage("menu.whitelist.add.title"),
                        langManager.getMessage("menu.whitelist.add.lore")),
                p -> {
                    p.closeInventory();
                    p.sendMessage(langManager.getMessage("menu.whitelist.add.select-type"));
                    p.sendMessage(langManager.getMessage("menu.whitelist.add.type.name"));
                    p.sendMessage(langManager.getMessage("menu.whitelist.add.type.ip"));
                    p.sendMessage(langManager.getMessage("menu.whitelist.add.type.uuid"));

                    ChatCaptureTemplate.nextChat(p, type -> {
                        switch (type) {
                            case "1":
                                p.sendMessage(langManager.getMessage("menu.whitelist.add.enter-name"));
                                ChatCaptureTemplate.nextChat(p, name -> {
                                    if (playerDataManager.getWhitelistedNames().contains(name.toLowerCase())) {
                                        p.sendMessage(langManager.getMessage("menu.whitelist.add.name-exists"));
                                    } else {
                                        playerDataManager.addToWhitelist(name, null, null);
                                        p.sendMessage(langManager.getMessage("menu.whitelist.add.name-success", name));
                                    }
                                    open(p, page);
                                });
                                break;
                            case "2":
                                p.sendMessage(langManager.getMessage("menu.whitelist.add.enter-ip"));
                                ChatCaptureTemplate.nextChat(p, ip -> {
                                    if (playerDataManager.getWhitelistedIPs().contains(ip)) {
                                        p.sendMessage(langManager.getMessage("menu.whitelist.add.ip-exists"));
                                    } else {
                                        playerDataManager.addToWhitelist(null, ip, null);
                                        p.sendMessage(langManager.getMessage("menu.whitelist.add.ip-success", ip));
                                    }
                                    open(p, page);
                                });
                                break;
                            case "3":
                                p.sendMessage(langManager.getMessage("menu.whitelist.add.enter-uuid"));
                                ChatCaptureTemplate.nextChat(p, uuidStr -> {
                                    try {
                                        UUID uuid = UUID.fromString(uuidStr);
                                        if (playerDataManager.getWhitelistedUUIDs().contains(uuid)) {
                                            p.sendMessage(langManager.getMessage("menu.whitelist.add.uuid-exists"));
                                        } else {
                                            playerDataManager.addToWhitelist(null, null, uuid);
                                            p.sendMessage(langManager.getMessage("menu.whitelist.add.uuid-success", uuidStr));
                                        }
                                    } catch (IllegalArgumentException e) {
                                        p.sendMessage(langManager.getMessage("menu.whitelist.add.invalid-uuid"));
                                    }
                                    open(p, page);
                                });
                                break;
                            default:
                                p.sendMessage(langManager.getMessage("menu.whitelist.add.invalid-type"));
                                open(p, page);
                                break;
                        }
                    });
                });

        String pageInfo = langManager.getMessage("menu.whitelist.page-info", page + 1, totalPages);
        if (page > 0) {
            builder.setItem('P', ChestMenuTemplate.createItem(Material.ARROW,
                            langManager.getMessage("menu.whitelist.prev-page"),
                            pageInfo),
                    p -> open(p, page - 1));
        }

        if (page < totalPages - 1) {
            builder.setItem('P', ChestMenuTemplate.createItem(Material.ARROW,
                            langManager.getMessage("menu.whitelist.next-page"),
                            pageInfo),
                    p -> open(p, page + 1));
        }

        List<WhitelistEntry> allEntries = new ArrayList<>();
        whitelistedPlayers.forEach(name -> allEntries.add(new WhitelistEntry("name", name, name)));
        whitelistedIPs.forEach(ip -> allEntries.add(new WhitelistEntry("ip", ip, "unknown#" + ip.hashCode())));
        whitelistedUUIDs.forEach(uuid -> {
            String name = playerDataManager.getNameByUUID(uuid);
            allEntries.add(new WhitelistEntry("uuid", uuid.toString(), name != null ? name : "unknown#" + uuid.toString().substring(0, 8)));
        });

        int startIndex = page * PLAYER_SLOTS.length;
        for (int i = 0; i < PLAYER_SLOTS.length && (startIndex + i) < allEntries.size(); i++) {
            final WhitelistEntry entry = allEntries.get(startIndex + i);
            final char slot = PLAYER_SLOTS[i];

            builder.setItem(slot, ChestMenuTemplate.createItem(Material.PLAYER_HEAD,
                            langManager.getMessage("menu.whitelist.entry.name", entry.displayName),
                            langManager.getMessage("menu.whitelist.entry.type", getTypeDisplay(entry.type, langManager)),
                            langManager.getMessage("menu.whitelist.entry.value", entry.value)),
                    null);

            builder.setRightClickItem(slot, null, p -> {
                switch (entry.type) {
                    case "name":
                        playerDataManager.removeFromWhitelist(entry.value, null, null);
                        break;
                    case "ip":
                        playerDataManager.removeFromWhitelist(null, entry.value, null);
                        break;
                    case "uuid":
                        playerDataManager.removeFromWhitelist(null, null, UUID.fromString(entry.value));
                        break;
                }
                p.sendMessage(langManager.getMessage("menu.whitelist.remove-success", entry.displayName));
                open(p, page);
            });
        }

        builder.build();
    }

    private String getTypeDisplay(String type, LangManager langManager) {
        return langManager.getMessage("menu.whitelist.type." + type);
    }

    private static class WhitelistEntry {
        final String type;
        final String value;
        final String displayName;

        WhitelistEntry(String type, String value, String displayName) {
            this.type = type;
            this.value = value;
            this.displayName = displayName;
        }
    }
}