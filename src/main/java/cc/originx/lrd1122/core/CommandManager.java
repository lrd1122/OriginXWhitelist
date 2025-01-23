package cc.originx.lrd1122.core;

import cc.originx.lrd1122.menu.WhiteListMainMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final LangManager langManager;
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("originxwhitelist.admin")) {
            sender.sendMessage(langManager.getMessage("command.no-permission"));
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player) {
                new WhiteListMainMenu(configManager, langManager, playerDataManager).open((Player) sender);
            } else {
                sendHelp(sender);
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(langManager.getMessage("menu.command.usage.add"));
                    sender.sendMessage(langManager.getMessage("menu.command.usage.types"));
                    return true;
                }
                handleAdd(sender, args[1], args[2]);
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(langManager.getMessage("menu.command.usage.remove"));
                    sender.sendMessage(langManager.getMessage("menu.command.usage.types"));
                    return true;
                }
                handleRemove(sender, args[1], args[2]);
                break;

            case "list":
                if (args.length > 1) {
                    handleTypeList(sender, args[1]);
                } else {
                    handleListAll(sender);
                }
                break;

            case "menu":
                if (sender instanceof Player) {
                    new WhiteListMainMenu(configManager, langManager, playerDataManager).open((Player) sender);
                } else {
                    sender.sendMessage(langManager.getMessage("command.player-only"));
                }
                break;

            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void handleAdd(CommandSender sender, String type, String value) {
        switch (type.toLowerCase()) {
            case "name":
                playerDataManager.addToWhitelist(value, null, null);
                sender.sendMessage(langManager.getMessage("menu.command.add.name", value));
                break;
            case "ip":
                playerDataManager.addToWhitelist(null, value, null);
                sender.sendMessage(langManager.getMessage("menu.command.add.ip", value));
                break;
            case "uuid":
                try {
                    UUID uuid = UUID.fromString(value);
                    playerDataManager.addToWhitelist(null, null, uuid);
                    sender.sendMessage(langManager.getMessage("menu.command.add.uuid", value));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(langManager.getMessage("menu.command.add.invalid-uuid"));
                }
                break;
            default:
                sender.sendMessage(langManager.getMessage("menu.command.add.invalid-type"));
        }
    }

    private void handleRemove(CommandSender sender, String type, String value) {
        switch (type.toLowerCase()) {
            case "name":
                playerDataManager.removeFromWhitelist(value, null, null);
                sender.sendMessage(langManager.getMessage("menu.command.remove.name", value));
                break;
            case "ip":
                playerDataManager.removeFromWhitelist(null, value, null);
                sender.sendMessage(langManager.getMessage("menu.command.remove.ip", value));
                break;
            case "uuid":
                try {
                    UUID uuid = UUID.fromString(value);
                    playerDataManager.removeFromWhitelist(null, null, uuid);
                    sender.sendMessage(langManager.getMessage("menu.command.remove.uuid", value));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(langManager.getMessage("menu.command.add.invalid-uuid"));
                }
                break;
            default:
                sender.sendMessage(langManager.getMessage("menu.command.add.invalid-type"));
        }
    }

    private void handleTypeList(CommandSender sender, String type) {
        switch (type.toLowerCase()) {
            case "name":
                sender.sendMessage(langManager.getMessage("menu.command.list.name-header"));
                playerDataManager.getWhitelistedNames().forEach(name ->
                        sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", name)));
                break;
            case "ip":
                sender.sendMessage(langManager.getMessage("menu.command.list.ip-header"));
                playerDataManager.getWhitelistedIPs().forEach(ip ->
                        sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", ip)));
                break;
            case "uuid":
                sender.sendMessage(langManager.getMessage("menu.command.list.uuid-header"));
                playerDataManager.getWhitelistedUUIDs().forEach(uuid ->
                        sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", uuid.toString())));
                break;
            default:
                sender.sendMessage(langManager.getMessage("menu.command.add.invalid-type"));
        }
    }

    private void handleListAll(CommandSender sender) {
        sender.sendMessage(langManager.getMessage("messages.whitelist.list-header"));
        if (!playerDataManager.getWhitelistedNames().isEmpty()) {
            sender.sendMessage(langManager.getMessage("menu.command.list.name-header"));
            playerDataManager.getWhitelistedNames().forEach(name ->
                    sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", name)));
        }
        if (!playerDataManager.getWhitelistedIPs().isEmpty()) {
            sender.sendMessage(langManager.getMessage("menu.command.list.ip-header"));
            playerDataManager.getWhitelistedIPs().forEach(ip ->
                    sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", ip)));
        }
        if (!playerDataManager.getWhitelistedUUIDs().isEmpty()) {
            sender.sendMessage(langManager.getMessage("menu.command.list.uuid-header"));
            playerDataManager.getWhitelistedUUIDs().forEach(uuid ->
                    sender.sendMessage(langManager.getMessage("messages.whitelist.list-format", uuid.toString())));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(langManager.getMessage("menu.command.help.title"));
        sender.sendMessage(langManager.getMessage("menu.command.help.add"));
        sender.sendMessage(langManager.getMessage("menu.command.help.remove"));
        sender.sendMessage(langManager.getMessage("menu.command.help.list"));
        if (sender instanceof Player) {
            sender.sendMessage(langManager.getMessage("menu.command.help.menu"));
        }
        sender.sendMessage(langManager.getMessage("menu.command.help.types"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("list");
            if (sender instanceof Player) {
                completions.add("menu");
            }
            return filterCompletions(completions, args[0]);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("remove") ||
                    args[0].equalsIgnoreCase("list")) {
                completions.add("name");
                completions.add("ip");
                completions.add("uuid");
                return filterCompletions(completions, args[1]);
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            switch (args[1].toLowerCase()) {
                case "name":
                    return filterCompletions(new ArrayList<>(playerDataManager.getWhitelistedNames()), args[2]);
                case "ip":
                    return filterCompletions(new ArrayList<>(playerDataManager.getWhitelistedIPs()), args[2]);
                case "uuid":
                    return filterCompletions(playerDataManager.getWhitelistedUUIDs().stream()
                            .map(UUID::toString)
                            .collect(Collectors.toList()), args[2]);
            }
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String prefix) {
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}