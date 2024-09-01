package org.luci.poweradmin.utils.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ChestGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, Runnable> clickActions;

    public ChestGUI(String title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.clickActions = new HashMap<>();
    }

    public void setItem(int slot, ItemStack item, Runnable action) {
        inventory.setItem(slot, item);
        clickActions.put(slot, action);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (clickActions.containsKey(slot)) {
            clickActions.get(slot).run();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
