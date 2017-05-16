package me.jeremy.ifp;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class listener implements Listener{
	static main plugin;
	public listener(main main) {
		plugin = main;
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		if (false) { 
			database.attemptAddorUpdate(e.getPlayer());	
		} else {
			plugin.getPlayers().set("Players." + e.getPlayer().getUniqueId().toString() + ".Name", e.getPlayer().getName());
			plugin.saveConfig();
			plugin.getPlayers().set("Players." + e.getPlayer().getUniqueId().toString() + ".Enabled", true);
			plugin.saveConfig();
			plugin.getPlayers().set("Players." + e.getPlayer().getUniqueId().toString() + ".Items", new ArrayList<String>());
			plugin.saveConfig();
			plugin.reloadPlayers();
			
		}
		
	}
	
	@EventHandler
    public static void PickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        	if (checkPublicFilter(p, e.getItem().getItemStack())) {
        		e.setCancelled(true);
        		return;
        	}
        	if (checkPrivateFiler(p, e.getItem().getItemStack())) {
        		e.setCancelled(true);
        		return;
        	}  
    }

	@SuppressWarnings("unused")
	public static boolean checkPrivateFiler(Player p, ItemStack i) {
		if (!false) { //Database: Enabled: false
			if (p.hasPermission("itemfilterpickup.user") || p.hasPermission("itemfilterpickup.user.canfilter")) {
				if (plugin.getPlayers().getBoolean("Players." + p.getUniqueId().toString() + ".Enabled")) {
					int b = 1;
					int max = getMaxFilter(p);
					for (String filter : plugin.getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items")) {
						if (max == -1) {
							ItemStack itemA = new ItemStack(Material.matchMaterial(filter.split(":")[0]), i.getAmount(), (byte) Integer.parseInt(filter.split(":")[1]));
							if (i.equals(itemA)) {
								return true;
							}
							b++;
						} else if (b <= max && max != -1) { 
							ItemStack itemA = new ItemStack(Material.matchMaterial(filter.split(":")[0]), i.getAmount(), (byte) Integer.parseInt(filter.split(":")[1]));
							if (i.equals(itemA)) {
								return true;
							}
							b++;
						} else {
							break;
						}
					}
					return false;
				} else {
					return false;
				}
			} else{
				return false;
			}
		} else {
			return database.checkDatabase(p, i);
		}
	}

	private static boolean checkPublicFilter(Player p, ItemStack i) {
		if (!p.hasPermission("itemfilterpickup.user.bypass.public") || !p.hasPermission("itemfilterpickup.admin")) {
			if (plugin.getConfig().getBoolean("Public Pickup Filter.Enabled")) {
				for (String filter : plugin.getConfig().getStringList("Public Pickup Filter.Items")) {
					ItemStack itemA = new ItemStack(Material.matchMaterial(filter.split(":")[0]), i.getAmount(), (byte) Integer.parseInt(filter.split(":")[1]));
					if (i.equals(itemA)) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		} else{
			return false;
		}
	}
	
	public static int getMaxFilter(Player p) {
		int curMax = 0;
		for (String perm : plugin.getConfig().getStringList("Permission/Filter Amount")) {
			if (p.hasPermission("itemfilterpickup.user.max." + perm.split(":")[0])) {
				if (curMax < Integer.parseInt(perm.split(":")[1])) {
					curMax = Integer.parseInt(perm.split(":")[1]);
				} else if (Integer.parseInt(perm.split(":")[1]) == -1) {
					return -1;
				}
			}
		}
		return curMax;
	}
}
