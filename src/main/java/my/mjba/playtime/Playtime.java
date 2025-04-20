package my.mjba.playtime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.ChatColor;

public final class Playtime extends JavaPlugin {
    private PlaytimeManager playtimeManager;
    private File playtimeFile;
    private PlaytimeExpansion playtimeExpansion;

    @Override
    public void onEnable() {
        try {
            // Initialize configuration
            saveDefaultConfig();
            
            // Setup playtime data storage
            playtimeFile = new File(getDataFolder(), "playtime.yml");
            if (!playtimeFile.exists()) {
                playtimeFile.getParentFile().mkdirs();
                saveResource("playtime.yml", false);
            }
            
            YamlConfiguration.loadConfiguration(playtimeFile);
            
            // Initialize playtime manager
            playtimeManager = new PlaytimeManager(this);
            
            // Register commands
            getCommand("playtime").setExecutor(this);
            getCommand("playtimereload").setExecutor(this);
            
            // Register event listener
            getServer().getPluginManager().registerEvents(new PlaytimeListener(playtimeManager), this);
            
            // Register PlaceholderAPI expansion
            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                playtimeExpansion = new PlaytimeExpansion(playtimeManager);
                playtimeExpansion.register();
                sendConfigMessage(getServer().getConsoleSender(), "messages.placeholders-registered");
            } else {
                sendConfigMessage(getServer().getConsoleSender(), "messages.placeholders-not-found");
            }
            
            sendConfigMessage(getServer().getConsoleSender(), "messages.plugin-enabled");
        } catch (Exception e) {
            getLogger().severe("Error enabling Playtime plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            // Save all playtime data if manager exists
            if (playtimeManager != null) {
                playtimeManager.saveAllPlaytime();
                sendConfigMessage(getServer().getConsoleSender(), "messages.data-saved");
            }
            if (playtimeExpansion != null) {
                playtimeExpansion.unregister();
            }
            sendConfigMessage(getServer().getConsoleSender(), "messages.plugin-disabled");
        } catch (Exception e) {
            getLogger().severe("Error disabling Playtime plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendConfigMessage(CommandSender sender, String path) {
        String message = getConfig().getString(path);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("playtimereload")) {
            if (!sender.hasPermission("playtime.reload")) {
                sendConfigMessage(sender, "messages.no-permission");
                return true;
            }
            
            try {
                reloadConfig();
                sendConfigMessage(sender, "messages.reload-success");
            } catch (Exception e) {
                sendConfigMessage(sender, "messages.reload-error");
                e.printStackTrace();
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("playtime")) {
            if (args.length == 0 && !(sender instanceof Player)) {
                sendConfigMessage(sender, "messages.console-error");
                return true;
            }

            if (args.length == 0) {
                // Show own playtime
                Player player = (Player) sender;
                sender.sendMessage(playtimeManager.getFormattedPlaytimeMessage(player, true));
                return true;
            }

            if (args[0].equalsIgnoreCase("top")) {
                if (!sender.hasPermission("playtime.top")) {
                    sendConfigMessage(sender, "messages.no-permission");
                    return true;
                }
                
                int limit = 10;
                if (args.length > 1) {
                    try {
                        limit = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sendConfigMessage(sender, "messages.invalid-number");
                        return true;
                    }
                }
                sender.sendMessage(playtimeManager.getFormattedTopList(limit));
                return true;
            }

            // Show other player's playtime
            if (!sender.hasPermission("playtime.others")) {
                sendConfigMessage(sender, "messages.no-permission");
                return true;
            }

            Player target = getServer().getPlayer(args[0]);
            if (target == null) {
                sendConfigMessage(sender, "messages.player-not-found");
                return true;
            }

            sender.sendMessage(playtimeManager.getFormattedPlaytimeMessage(target, false));
            return true;
        }

        return false;
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }
}
