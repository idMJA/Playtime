package my.mjba.playtime;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;

public class PlaytimeCommand implements CommandExecutor {
    private final PlaytimeManager playtimeManager;
    private final Playtime plugin;
    private final long serverStartTime;

    public PlaytimeCommand(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
        this.plugin = playtimeManager.getPlugin();
        this.serverStartTime = System.currentTimeMillis();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return true;
            }

            if (!sender.hasPermission("playtime.check")) {
                sender.sendMessage("§cYou don't have permission to check your playtime!");
                return true;
            }

            Player player = (Player) sender;
            long playtime = playtimeManager.getPlaytime(player);
            sender.sendMessage("§aYour playtime: §f" + playtimeManager.formatPlaytime(playtime));
            return true;
        }

        if (args[0].equalsIgnoreCase("top")) {
            if (!sender.hasPermission("playtime.checktop")) {
                sender.sendMessage("§cYou don't have permission to check top players!");
                return true;
            }

            List<Map.Entry<String, Long>> topPlayers = playtimeManager.getTopPlayers(10);
            
            sender.sendMessage("§6§l=== Top 10 Players by Playtime ===");

            for (int i = 0; i < topPlayers.size(); i++) {
                Map.Entry<String, Long> entry = topPlayers.get(i);
                sender.sendMessage("§e" + (i + 1) + ". §f" + entry.getKey() + " §7- §f" + 
                    playtimeManager.formatPlaytime(entry.getValue()));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("uptime")) {
            if (!sender.hasPermission("playtime.uptime")) {
                sender.sendMessage("§cYou don't have permission to check server uptime!");
                return true;
            }

            long uptime = System.currentTimeMillis() - serverStartTime;
            sender.sendMessage("§aServer uptime: §f" + playtimeManager.formatPlaytime(uptime));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("playtime.reload")) {
                sender.sendMessage("§cYou don't have permission to reload the config!");
                return true;
            }

            plugin.reloadConfig();
            sender.sendMessage("§aConfiguration reloaded successfully!");
            return true;
        }

        // Check other player's playtime
        if (!sender.hasPermission("playtime.checkothers")) {
            sender.sendMessage("§cYou don't have permission to check other players' playtime!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: §f" + args[0]);
            return true;
        }

        long playtime = playtimeManager.getPlaytime(target);
        sender.sendMessage("§a" + target.getName() + "'s playtime: §f" + 
            playtimeManager.formatPlaytime(playtime));
        return true;
    }
} 