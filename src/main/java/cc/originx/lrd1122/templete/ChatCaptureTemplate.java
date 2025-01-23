package cc.originx.lrd1122.templete;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatCaptureTemplate implements Listener {
    private static Plugin plugin;
    private static boolean isRegistered = false;
    private static final Map<UUID, ChatCapture> activeChatCaptures = new HashMap<>();

    /**
     * 初始化聊天捕获模板
     */
    public static void init(Plugin plugin) {
        if (!isRegistered) {
            ChatCaptureTemplate.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(new ChatCaptureTemplate(), plugin);
            isRegistered = true;
        }
    }

    /**
     * 等待玩家的下一条聊天消息
     * @param player 目标玩家
     * @param callback 接收到消息时的回调
     */
    public static void nextChat(Player player, Consumer<String> callback) {
        startCapture(player, callback, null, null, -1);
    }

    /**
     * 在指定时间内等待玩家的下一条聊天消息
     * @param player 目标玩家
     * @param timeoutTicks 超时时间（tick）
     * @param callback 接收到消息时的回调
     * @param timeoutCallback 超时时的回调
     * @param cancelCallback 取消时的回调
     */
    public static void nextChatInTicks(Player player, int timeoutTicks,
                                       Consumer<String> callback,
                                       Runnable timeoutCallback,
                                       Runnable cancelCallback) {
        startCapture(player, callback, timeoutCallback, cancelCallback, timeoutTicks);
    }

    private static void startCapture(Player player, Consumer<String> callback,
                                     Runnable timeoutCallback, Runnable cancelCallback,
                                     int timeoutTicks) {
        UUID playerId = player.getUniqueId();

        // 如果已经有活动的捕获，先取消它
        cancelExistingCapture(playerId);

        // 创建新的捕获
        ChatCapture capture = new ChatCapture(callback, timeoutCallback, cancelCallback);
        activeChatCaptures.put(playerId, capture);

        // 如果设置了超时
        if (timeoutTicks > 0) {
            capture.taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ChatCapture removed = activeChatCaptures.remove(playerId);
                if (removed != null && removed.timeoutCallback != null) {
                    removed.timeoutCallback.run();
                }
            }, timeoutTicks).getTaskId();
        }
    }

    private static void cancelExistingCapture(UUID playerId) {
        ChatCapture existing = activeChatCaptures.remove(playerId);
        if (existing != null) {
            if (existing.taskId != -1) {
                Bukkit.getScheduler().cancelTask(existing.taskId);
            }
            if (existing.cancelCallback != null) {
                existing.cancelCallback.run();
            }
        }
    }

    public static void cancelCapture(Player player) {
        cancelExistingCapture(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatCapture capture = activeChatCaptures.remove(player.getUniqueId());

        if (capture != null) {
            event.setCancelled(true);
            if (capture.taskId != -1) {
                Bukkit.getScheduler().cancelTask(capture.taskId);
            }

            // 在主线程中执行回调
            Bukkit.getScheduler().runTask(plugin, () ->
                    capture.callback.accept(event.getMessage()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelExistingCapture(event.getPlayer().getUniqueId());
    }

    private static class ChatCapture {
        private final Consumer<String> callback;
        private final Runnable timeoutCallback;
        private final Runnable cancelCallback;
        private int taskId = -1;

        private ChatCapture(Consumer<String> callback, Runnable timeoutCallback, Runnable cancelCallback) {
            this.callback = callback;
            this.timeoutCallback = timeoutCallback;
            this.cancelCallback = cancelCallback;
        }
    }
}