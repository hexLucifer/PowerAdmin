package org.luci.poweradmin;

import org.bukkit.plugin.java.JavaPlugin;
import org.luci.poweradmin.commands.Ban;
import org.luci.poweradmin.listeners.ChestGUIListener;

public final class PowerAdmin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ChestGUIListener(), this);

        this.saveDefaultConfig();
        this.getCommand("ban").setExecutor(new Ban(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
