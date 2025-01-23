package cc.originx.lrd1122.templete;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

@Data
public class ChestMenuTemplate implements Listener {
    private static boolean isRegistered = false;
    private static Plugin plugin;

    public static void init(Plugin plugin) {
        if (!isRegistered) {
            ChestMenuTemplate.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(new ChestMenuTemplate(), plugin);
            isRegistered = true;
        }
    }

    public static MenuBuilder createMenu(Player player, String title, String... pattern) {
        if (!isRegistered) {
            throw new IllegalStateException("ChestMenuTemplate has not been initialized! Call ChestMenuTemplate.init(plugin) first!");
        }
        return new MenuBuilder(player, title, pattern);
    }

    public static class MenuHolder implements InventoryHolder {
        private final Map<Integer, ClickHandlers> handlers = new HashMap<>();
        private final UUID menuId = UUID.randomUUID();

        @Override
        public Inventory getInventory() {
            return null;
        }

        public Map<Integer, ClickHandlers> getHandlers() {
            return handlers;
        }

        public UUID getMenuId() {
            return menuId;
        }
    }

    public static class ClickHandlers {
        private ClickHandler leftClickHandler;
        private ClickHandler rightClickHandler;
        private ClickHandler shiftLeftClickHandler;
        private ClickHandler shiftRightClickHandler;
        private ClickHandler generalClickHandler;

        public ClickHandlers() {}

        public void setLeftClickHandler(ClickHandler handler) {
            this.leftClickHandler = handler;
        }

        public void setRightClickHandler(ClickHandler handler) {
            this.rightClickHandler = handler;
        }

        public void setShiftLeftClickHandler(ClickHandler handler) {
            this.shiftLeftClickHandler = handler;
        }

        public void setShiftRightClickHandler(ClickHandler handler) {
            this.shiftRightClickHandler = handler;
        }

        public void setGeneralClickHandler(ClickHandler handler) {
            this.generalClickHandler = handler;
        }

        public ClickHandler getLeftClickHandler() {
            return leftClickHandler;
        }

        public ClickHandler getRightClickHandler() {
            return rightClickHandler;
        }

        public ClickHandler getShiftLeftClickHandler() {
            return shiftLeftClickHandler;
        }

        public ClickHandler getShiftRightClickHandler() {
            return shiftRightClickHandler;
        }

        public ClickHandler getGeneralClickHandler() {
            return generalClickHandler;
        }
    }

    public static class MenuBuilder {
        private final Player player;
        private final List<String[]> pages = new ArrayList<>();
        private final Map<Character, MenuItem> items = new HashMap<>();
        private final Map<Integer, ClickHandlers> globalHandlers = new HashMap<>();
        private String title;
        private int currentPage = 0;
        private MenuHolder holder;
        private Inventory inventory;

        private MenuBuilder(Player player, String title, String... pattern) {
            this.player = player;
            this.title = title;
            this.pages.add(pattern);
            this.holder = new MenuHolder();
            this.inventory = Bukkit.createInventory(holder, pattern.length * 9, title);
        }

        // 普通点击
        public MenuBuilder setItem(char symbol, ItemStack item, ClickHandler handler) {
            MenuItem menuItem = items.computeIfAbsent(symbol, k -> new MenuItem(item));
            menuItem.getHandlers().setGeneralClickHandler(handler);
            return this;
        }

        // 左键点击
        public MenuBuilder setLeftClickItem(char symbol, ItemStack item, ClickHandler handler) {
            MenuItem menuItem = items.computeIfAbsent(symbol, k -> new MenuItem(item));
            menuItem.getHandlers().setLeftClickHandler(handler);
            return this;
        }

        // 右键点击
        public MenuBuilder setRightClickItem(char symbol, ItemStack item, ClickHandler handler) {
            MenuItem menuItem = items.computeIfAbsent(symbol, k -> new MenuItem(item));
            menuItem.getHandlers().setRightClickHandler(handler);
            return this;
        }

        // Shift+左键点击
        public MenuBuilder setShiftLeftClickItem(char symbol, ItemStack item, ClickHandler handler) {
            MenuItem menuItem = items.computeIfAbsent(symbol, k -> new MenuItem(item));
            menuItem.getHandlers().setShiftLeftClickHandler(handler);
            return this;
        }

