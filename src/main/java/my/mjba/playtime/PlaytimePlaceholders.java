package my.mjba.playtime;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimePlaceholders extends PlaceholderExpansion {
    private final PlaytimeManager playtimeManager;

    public PlaytimePlaceholders(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playtime";
    }

    @Override
    public @NotNull String getAuthor() {
        return "iaMJ";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // Basic player info
        if (identifier.equals("player")) {
            return player.getName();
        }

        // Time formats
        long totalPlaytime = playtimeManager.getPlaytime(player);
        
        switch (identifier) {
            case "time":
                return playtimeManager.formatPlaytime(totalPlaytime);
            case "time_seconds":
                return String.valueOf(totalPlaytime / 1000);
            case "time_minutes":
                return String.valueOf(totalPlaytime / (1000 * 60));
            case "time_hours":
                return String.valueOf(totalPlaytime / (1000 * 60 * 60));
            case "time_days":
                return String.valueOf(totalPlaytime / (1000 * 60 * 60 * 24));
            case "time_weeks":
                return String.valueOf(totalPlaytime / (1000 * 60 * 60 * 24 * 7));
            case "timesjoined":
                return String.valueOf(player.getStatistic(org.bukkit.Statistic.LEAVE_GAME) + 1);
            case "serveruptime":
                return playtimeManager.formatPlaytime(System.currentTimeMillis() - playtimeManager.getServerStartTime());
            case "position":
                return String.valueOf(playtimeManager.getPlayerPosition(player));
        }

        // Top player placeholders
        if (identifier.startsWith("top_")) {
            String[] parts = identifier.split("_");
            if (parts.length == 3) {
                try {
                    int position = Integer.parseInt(parts[1]);
                    if (position < 1 || position > 10) {
                        return "";
                    }
                    
                    var topPlayers = playtimeManager.getTopPlayers(10);
                    if (position > topPlayers.size()) {
                        return "";
                    }
                    
                    var entry = topPlayers.get(position - 1);
                    if (parts[2].equals("name")) {
                        return entry.getKey();
                    } else if (parts[2].equals("time")) {
                        return playtimeManager.formatPlaytime(entry.getValue());
                    }
                } catch (NumberFormatException e) {
                    return "";
                }
            }
        }

        return null;
    }
} 