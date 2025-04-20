package my.mjba.playtime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeListener implements Listener {
    private final PlaytimeManager playtimeManager;

    public PlaytimeListener(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playtimeManager.startTracking(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playtimeManager.stopTracking(event.getPlayer());
    }
} 