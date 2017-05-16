package me.jeremy.ifp;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin {
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(new listener(this), this);
		//Bukkit.getServer().getPluginManager().registerEvents(new database(this), this);
		loadConfiguration();
		if (false) {
	    	//database.connect();
	    }
		System.out.println("[ItemFilterPickup] Plugin is fully loaded and ready to go! Good luck!");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void loadConfiguration(){
		getConfig().options().copyDefaults(true);
	    saveConfig();
	    reloadConfig();
	    getPlayers().options().copyDefaults(true);
	    savePlayers();
	    reloadPlayers();
	}	
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (cmdLabel.equalsIgnoreCase("ifp") || cmdLabel.equalsIgnoreCase("itemfilter")) {
				if (args.length == 0) {
					help(p); 
					return true;
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("toggle")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { toggleStatus(p, false); return true;}}
					if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("+")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { addItem(p, false); return true;}}
					if (args[0].equalsIgnoreCase("gui")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { guiOpen(p, "main", false); return true;}}
					if (args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { removeItem(p, false); return true;}}
					if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {if (checkPermsMsg(p, "itemfilterpickup.user")){ clearList(p, false); return true;}}
					if (args[0].equalsIgnoreCase("list")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { viewList(p, 1, false); return true;}}
					if (args[0].equalsIgnoreCase("help")) { help(p); return true;}
				} else if (args.length == 2) { 
					try {
						if (args[0].equalsIgnoreCase("list")) {if (p.hasPermission("itemfilterpickup.user") & checkPermsMsg(p, "itemfilterpickup.user")) { viewList(p, Integer.parseInt(args[1]), false); return true; }}
					} catch (NumberFormatException e) {
						pSend(p, getConfig().getString("Messages.failed-command").replace("%SYNTAX%", "/ifp list {#}").replace("%NL%", "\n"));
						return true;
					}
					
				}
			}
			if (cmdLabel.equalsIgnoreCase("ifpa") || cmdLabel.equalsIgnoreCase("itemfilteradmin")) {
				if (args.length == 0) {
					help(p); 
					return true;
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.admin.reload")) { reloadConfig(); reloadPlayers(); pSend(p, getConfig().getString("Messages.reload")); return true;}}
					if (args[0].equalsIgnoreCase("toggle")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.admin.toggle")) { toggleStatus(p, true); return true;}}
					if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("+")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.edit")) { addItem(p, true); return true;}}
					if (args[0].equalsIgnoreCase("gui")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { guiOpen(p, "main", true); return true;}}
					if (args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.admin.edit")) { removeItem(p, true); return true;}}
					if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.admin.edit")){ clearList(p, true); return true;}}
					if (args[0].equalsIgnoreCase("list")) {if (checkPermsMsg(p, "itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.admin.edit") || checkPermsMsg(p, "itemfilterpickup.public.view")) { viewList(p, 1, true); return true;}}
					if (args[0].equalsIgnoreCase("help")) { help(p); return true;}
				} else if (args.length == 2) { 
					try {
						if (args[0].equalsIgnoreCase("list")) {if (p.hasPermission("itemfilterpickup.admin") || checkPermsMsg(p, "itemfilterpickup.public.view") || checkPermsMsg(p, "itemfilterpickup.admin.edit")) { viewList(p, Integer.parseInt(args[1]), true); return true; }}
					} catch (NumberFormatException e) {
						pSend(p, getConfig().getString("Messages.failed-command").replace("%SYNTAX%", "/ifpa list {#}").replace("%NL%", "\n"));
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	public String ct(String text) {
		String finalText = text.replace("&0", ChatColor.BLACK + "").replace("&1", ChatColor.DARK_BLUE + "").replace("&2", ChatColor.DARK_GREEN + "").replace("&3", ChatColor.DARK_AQUA + "").replace("&4", ChatColor.DARK_RED + "").replace("&5", ChatColor.DARK_PURPLE + "").replace("&6", ChatColor.GOLD + "").replace("&7", ChatColor.GRAY + "").replace("&8", ChatColor.DARK_GRAY + "").replace("&9", ChatColor.BLUE + "").replace("&a", ChatColor.GREEN + "").replace("&b", ChatColor.AQUA + "").replace("&c", ChatColor.RED + "").replace("&d", ChatColor.LIGHT_PURPLE + "").replace("&e", ChatColor.YELLOW + "").replace("&f", ChatColor.WHITE + "").replace("&l", ChatColor.BOLD + "").replace("&m", ChatColor.STRIKETHROUGH + "").replace("&n", ChatColor.UNDERLINE + "").replace("&o", ChatColor.ITALIC + "").replace("&r", ChatColor.RESET + "");
		return finalText;
	}
	
	public  ItemStack createItem(String it, int amount, int data) {
		ItemStack item = new ItemStack(Material.matchMaterial(it), amount, (byte) data);
		return item;
	}
	public  ItemStack addName(ItemStack it, String name) {
		if (!name.equalsIgnoreCase("none")) {
			ItemMeta m = it.getItemMeta();
	        m.setDisplayName(ct(name));
	        it.setItemMeta(m);
			return it;
		} else {
			return it;
		}
		
	}
	public  ItemStack addEnchant(ItemStack it, String enchantList) {
		if (!enchantList.equalsIgnoreCase("none")) {
			String[] e = enchantList.split(",");
			for (String preEnchant : e) {
				String[] enchant = preEnchant.split("@");
				it.addUnsafeEnchantment(Enchantment.getByName(enchant[0]), Integer.parseInt(enchant[1]));
			}
			return it;
		} else {
			return it;
		}
		
	}
	public  ItemStack addLore(ItemStack it, String loreList) {
		if (!loreList.equalsIgnoreCase("none")) {
			ItemMeta m = it.getItemMeta();
			List<String> lores = new ArrayList<String>();
			String[] preLores = loreList.split(",");
			for (String lore : preLores) { lores.add(ct(lore)); }
	        m.setLore(lores);
	        it.setItemMeta(m);
			return it;
		} else {
			return it;
		}
	}
	
	public  ItemStack getItem(String item) {
		String[] i = item.split(":"); // added
	    Integer amount = Integer.parseInt(i[2]); //added
	    String enchant = i[3];
	    String name = i[4];
	    String lore = i[5];
	    ItemStack newItem = createItem(i[0], amount, Integer.parseInt(i[1]));
		if (!enchant.equalsIgnoreCase("none")) {
			newItem = addEnchant(newItem, enchant);
		}
		if (!name.equalsIgnoreCase("none")) {
			newItem = addName(newItem, name);
		}
		if (!lore.equalsIgnoreCase("none")) {
			newItem = addLore(newItem, lore);
		}
		
		return newItem;
		
	}
	
	public ItemStack addGlow(ItemStack it) {
		ItemMeta im = it.getItemMeta();
		enchant_glow glow = new enchant_glow(179);
		im.addEnchant(glow, 1, true);
		it.setItemMeta(im);
		return it;
		
	}
	
	public void guiOpen(Player p, String menu, Boolean admin) {
		switch (menu) {
		case "main":
			p.openInventory(mainGUIMenu(p, admin));
			break;
		}
			
		
	}
	
	public Inventory mainGUIMenu(Player p, Boolean admin) {
		if (admin) {
			Inventory myInv = Bukkit.createInventory(null, 9, ct("&cAdmin &7Main Menu"));
			myInv.setItem(2, getItem("LAVA_BUCKET:0:1:none:&cR&8emove &cM&8enu:none"));
			myInv.setItem(4, getItem("WATER_BUCKET:0:1:none:&aA&8dd &aM&8enu:none"));
			myInv.setItem(6, getItem("BUCKET:0:1:none:&9V&8iew &9M&8enu:none"));
			return myInv;
		} else {
			Inventory myInv = Bukkit.createInventory(null, 9, ct("&9Player &7Main Menu"));
			myInv.setItem(2, getItem("LAVA_BUCKET:0:1:none:&aR&8emove &aM&8enu:none"));
			myInv.setItem(4, getItem("WATER_BUCKET:0:1:none:&aA&8dd &aM&8enu:none"));
			myInv.setItem(6, getItem("BUCKET:0:1:none:&aV&8iew &aM&8enu:none"));
			return myInv;
		}
		
	}
	
	public Inventory addremGUIMenu(Player p, int page, Boolean admin) {

		
	}
	
	public Inventory viewGUIMenu(Player p, int page, Boolean admin) {
		int invI = 0;
		List<String> items = new ArrayList<String>();
		if (admin) {
			items = getConfig().getStringList("Public Pickup Filter.Items");
		} else {
			items = getPlayers().getStringList("Players."+p.getUniqueId().toString()+".Items");
		}
		double listLength = items.size();
		double length = 32;
		double pages = Math.ceil((double)listLength/(double)length);
		int i = 0;
		Inventory myInv = Bukkit.createInventory(null, 36, ct("&cAdmin &7List &r(" + page + "/" + (pages + "").replace(".0", "") + ")"));
		if (admin) {
			myInv = Bukkit.createInventory(null, 36, ct("&cAdmin &7List &r(" + page + "/" + (pages + "").replace(".0", "") + ")"));	
		} else {
			myInv = Bukkit.createInventory(null, 36, ct("&9Player &7List &r(" + page + "/" + (pages + "").replace(".0", "") + ")"));
		}
		for (String item : items) {
			if (i < (length * page)) {
				if (i >= length * (page - 1)) {
					int max = listener.getMaxFilter(p);
					if (max >= i+1 || max == -1) {
						myInv.setItem(invI, enchant_glow.addGlow(getItem(item + ":1:none:none:none")));
					} else {
						myInv.setItem(invI, getItem(item + ":1:none:none:none"));
					}
					
					System.out.println(invI + " " + item);
					invI++;
					i++;
					continue;
				}
			}
			i++;
		}
		if (page > 1) {
			myInv.setItem(33, getItem("STAINED_GLASS:14:1:none:&cP&8age &cB&8ack:none"));
		}
		if (listLength >= 32 && !(invI < 32)) {
			myInv.setItem(34, getItem("STAINED_GLASS:5:1:none:&aP&8age &aF&8orward:none"));
		}
		
		myInv.setItem(35, getItem("CHEST:0:1:none:&9M&8ain &9M&8enu:none"));
		return myInv;
	}
	
	private void help(Player p) {
		String sendMessage = "&7----- &6Item Filter Help &7-----\n";
		
		if (p.hasPermission("itemfilterpickup.user")) {
			sendMessage = 
							"&6/ifp {add/+} &7- Adds the item you are holding to your filter list.\n" + 
							"&6/ifp {rem/remove/del/delete/-}&7 - Removes the item you are holding from your filter list.\n" + 
							"&6/ifp clear&7 - Removes all items from your filter list\n" + 
							"&6/ifp toggle&7 - Enable/Disables the ability to pickup items in your filter list.\n" + 
							"&6/ifp list &7- Allows you to view the first page of your pickup filter list\n" + 
							"&6/ifp list {page#} &7- Allows you to chose a page of the pickup filter list to view\n" + 
							"&6/ifp help &7- This gives you more information about commands you can use\n";
			
		}
		if (p.hasPermission("itemfilterpickup.admin")){
			sendMessage = 
							"&6/ifpa {rem/remove/del/delete/-} &7- Removes the item you are holding from the public filter list.\n" + 
							"&6/ifpa clear &7- Removes all items from the public filter list\n" + 
							"&6/ifpa toggle &7- Enable/Disables the ability to pickup items in the public filter list.\n" +
							"&6/ifpa bypass &7- Enable/Disables the ability to pickup items in the public filter list for your self only.\n" +
							"&6/ifpa list &7- Allows you to view the first page of the public pickup filter list\n" + 
							"&6/ifpa list {page#} &7- Allows you to chose a page of the public pickup filter list to view\n" + 
							"&6/ifp reload &7- This will reload the Config and Player files\n";
		}
		if (!p.hasPermission("itemfilterpickup.admin") && p.hasPermission("itemfilterpickup.admin.edit")){
			sendMessage = 
							"&6/ifpa {rem/remove/del/delete/-} &7- Removes the item you are holding from the public filter list.\n" + 
							"&6/ifpa clear &7- Removes all items from the public filter list\n" + 
							"&6/ifpa list &7- Allows you to view the first page of the public pickup filter list\n" + 
							"&6/ifpa list {page#} &7- Allows you to chose a page of the public pickup filter list to view\n";
		}
		if (!p.hasPermission("itemfilterpickup.admin") && p.hasPermission("itemfilterpickup.public.bypass")){
			sendMessage = 
							"&6/ifpa bypass &7- Enable/Disables the ability to pickup items in the public filter list for your self only.\n";
		}
		if (!p.hasPermission("itemfilterpickup.admin") && p.hasPermission("itemfilterpickup.public.view")){
			sendMessage = 
							"&6/ifpa list &7- Allows you to view the first page of the public pickup filter list\n" + 
							"&6/ifpa list {page#} &7- Allows you to chose a page of the public pickup filter list to view\n";
		}
		if (!p.hasPermission("itemfilterpickup.admin") && p.hasPermission("itemfilterpickup.admin.reload")){
			sendMessage = 
							"&6/ifp reload &7- This will reload the Config and Player files\n";
		}
		if (!p.hasPermission("itemfilterpickup.admin") && p.hasPermission("itemfilterpickup.admin.toggle")){
			sendMessage = 
							"&6/ifp toggle&7 - Enable/Disables the ability to pickup items in your filter list.\n"; 
		}
		
		pSend(p, sendMessage);
	}

	private boolean checkPermsMsg(Player p, String string) {
		boolean state = false;
		for (String perms : string.split(",")) {
			if (p.hasPermission(perms)) {
				state = true;
			}
		}
		if (!state) {
			pSend(p, getConfig().getString("Messages.no-permission"));
			return false;
		}
		return true;
	}

	private void clearList(Player p, Boolean admin) {
		if (admin) {
			getConfig().set("Public Pickup Filter.Items", "");
			saveConfig();
			pSend(p, getConfig().getString("Messages.public-clear-filter"));
		} else {
			getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", new ArrayList<String>());
			savePlayers();
			pSend(p, getConfig().getString("Messages.clear-filter"));
		}		
	}

	@SuppressWarnings("unused")
	private void viewList(Player p, int page, Boolean admin) {
		int max = listener.getMaxFilter(p);
		List<String> filterList = new ArrayList<String>();
		if (false) {
			//filterList = database.getFilterList(p);
		} else {
			if (!admin) {
				filterList = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
			} else {
				filterList = getConfig().getStringList("Public Pickup Filter.Items");
			}
		}
		
		String filter = "";
		double listLength = filterList.size();
		double length = getConfig().getDouble("Settings.Page Length") + 1;
		double pages = Math.ceil((double)listLength/(double)length);
		int i = 1;
		for (String item : filterList) {
			item = item.split(":")[0];
			if (i < (length * page)) {
				if (i >= length * (page - 1)) {
					if (filter.equalsIgnoreCase("")) {
						if (!admin) {
							if (max >= i || max == -1) {
								filter = getConfig().getString("Messages.filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&a" + item);	
							} else {
								filter = getConfig().getString("Messages.filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&c" + item);
							}
						} else {
							filter = getConfig().getString("Messages.public-filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&a" + item);	
						}
						
					} else {
						if (!item.equalsIgnoreCase("")) {
							if (!admin) {
								if (max >= i || max == -1) {
									filter = filter + "\n" + getConfig().getString("Messages.filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&a" + item);
								} else {
									filter = filter + "\n" + getConfig().getString("Messages.filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&c" + item);
								}
							} else {
								filter = filter + "\n" + getConfig().getString("Messages.public-filter-item-layout").replace("%#%", i + "").replace("%ITEM%", "&a" + item);
							}
							
							
						}
					}
				}
			} else { break; }
			i++;
		}	
		if (!admin) {
			pSend(p, getConfig().getString("Messages.filter-layout").replace("%ITEMS%", filter).replace("%CUR_PAGE%", (page + "").replace(".0", "")).replace("%ALL_PAGE%", (pages + "").replace(".0", "")).replace("%LIST_SIZE%", (length + "").replace(".0", "")).replace("%ALL_ITEM_COUNT%", (listLength + "").replace(".0", "")).replace("%NL%", "\n"));
		} else {
			pSend(p, getConfig().getString("Messages.public-filter-layout").replace("%ITEMS%", filter).replace("%CUR_PAGE%", (page + "").replace(".0", "")).replace("%ALL_PAGE%", (pages + "").replace(".0", "")).replace("%LIST_SIZE%", (length + "").replace(".0", "")).replace("%ALL_ITEM_COUNT%", (listLength + "").replace(".0", "")).replace("%NL%", "\n"));
		}
		
	}

	@SuppressWarnings("unused")
	private void removeItem(Player p, Boolean admin) {
		if (false) {
			//if (database.removeItem(p, p.getItemInHand().getType().toString() + ":" + p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0])) {
			//	pSend(p, getConfig().getString("Messages.remove-success-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
			//} else {
			//	pSend(p, getConfig().getString("Messages.remove-fail-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
			//}
		} else {
			List<String> removeItems = new ArrayList<String>();
			if (admin) {
				removeItems = getConfig().getStringList("Public Pickup Filter.Items");
			} else {
				removeItems = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
			}
				
			
					if (removeItems.remove(p.getItemInHand().getType().toString() + ":" + p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0])) {
						if (admin) {
							getConfig().set("Public Pickup Filter.Items", removeItems);
							saveConfig();
							reloadConfig();
							pSend(p, getConfig().getString("Messages.public-remove-success-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						} else {
							getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", removeItems);
							savePlayers();
							reloadPlayers();
							pSend(p, getConfig().getString("Messages.remove-success-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						}
						
						
					} else {
						if (admin) {
							pSend(p, getConfig().getString("Messages.public-remove-fail-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						} else {
							pSend(p, getConfig().getString("Messages.remove-fail-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						}
						
					}
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void addItem(Player p, Boolean admin) {
			if (false) {
				String item = p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0];
				/*if (database.addToFilter(p, p.getItemInHand().getType().toString() + ":" + item)) {
					pSend(p, getConfig().getString("Messages.add-to-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
				} else {
					pSend(p, getConfig().getString("Messages.already-added-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
				}*/
				
			} else { 
				List<String> items = new ArrayList<String>();
				if (admin) {
					items = getConfig().getStringList("Public Pickup Filter.Items");
				} else {
					items = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
				}
				 
				String item = p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0];
				if (items.contains(p.getItemInHand().getType().toString() + ":" + item)) {
					if (admin) {
						pSend(p, getConfig().getString("Messages.public-already-added-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						return;
					} else {
						pSend(p, getConfig().getString("Messages.already-added-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
						return;
					}
					
				}
				if (admin) {
					items.add(p.getItemInHand().getType().toString() + ":" + item);
					getConfig().set("Public Pickup Filter.Items", items);
					saveConfig();
					reloadConfig();
					pSend(p, getConfig().getString("Messages.public-add-to-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
				} else {
					items.add(p.getItemInHand().getType().toString() + ":" + item);
					getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", items);
					savePlayers();
					reloadPlayers();
					pSend(p, getConfig().getString("Messages.add-to-filter").replace("%ITEM%", p.getItemInHand().getType().toString()));
				}
				
			}
	}

	@SuppressWarnings("unused")
	public void toggleStatus(Player p, Boolean admin) {
		if (false) {
			/*if (!database.getState(p)) {
				database.setState(p, 1);
				pSend(p, getConfig().getString("Messages.toggle-filter").replace("%STATE%", "&aON&r").replace("%STATE_REVERSE%", "&cOFF&r"));
			} else {
				database.setState(p, 0);
				pSend(p, getConfig().getString("Messages.toggle-filter").replace("%STATE%", "&cOFF&r").replace("%STATE_REVERSE%", "&aON&r"));
			}*/
			
		} else {
			if (admin) {
				if (!getConfig().getBoolean("Public Pickup Filter.Enabled")) {
					getConfig().set("Public Pickup Filter.Enabled", true);
					saveConfig();
					reloadConfig();
					pSend(p, getConfig().getString("Messages.public-toggle-filter").replace("%STATE%", "&aON&r").replace("%STATE_REVERSE%", "&cOFF&r"));
				} else {
					getConfig().set("Public Pickup Filter.Enabled", false);
					saveConfig();
					reloadConfig();
					pSend(p, getConfig().getString("Messages.public-toggle-filter").replace("%STATE%", "&cOFF&r").replace("%STATE_REVERSE%", "&aON&r"));
				}
			} else {
				if (!getPlayers().getBoolean("Players." + p.getUniqueId().toString() + ".Enabled")) {
					getPlayers().set("Players." + p.getUniqueId().toString() + ".Enabled", true);
					savePlayers();
					reloadPlayers();
					pSend(p, getConfig().getString("Messages.toggle-filter").replace("%STATE%", "&aON&r").replace("%STATE_REVERSE%", "&cOFF&r"));
				} else {
					getPlayers().set("Players." + p.getUniqueId().toString() + ".Enabled", false);
					savePlayers();
					reloadPlayers();
					pSend(p, getConfig().getString("Messages.toggle-filter").replace("%STATE%", "&cOFF&r").replace("%STATE_REVERSE%", "&aON&r"));
				}
			}
			
			
		}
	}
	
	public void pSend(Player p, String msg) {
		 p.sendMessage(colorChatb(msg));
	 }
	
	public String colorChatb(String message) {
		 return message.replace("&0", ChatColor.BLACK + "").replace("&1", ChatColor.DARK_BLUE + "").replace("&2", ChatColor.DARK_GREEN + "").replace("&3", ChatColor.DARK_AQUA + "").replace("&4", ChatColor.DARK_RED + "").replace("&5", ChatColor.DARK_PURPLE + "").replace("&6", ChatColor.GOLD + "").replace("&7", ChatColor.GRAY + "").replace("&8", ChatColor.DARK_GRAY + "").replace("&9", ChatColor.BLUE + "").replace("&a", ChatColor.GREEN + "").replace("&b", ChatColor.AQUA + "").replace("&c", ChatColor.RED + "").replace("&d", ChatColor.LIGHT_PURPLE + "").replace("&e", ChatColor.YELLOW + "").replace("&f", ChatColor.WHITE + "").replace("&l", ChatColor.BOLD + "").replace("&m", ChatColor.STRIKETHROUGH + "").replace("&n", ChatColor.UNDERLINE + "").replace("&o", ChatColor.ITALIC + "").replace("&r", ChatColor.RESET + "");
	 }
	
	 // Added the ability to have a custom chats config! - v0.8.2 - 10/3/2016
    private FileConfiguration playersConfig = null; //customConfig 
    private File players = null; //customConfigFile
    
    
    public void reloadPlayers() {
        if (players == null) {
        	players = new File(getDataFolder(), "players.yml");
        }
        playersConfig = YamlConfiguration.loadConfiguration(players);
        // Look for defaults in the jar
        Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(this.getResource("players.yml"), "UTF8");
		
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            playersConfig.setDefaults(defConfig);
        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public FileConfiguration getPlayers() {
        if (playersConfig == null) {
            reloadPlayers();
        }
        return playersConfig;
    }
    
    public void savePlayers() {
        if (playersConfig == null || playersConfig == null) {
            return;
        }
        try {
        	getPlayers().save(players);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + players, ex);
        }
    }
    
    public void saveDefaultPlayers() {
        if (players == null) {
        	players = new File(getDataFolder(), "players.yml");
        }
        if (!players.exists()) {            
             this.saveResource("chat.yml", false);
         }
    }
    
    //--- End of chats.yml writing tools
	
}
