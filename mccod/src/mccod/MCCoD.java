package mccod;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MCCoD extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(this, this);
        System.out.println("blabla has been enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("blabla has been disabled!");
    }
    HashMap<String, Integer> redTeam = new HashMap<String, Integer>();
    HashMap<String, Integer> blueTeam = new HashMap<String, Integer>();
    HashMap<String, Integer> banOnDeath = new HashMap<String, Integer>();

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (redTeam.size() == blueTeam.size()) {
            redTeam.put(p.getName(), null);
            p.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "TeamRed" + ChatColor.DARK_GRAY + "] " + p.getName());
            p.sendMessage(ChatColor.GRAY + "You are in team " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "!");
        } else if (redTeam.size() < blueTeam.size()) {
            redTeam.put(p.getName(), null);
            p.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "TeamRed" + ChatColor.DARK_GRAY + "] " + p.getName());
            p.sendMessage(ChatColor.GRAY + "You are in team " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "!");
        } else {
            blueTeam.put(p.getName(), null);
            p.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "TeamBlue" + ChatColor.DARK_GRAY + "] " + p.getName());
            p.sendMessage(ChatColor.GRAY + "You are in team " + ChatColor.BLUE + "blue" + ChatColor.DARK_GRAY + "!");
        }

        e.getPlayer().getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD, 1));
        e.getPlayer().getInventory().addItem(new ItemStack(Material.BOW, 1));
        e.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.setLevel(0);

        if (p.getKiller() instanceof Player) {
            Player killer = p.getKiller();

            this.getServer().broadcastMessage(killer.getDisplayName() + ChatColor.UNDERLINE + " killed " + ChatColor.RESET + p.getDisplayName() + "!");
            p.getInventory().clear();

            if (redTeam.containsKey(p.getName())) {
                redTeam.remove(p.getName());
            }
            if (blueTeam.containsKey(p.getName())) {
                blueTeam.remove(p.getName());
            }

            p.kickPlayer(ChatColor.AQUA + "You got killed by " + ChatColor.GOLD + killer.getName() + ChatColor.AQUA + "!");
            banOnDeath.put(p.getName(), null);
            killer.setLevel(killer.getLevel() + 1);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (redTeam.containsKey(p.getName())) {
            redTeam.remove(p.getName());
        }
        if (blueTeam.containsKey(p.getName())) {
            blueTeam.remove(p.getName());
        }

        banOnDeath.put(p.getName(), null);
        p.getInventory().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preLogin2(AsyncPlayerPreLoginEvent e) {
        if (banOnDeath.containsKey(e.getName())) {
            e.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + "There's a game running, wait until the next round!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preLogin1(PlayerPreLoginEvent e) {
        if (banOnDeath.containsKey(e.getName())) {
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "There's a game running, wait until the next round!");
        }
    }

    @EventHandler
    public void xpChange(PlayerExpChangeEvent e) {
        e.setAmount(0);
    }
}
