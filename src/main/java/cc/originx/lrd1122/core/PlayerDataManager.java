package cc.originx.lrd1122.core;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerDataManager {
    private final File dataFile;
    private FileConfiguration config;
    private final Set<String> whitelistedNames;
    private final Set<String> whitelistedIPs;
    private final Set<UUID> whitelistedUUIDs;
    private final Map<String, UUID> nameToUUID;

    public PlayerDataManager(File dataFolder) {
        this.dataFile = new File(dataFolder, "playerdata.yml");
        this.whitelistedNames = ConcurrentHashMap.newKeySet();
        this.whitelistedIPs = ConcurrentHashMap.newKeySet();
        this.whitelistedUUIDs = ConcurrentHashMap.newKeySet();
        this.nameToUUID = new ConcurrentHashMap<>();
        loadData();
    }
    public String getNameByUUID(UUID uuid) {
        for (Map.Entry<String, UUID> entry : nameToUUID.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }
    private void loadData() {
        if (!dataFile.exists()) {
            createFile();
        }
        config = YamlConfiguration.loadConfiguration(dataFile);
        loadWhitelist();
    }

    private void createFile() {
        dataFile.getParentFile().mkdirs();
        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWhitelist() {
        whitelistedNames.clear();
        whitelistedIPs.clear();
        whitelistedUUIDs.clear();
        nameToUUID.clear();

        whitelistedNames.addAll(config.getStringList("whitelist.names"));
        whitelistedIPs.addAll(config.getStringList("whitelist.ips"));
        config.getStringList("whitelist.uuids").forEach(uuid ->
                whitelistedUUIDs.add(UUID.fromString(uuid)));

        ConfigurationSection uuidMap = config.getConfigurationSection("name-uuid-map");
        if (uuidMap != null) {
            uuidMap.getKeys(false).forEach(name ->
                    nameToUUID.put(name, UUID.fromString(uuidMap.getString(name))));
        }
    }

    public void saveData() {
        config.set("whitelist.names", new ArrayList<>(whitelistedNames));
        config.set("whitelist.ips", new ArrayList<>(whitelistedIPs));
        config.set("whitelist.uuids",
                whitelistedUUIDs.stream().map(UUID::toString).collect(Collectors.toList()));

        nameToUUID.forEach((name, uuid) ->
                config.set("name-uuid-map." + name, uuid.toString()));

        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWhitelisted(String name, String ip, UUID uuid) {
        return whitelistedNames.contains(name.toLowerCase()) ||
                whitelistedIPs.contains(ip) ||
                whitelistedUUIDs.contains(uuid);
    }

    public void addToWhitelist(String name, String ip, UUID uuid) {
        if (name != null) whitelistedNames.add(name.toLowerCase());
        if (ip != null) whitelistedIPs.add(ip);
        if (uuid != null) {
            whitelistedUUIDs.add(uuid);
            if (name != null) nameToUUID.put(name.toLowerCase(), uuid);
        }
        saveData();
    }

    public void removeFromWhitelist(String name, String ip, UUID uuid) {
        if (name != null) whitelistedNames.remove(name.toLowerCase());
        if (ip != null) whitelistedIPs.remove(ip);
        if (uuid != null) whitelistedUUIDs.remove(uuid);
        if (name != null) nameToUUID.remove(name.toLowerCase());
        saveData();
    }

    public Set<String> getWhitelistedNames() {
        return Collections.unmodifiableSet(whitelistedNames);
    }

    public Set<String> getWhitelistedIPs() {
        return Collections.unmodifiableSet(whitelistedIPs);
    }

    public Set<UUID> getWhitelistedUUIDs() {
        return Collections.unmodifiableSet(whitelistedUUIDs);
    }

    public UUID getUUID(String name) {
        return nameToUUID.get(name.toLowerCase());
    }
}