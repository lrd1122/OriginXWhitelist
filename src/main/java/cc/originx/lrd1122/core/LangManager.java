package cc.originx.lrd1122.core;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class LangManager {
    private final JavaPlugin plugin;
    private final File langFolder;
    private File currentLangFile;
    private FileConfiguration langConfig;
    private final Map<String, String> defaultMessages;
    private String currentLanguage;

    public LangManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultMessages = new HashMap<>();
        this.langFolder = new File(plugin.getDataFolder(), "lang");

        // 获取系统默认语言和地区
        Locale defaultLocale = Locale.getDefault();
        String defaultLang = defaultLocale.getLanguage() + "_" + defaultLocale.getCountry();

        // 从配置文件获取语言设置，如果没有则使用系统语言
        this.currentLanguage = plugin.getConfig().getString("language", defaultLang);

        // 初始化语言文件
        this.currentLangFile = new File(langFolder, currentLanguage + ".yml");

        loadDefaults();
        loadConfig();
        updateConfig();
    }

    private void loadDefaults() {
        if (!langFolder.exists()) {
            langFolder.mkdirs();
            saveDefaultLanguages();
        }

        String defaultFile = "lang/" + currentLanguage + ".yml";
        InputStream defaultStream = plugin.getResource(defaultFile);

        // 如果找不到对应语言文件，回退到英语
        if (defaultStream == null) {
            defaultStream = plugin.getResource("lang/en_US.yml");
            currentLanguage = "en_US";
            currentLangFile = new File(langFolder, "en_US.yml");
            plugin.getConfig().set("language", "en_US");
            plugin.saveConfig();
        }

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
            );
            for (String key : defaultConfig.getKeys(true)) {
                if (defaultConfig.isString(key)) {
                    defaultMessages.put(key, defaultConfig.getString(key));
                }
            }
        }
    }

    private void saveDefaultLanguages() {
        String[] defaultLangs = {"en_US.yml", "zh_CN.yml"};
        for (String lang : defaultLangs) {
            InputStream is = plugin.getResource("lang/" + lang);
            if (is != null) {
                File outFile = new File(langFolder, lang);
                try {
                    if (!outFile.exists()) {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = is.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadConfig() {
        if (!currentLangFile.exists()) {
            File defaultLangFile = new File(langFolder, currentLanguage + ".yml");
            try {
                defaultLangFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(currentLangFile);
    }

    private void updateConfig() {
        boolean needsSave = false;
        for (Map.Entry<String, String> entry : defaultMessages.entrySet()) {
            if (!langConfig.contains(entry.getKey())) {
                langConfig.set(entry.getKey(), entry.getValue());
                needsSave = true;
            }
        }
        if (needsSave) {
            try {
                langConfig.save(currentLangFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLanguage(String language) {
        if (!language.equals(currentLanguage)) {
            currentLanguage = language;
            currentLangFile = new File(langFolder, language + ".yml");
            plugin.getConfig().set("language", language);
            plugin.saveConfig();

            defaultMessages.clear();
            loadDefaults();
            loadConfig();
            updateConfig();
        }
    }

    public String getMessage(String key) {
        return langConfig.getString(key, defaultMessages.getOrDefault(key, "Missing message: " + key));
    }

    public String getMessage(String key, Object... args) {
        String message = getMessage(key);
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }
}