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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin{

	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(new listener(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new database(this), this);
		loadConfiguration();
		if (false) {
	    	database.connect();
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
					if (args[0].equalsIgnoreCase("reload")) {if (checkPermsMsg(p, "itemfilterpickup.admin")) { reloadConfig(); reloadPlayers(); pSend(p, getConfig().getString("Messages.reload")); return true;}}
					if (args[0].equalsIgnoreCase("toggle")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { toggleStatus(p); return true;}}
					if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("+")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { addItem(p); return true;}}
					if (args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { removeItem(p); return true;}}
					if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {if (checkPermsMsg(p, "itemfilterpickup.user")){ clearList(p); return true;}}
					if (args[0].equalsIgnoreCase("list")) {if (checkPermsMsg(p, "itemfilterpickup.user")) { viewList(p, 1); return true;}}
					if (args[0].equalsIgnoreCase("help")) { help(p); return true;}
				} else if (args.length == 2) { 
					try {
						if (args[0].equalsIgnoreCase("list")) {if (p.hasPermission("itemfilterpickup.user") & checkPermsMsg(p, "itemfilterpickup.user")) { viewList(p, Integer.parseInt(args[1])); return true; }}
					} catch (NumberFormatException e) {
						pSend(p, getConfig().getString("Messages.failed-command").replaceAll("%SYNTAX%", "/ifp list {#}").replaceAll("%NL%", "\n"));
						return true;
					}
					
				}
			}
		}
		return false;
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

	private void clearList(Player p) {
		getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", new ArrayList<String>());
		savePlayers();
		pSend(p, getConfig().getString("Messages.clear-filter"));
		
	}

	@SuppressWarnings("unused")
	private void viewList(Player p, int page) {
		int max = listener.getMaxFilter(p);
		List<String> filterList = new ArrayList<String>();
		if (false) {
			filterList = database.getFilterList(p);
		} else {
			filterList = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
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
						if (max >= i || max == -1) {
							filter = getConfig().getString("Messages.filter-item-layout").replaceAll("%#%", i + "").replaceAll("%ITEM%", "&a" + item);	
						} else {
							filter = getConfig().getString("Messages.filter-item-layout").replaceAll("%#%", i + "").replaceAll("%ITEM%", "&c" + item);
						}
						
					} else {
						if (!item.equalsIgnoreCase("")) {
							if (max >= i || max == -1) {
								filter = filter + "\n" + getConfig().getString("Messages.filter-item-layout").replaceAll("%#%", i + "").replaceAll("%ITEM%", "&a" + item);
							} else {
								filter = filter + "\n" + getConfig().getString("Messages.filter-item-layout").replaceAll("%#%", i + "").replaceAll("%ITEM%", "&c" + item);
							}
							
						}
					}
				}
			} else { break; }
			i++;
		}	
		pSend(p, getConfig().getString("Messages.filter-layout").replaceAll("%ITEMS%", filter).replaceAll("%CUR_PAGE%", (page + "").replaceAll(".0", "")).replaceAll("%ALL_PAGE%", (pages + "").replaceAll(".0", "")).replaceAll("%LIST_SIZE%", (length + "").replaceAll(".0", "")).replaceAll("%ALL_ITEM_COUNT%", (listLength + "").replaceAll(".0", "")).replaceAll("%NL%", "\n"));
	}

	@SuppressWarnings("unused")
	private void removeItem(Player p) {
		if (false) {
			if (database.removeItem(p, p.getItemInHand().getType().toString() + ":" + p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0])) {
				pSend(p, getConfig().getString("Messages.remove-success-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
			} else {
				pSend(p, getConfig().getString("Messages.remove-fail-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
			}
		} else {
			List<String> removeItems = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
					if (removeItems.remove(p.getItemInHand().getType().toString() + ":" + p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0])) {
						getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", removeItems);
						savePlayers();
						reloadPlayers();
						pSend(p, getConfig().getString("Messages.remove-success-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
					} else {
						pSend(p, getConfig().getString("Messages.remove-fail-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
					}
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void addItem(Player p) {
			if (false) {
				String item = p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0];
				if (database.addToFilter(p, p.getItemInHand().getType().toString() + ":" + item)) {
					pSend(p, getConfig().getString("Messages.add-to-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
				} else {
					pSend(p, getConfig().getString("Messages.already-added-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
				}
				
			} else { 
				List<String> items = getPlayers().getStringList("Players." + p.getUniqueId().toString() + ".Items");
				String item = p.getItemInHand().getData().toString().split("\\(")[1].split("\\)")[0];
				if (items.contains(p.getItemInHand().getType().toString() + ":" + item)) {
					pSend(p, getConfig().getString("Messages.already-added-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
					return;
				}
				items.add(p.getItemInHand().getType().toString() + ":" + item);
				getPlayers().set("Players." + p.getUniqueId().toString() + ".Items", items);
				savePlayers();
				reloadPlayers();
				pSend(p, getConfig().getString("Messages.add-to-filter").replaceAll("%ITEM%", p.getItemInHand().getType().toString()));
			}
	}

	@SuppressWarnings("unused")
	public void toggleStatus(Player p) {
		if (false) {
			if (!database.getState(p)) {
				database.setState(p, 1);
				pSend(p, getConfig().getString("Messages.toggle-filter").replaceAll("%STATE%", "&aON&r").replaceAll("%STATE_REVERSE%", "&cOFF&r"));
			} else {
				database.setState(p, 0);
				pSend(p, getConfig().getString("Messages.toggle-filter").replaceAll("%STATE%", "&cOFF&r").replaceAll("%STATE_REVERSE%", "&aON&r"));
			}
			
		} else {
			if (!getPlayers().getBoolean("Players." + p.getUniqueId().toString() + ".Enabled")) {
				getPlayers().set("Players." + p.getUniqueId().toString() + ".Enabled", true);
				savePlayers();
				reloadPlayers();
				pSend(p, getConfig().getString("Messages.toggle-filter").replaceAll("%STATE%", "&aON&r").replaceAll("%STATE_REVERSE%", "&cOFF&r"));
			} else {
				getPlayers().set("Players." + p.getUniqueId().toString() + ".Enabled", false);
				savePlayers();
				reloadPlayers();
				pSend(p, getConfig().getString("Messages.toggle-filter").replaceAll("%STATE%", "&cOFF&r").replaceAll("%STATE_REVERSE%", "&aON&r"));
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
