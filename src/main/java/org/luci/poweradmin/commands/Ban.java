package org.luci.poweradmin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.luci.poweradmin.utils.guis.ChestGUI;

import java.io.File;
import java.util.List;

public class Ban implements CommandExecutor {

    private final FileConfiguration banConfig;

    public Ban(JavaPlugin plugin) {
        // Load the configuration file
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        banConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    private void mainGUI(Player player, Player target) {
        ChestGUI mainGUI = new ChestGUI("Ban " + target.getName(), 27);

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta meta = border.getItemMeta();
        meta.displayName(Component.empty());
        border.setItemMeta(meta);

        for (int slot = 0; slot < mainGUI.getInventory().getSize(); slot++) {
            mainGUI.setItem(slot, border, () -> {});
        }

        if (banConfig != null) {
            for (int slot = 9; slot <= 17; slot++) {
                String slotKey = "slot_" + slot;
                if (banConfig.contains("ban_reasons." + slotKey)) {
                    String materialName = banConfig.getString("ban_reasons." + slotKey + ".material");
                    Material material = Material.matchMaterial(materialName);

                    if (material != null) {
                        ItemStack itemStack = new ItemStack(material);

                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta != null) {
                            String name = banConfig.getString("ban_reasons." + slotKey + ".name");
                            if (name != null) {
                                itemMeta.displayName(Component.text(name).color(NamedTextColor.RED));
                            }

                            List<String> lore = banConfig.getStringList("ban_reasons." + slotKey + ".lore");
                            if (lore != null && !lore.isEmpty()) {
                                itemMeta.lore(lore.stream().map(Component::text).toList());
                            }

                            itemStack.setItemMeta(itemMeta);
                        }

                        mainGUI.setItem(slot, itemStack, () -> {
                            switch (itemMeta.displayName().toString()) {
                                case "Cheating":
                                    player.sendMessage("Selected reason: Cheating");
                                    // Here you could open a specific GUI for Cheating
                                    // new CheatingGUI().open(player);
                                    break;
                                case "Harassment":
                                    player.sendMessage("Selected reason: Harassment");
                                    // Here you could open a specific GUI for Harassment
                                    // new HarassmentGUI().open(player);
                                    break;
                                default:
                                    player.sendMessage("Selected reason: " + itemMeta.displayName().toString());
                            }
                        });
                    }
                }
            }
        }

        mainGUI.open(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("poweradmin.full.ban")) {
                if (args.length > 0) {
                    String targetName = args[0];
                    Player target = Bukkit.getPlayer(targetName);

                    if (target != null) {
                        mainGUI(player, target);
                        return true;
                    } else {
                        player.sendMessage("The player " + targetName + " is not online.");
                        return false;
                    }
                } else {
                    player.sendMessage("Please specify a player to ban.");
                    return false;
                }
            } else {
                player.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }
    }
}
