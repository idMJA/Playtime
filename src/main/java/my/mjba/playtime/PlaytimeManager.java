package my.mjba.playtime;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Statistic;
import org.bukkit.ChatColor;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlaytimeManager {
    private final Playtime plugin;
    private final Map<UUID, Long> playtimeData;
    private final File playtimeFile;
    private FileConfiguration playtimeConfig;
    private final long serverStartTime;

    public PlaytimeManager(Playtime plugin) {
        this.plugin = plugin;
        this.playtimeData = new HashMap<>();
        new HashMap<>();
        this.playtimeFile = new File(plugin.getDataFolder(), "playtime.yml");
        this.serverStartTime = System.currentTimeMillis();
        
        // Create playtime.yml if it doesn't exist
        if (!playtimeFile.exists()) {
            try {
                playtimeFile.getParentFile().mkdirs();
                playtimeFile.createNewFile();
                plugin.saveResource("playtime.yml", false);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playtime.yml!");
                e.printStackTrace();
            }
        }

        // Load existing data
        this.playtimeConfig = YamlConfiguration.loadConfiguration(playtimeFile);
        loadPlaytimeData();
        startRealtimeTracking();
    }

    private void loadPlaytimeData() {
        if (playtimeConfig.contains("playtime")) {
            ConfigurationSection playtimeSection = playtimeConfig.getConfigurationSection("playtime");
            if (playtimeSection != null) {
                for (String uuidString : playtimeSection.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        long playtime = playtimeSection.getLong(uuidString);
                        playtimeData.put(uuid, playtime);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID in playtime data: " + uuidString);
                    }
                }
            }
        }
    }

    private void startRealtimeTracking() {
        // Update and save playtime every minute
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAndSavePlaytime();
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // 1200 ticks = 1 minute
    }

    private void updateAndSavePlaytime() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            // Convert ticks to milliseconds (1 tick = 50ms)
            long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
            playtimeData.put(uuid, playtime);
        }
        saveAllPlaytime();
    }

    public void saveAllPlaytime() {
        // Clear existing data in config
        playtimeConfig.set("playtime", null);
        
        // Save current data
        for (Map.Entry<UUID, Long> entry : playtimeData.entrySet()) {
            playtimeConfig.set("playtime." + entry.getKey().toString(), entry.getValue());
        }

        try {
            playtimeConfig.save(playtimeFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playtime data!");
            e.printStackTrace();
        }
    }

    public void startTracking(Player player) {
        // Support for cracked players - use name as fallback if UUID is not available
        UUID uuid = player.getUniqueId();
        if (uuid == null || uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
            // Use player name as identifier for cracked players
            String playerName = player.getName().toLowerCase();
            uuid = UUID.nameUUIDFromBytes(playerName.getBytes());
        }
        
        // Convert ticks to milliseconds (1 tick = 50ms)
        long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
        playtimeData.put(uuid, playtime);
    }

    public void stopTracking(Player player) {
        UUID uuid = player.getUniqueId();
        if (uuid == null || uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
            String playerName = player.getName().toLowerCase();
            uuid = UUID.nameUUIDFromBytes(playerName.getBytes());
        }
        
        // Save final playtime
        long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
        playtimeData.put(uuid, playtime);
        saveAllPlaytime();
    }

    public long getPlaytime(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // For online players, get current statistic
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
        }
        
        // For offline players, return stored value
        return playtimeData.getOrDefault(uuid, 0L);
    }

    public long getPlaytime(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
    }

    public Playtime getPlugin() {
        return plugin;
    }

    public List<Map.Entry<String, Long>> getTopPlayers(int limit) {
        // Update playtime before getting top list
        updateAndSavePlaytime();
        
        return playtimeData.entrySet().stream()
            .map(entry -> {
                String playerName = plugin.getServer().getOfflinePlayer(entry.getKey()).getName();
                if (playerName == null) playerName = "Unknown";
                return new AbstractMap.SimpleEntry<>(playerName, entry.getValue());
            })
            .filter(entry -> !entry.getKey().equals("Unknown"))
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public String formatPlaytime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;
        long totalDays = totalHours / 24;
        
        // Calculate larger time units
        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long weeks = ((totalDays % 365) % 30) / 7;
        long days = ((totalDays % 365) % 30) % 7;
        long hours = totalHours % 24;
        long minutes = totalMinutes % 60;
        
        String primaryColor = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("colors.primary", "&a"));
        String secondaryColor = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("colors.secondary", "&7"));
        
        // Check if we should show detailed time
        boolean showDetailedTime = plugin.getConfig().getBoolean("show-detailed-time", true);
        
        if (!showDetailedTime) {
            // Show total minutes only
            return primaryColor + totalMinutes + secondaryColor + " minutes";
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Years
        if (years > 0) {
            sb.append(primaryColor).append(years).append(secondaryColor)
              .append(years == 1 ? " year" : " years").append(" ");
        }
        
        // Months
        if (months > 0 || (years > 0 && (weeks > 0 || days > 0 || hours > 0))) {
            sb.append(primaryColor).append(months).append(secondaryColor)
              .append(months == 1 ? " month" : " months").append(" ");
        }
        
        // Weeks
        if (weeks > 0 || (years > 0 || months > 0) && (days > 0 || hours > 0)) {
            sb.append(primaryColor).append(weeks).append(secondaryColor)
              .append(weeks == 1 ? " week" : " weeks").append(" ");
        }
        
        // Days
        if (days > 0 || ((years > 0 || months > 0 || weeks > 0) && hours > 0)) {
            sb.append(primaryColor).append(days).append(secondaryColor)
              .append(days == 1 ? " day" : " days").append(" ");
        }
        
        // Hours - show if there are hours or if we have larger units
        if (hours > 0 || (years > 0 || months > 0 || weeks > 0 || days > 0)) {
            sb.append(primaryColor).append(hours).append(secondaryColor)
              .append(hours == 1 ? " hour" : " hours").append(" ");
        }
        
        // Always show minutes
        sb.append(primaryColor).append(minutes).append(secondaryColor)
          .append(minutes == 1 ? " minute" : " minutes");
        
        return sb.toString().trim();
    }

    public long getServerStartTime() {
        return serverStartTime;
    }

    public int getPlayerPosition(UUID uuid) {
        List<Map.Entry<UUID, Long>> sortedPlayers = playtimeData.entrySet()
            .stream()
            .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
            .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getPlayerPosition(Player player) {
        return getPlayerPosition(player.getUniqueId());
    }

    public String getFormattedPlaytimeMessage(Player player, boolean isSelf) {
        StringBuilder message = new StringBuilder();
        
        // Get the base message format
        String format = isSelf ? 
            plugin.getConfig().getString("messages.playtime-self", "&7Your PlayTime:\n&a%time%") :
            plugin.getConfig().getString("messages.playtime-other", "&7%player%'s PlayTime:\n&a%time%");
        
        // Replace placeholders
        format = format.replace("%player%", player.getName())
                      .replace("%time%", formatPlaytime(getPlaytime(player)));
        
        message.append(ChatColor.translateAlternateColorCodes('&', format));
        
        // Add joins count
        String joinsFormat = plugin.getConfig().getString("messages.joins", "&7%joins%x Joined");
        int joins = player.getStatistic(Statistic.LEAVE_GAME) + 1; // +1 for current session
        message.append("\n").append(ChatColor.translateAlternateColorCodes('&', 
            joinsFormat.replace("%joins%", String.valueOf(joins))));
        
        return message.toString();
    }

    public String getFormattedTopList(int limit) {
        StringBuilder message = new StringBuilder();
        
        // Add header
        String header = plugin.getConfig().getString("messages.top-header", "&7Top PlayTime:");
        message.append(ChatColor.translateAlternateColorCodes('&', header)).append("\n");
        
        // Get top players
        List<Map.Entry<String, Long>> topPlayers = getTopPlayers(limit);
        String format = plugin.getConfig().getString("messages.top-format", "&7#%position%. &a%player% &7- &a%time%");
        
        // Add each player
        int position = 1;
        for (Map.Entry<String, Long> entry : topPlayers) {
            String line = format.replace("%position%", String.valueOf(position))
                               .replace("%player%", entry.getKey())
                               .replace("%time%", formatPlaytime(entry.getValue()));
            message.append(ChatColor.translateAlternateColorCodes('&', line)).append("\n");
            position++;
        }
        
        return message.toString().trim();
    }

    public String getReloadMessage(boolean success) {
        String key = success ? "messages.reload-success" : "messages.reload-error";
        String message = plugin.getConfig().getString(key, 
            success ? "&a[PlayTime] &7Configuration reloaded successfully!" :
                     "&c[PlayTime] &7Error reloading configuration!");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
} 