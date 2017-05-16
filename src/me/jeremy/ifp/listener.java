package me.jeremy.ifp;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
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
			//database.attemptAddorUpdate(e.getPlayer());	
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
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			Player p = (Player) event.getWhoClicked();
			ItemStack clicked = event.getCurrentItem();
			Inventory inventory = event.getInventory();
			if (inventory.getName().equals(plugin.ct("&cAdmin &7Main Menu"))) {
				event.setCancelled(true);
				if (clicked.getType().toString().equals("LAVA_BUCKET")) {
					 p.closeInventory();
					 
				 }
				if (clicked.getType().toString().equals("WATER_BUCKET")) {
					 p.openInventory(plugin.addGUIMenu(p, true));
				 }
				if (clicked.getType().toString().equals("BUCKET")) {
					p.openInventory(plugin.viewGUIMenu(p, 1, true));
				 }
			}
			
			if (inventory.getName().equals(plugin.ct("&9Player &7Main Menu"))) {
				event.setCancelled(true);
				if (clicked.getType().toString().equals("LAVA_BUCKET")) {
					 p.closeInventory();
				 }
				if (clicked.getType().toString().equals("WATER_BUCKET")) {
					 p.openInventory(plugin.addGUIMenu(p, false));
				 }
				if (clicked.getType().toString().equals("BUCKET")) {
					p.openInventory(plugin.viewGUIMenu(p, 1, false));

				 } 
			}	
			
			if (inventory.getName().contains(plugin.ct("&cAdmin &7List"))) {
				event.setCancelled(true);
				if (clicked.getType().toString().equals("STAINED_GLASS")) {
					if (clicked.getData().getData() == (byte) 14) {
						int pg = Integer.parseInt(inventory.getName().split("\\(")[1].split("\\/")[0]);
						p.openInventory(plugin.viewGUIMenu(p, pg - 1, true));
					}
					if (clicked.getData().getData() == (byte) 5) {
						int pg = Integer.parseInt(inventory.getName().split("\\(")[1].split("\\/")[0]);
						p.openInventory(plugin.viewGUIMenu(p, pg + 1, true));
					}
				} else if (clicked.getType().toString().equals("CHEST") && clicked.getItemMeta().getDisplayName().equals(plugin.ct("&9M&8ain &9M&8enu"))) { 
					p.openInventory(plugin.mainGUIMenu(p, true));
				}
			}
			
			if (inventory.getName().contains(plugin.ct("&9Player &7List"))) {
				event.setCancelled(true);
				if (clicked.getType().toString().equals("STAINED_GLASS")) {
					if (clicked.getData().getData() == (byte) 14) {
						int pg = Integer.parseInt(inventory.getName().split("\\(")[1].split("\\/")[0]);
						p.openInventory(plugin.viewGUIMenu(p, pg - 1, false));
					}
					if (clicked.getData().getData() == (byte) 5) {
						int pg = Integer.parseInt(inventory.getName().split("\\(")[1].split("\\/")[0]);
						p.openInventory(plugin.viewGUIMenu(p, pg + 1, false));
					}
				} else if (clicked.getType().toString().equals("CHEST") && clicked.getItemMeta().getDisplayName().equals(plugin.ct("&9M&8ain &9M&8enu"))) { 
					p.openInventory(plugin.mainGUIMenu(p, false));
				}
			}
			
			
			
		} catch (NullPointerException ex) {
			
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
			return false;
			//return database.checkDatabase(p, i);
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
