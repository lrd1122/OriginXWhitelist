package cc.originx.lrd1122.events;

import cc.originx.lrd1122.OriginXWhitelist;
import cc.originx.lrd1122.core.LangManager;
import cc.originx.lrd1122.core.LoginAttemptManager;
import cc.originx.lrd1122.core.PlayerDataManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final LoginAttemptManager loginAttemptManager;
    private final LangManager langManager;

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        String ip = event.getAddress().getHostAddress();
        UUID uuid = event.getUniqueId();

        if (loginAttemptManager.isBlocked(ip)) {
            long remaining = loginAttemptManager.getBlockTimeRemaining(ip);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    ChatColor.translateAlternateColorCodes('&', langManager.getMessage("messages.kick.ip-blocked", remaining)));
            return;
        }


        if (!playerDataManager.isWhitelisted(name, ip, uuid)) {
            loginAttemptManager.recordAttempt(ip);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    ChatColor.translateAlternateColorCodes('&', OriginXWhitelist.instance.getLangManager().getMessage("messages.kick.not-whitelisted")));
        }
    }
}