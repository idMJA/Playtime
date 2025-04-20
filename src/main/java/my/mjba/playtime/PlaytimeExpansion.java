package my.mjba.playtime;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.concurrent.TimeUnit;

public class PlaytimeExpansion extends PlaceholderExpansion {
    private final PlaytimeManager playtimeManager;

    public PlaytimeExpansion(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @Override
    public String getIdentifier() {
        return "playtime";
    }

    @Override
    public String getAuthor() {
        return "MJBA";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "";
        }

        // Convert to online player if possible
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null && !params.equals("server_uptime")) {
            return "";
        }

        switch (params.toLowerCase()) {
            case "time":
                return String.valueOf(playtimeManager.getPlaytime(player.getUniqueId()));
            case "formatted_time":
                long playtime = playtimeManager.getPlaytime(player.getUniqueId());
                return formatTime(playtime);
            case "position":
                return String.valueOf(playtimeManager.getPlayerPosition(player.getUniqueId()));
            case "server_uptime":
                return formatTime(System.currentTimeMillis() - playtimeManager.getServerStartTime());
            default:
                return null;
        }
    }

    private String formatTime(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
} 