        // Shift+右键点击
        public MenuBuilder setShiftRightClickItem(char symbol, ItemStack item, ClickHandler handler) {
            MenuItem menuItem = items.computeIfAbsent(symbol, k -> new MenuItem(item));
            menuItem.getHandlers().setShiftRightClickHandler(handler);
            return this;
        }

        public MenuBuilder addPage(String... pattern) {
            this.pages.add(pattern);
            return this;
        }

        public MenuBuilder setGlobalClickHandler(int slot, ClickHandler handler) {
            ClickHandlers handlers = globalHandlers.computeIfAbsent(slot, k -> new ClickHandlers());
            handlers.setGeneralClickHandler(handler);
            return this;
        }

        public void build() {
            renderPage(currentPage);
            player.openInventory(inventory);
        }

        private void renderPage(int page) {
            if (page < 0 || page >= pages.size()) return;

            inventory.clear();
            holder.getHandlers().clear();

            String[] pattern = pages.get(page);

            for (int row = 0; row < pattern.length; row++) {
                String line = pattern[row];
                for (int col = 0; col < line.length() && col < 9; col++) {
                    char symbol = line.charAt(col);
                    int slot = row * 9 + col;
                    if (items.containsKey(symbol)) {
                        MenuItem menuItem = items.get(symbol);
                        inventory.setItem(slot, menuItem.item);
                        if (menuItem.handlers != null) {
                            holder.getHandlers().put(slot, menuItem.handlers);
                        }
                    }
                }
            }

            // 添加分页按钮
            if (currentPage > 0) {
                ItemStack previousPage = createItem(Material.ARROW, "&a上一页", "&f点击返回上一页");
                int slot = inventory.getSize() - 9;
                inventory.setItem(slot, previousPage);
                ClickHandlers handlers = new ClickHandlers();
                handlers.setGeneralClickHandler(player -> {
                    currentPage--;
                    renderPage(currentPage);
                });
                holder.getHandlers().put(slot, handlers);
            }

            if (currentPage < pages.size() - 1) {
                ItemStack nextPage = createItem(Material.ARROW, "&a下一页", "&f点击前往下一页");
                int slot = inventory.getSize() - 1;
                inventory.setItem(slot, nextPage);
                ClickHandlers handlers = new ClickHandlers();
                handlers.setGeneralClickHandler(player -> {
                    currentPage++;
                    renderPage(currentPage);
                });
                holder.getHandlers().put(slot, handlers);
            }
        }
    }

    private static class MenuItem {
        private final ItemStack item;
        private final ClickHandlers handlers;

        private MenuItem(ItemStack item) {
            this.item = item;
            this.handlers = new ClickHandlers();
        }

        public ClickHandlers getHandlers() {
            return handlers;
        }
    }

    @FunctionalInterface
    public interface ClickHandler {
        void onClick(Player player);
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name.replace("&", "§"));
            if (lore.length > 0) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(line.replace("&", "§"));
                }
                meta.setLore(coloredLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (!(clickedInventory.getHolder() instanceof MenuHolder)) return;

        event.setCancelled(true);

        if (event.getRawSlot() != event.getSlot()) return;

        MenuHolder holder = (MenuHolder) clickedInventory.getHolder();
        ClickHandlers handlers = holder.getHandlers().get(event.getSlot());

        if (handlers != null) {
            ClickHandler handler = null;

            // 根据点击类型选择对应的处理器
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                handler = handlers.getShiftLeftClickHandler();
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                handler = handlers.getShiftRightClickHandler();
            } else if (event.getClick() == ClickType.LEFT) {
                handler = handlers.getLeftClickHandler();
            } else if (event.getClick() == ClickType.RIGHT) {
                handler = handlers.getRightClickHandler();
            }

            // 如果没有特定的点击类型处理器，使用通用处理器
            if (handler == null) {
                handler = handlers.getGeneralClickHandler();
            }

            if (handler != null) {
                final ClickHandler finalHandler = handler;
                Bukkit.getScheduler().runTask(plugin, () -> finalHandler.onClick((Player) event.getWhoClicked()));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MenuHolder) {
            MenuHolder holder = (MenuHolder) event.getInventory().getHolder();
            holder.getHandlers().clear();
        }
    }
}