package cc.originx.lrd1122.core;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class LoginAttemptManager {
    private final Map<String, Integer> attempts = new HashMap<>();
    private final Map<String, Long> blockExpiry = new HashMap<>();
    private final ConfigManager config;

    public LoginAttemptManager(ConfigManager config) {
        this.config = config;
    }

    public void recordAttempt(String ip) {
        attempts.merge(ip, 1, Integer::sum);
        if (attempts.get(ip) >= config.getMaxLoginAttempts()) {
            blockExpiry.put(ip, System.currentTimeMillis() + config.getBlockDuration() * 1000L);
        }
    }

    public boolean isBlocked(String ip) {
        Long expiry = blockExpiry.get(ip);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public long getBlockTimeRemaining(String ip) {
        Long expiry = blockExpiry.get(ip);
        return expiry == null ? 0 : (expiry - System.currentTimeMillis()) / 1000;
    }

    public void clearBlocks() {
        attempts.clear();
        blockExpiry.clear();
    }
}