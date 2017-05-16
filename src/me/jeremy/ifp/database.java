package me.jeremy.ifp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jeremy.ifp.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class database implements Listener {
	static main plugin;
	static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static String username = null;
    static String password = null;
    static String url = null;
    static PreparedStatement prep = null;
	public database(main main) {
	plugin = main;
	}
	
	public static void quietConnect() {
		try {
			try {
				con.close();
				con = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {}
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage() + " (Quiet Connect)");
		}
	}
	
	public static void connect() {
		username = plugin.getConfig().getString("Database.Username");
		password = plugin.getConfig().getString("Database.Password");;
	    url = "jdbc:mysql://"+ plugin.getConfig().getString("Database.Host") + ":" + plugin.getConfig().getString("Database.Port") + "/" + plugin.getConfig().getString("Database.Database");
	    try {
			con = DriverManager.getConnection(url, username, password);
			quietConnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    try {
			//con = DriverManager.getConnection(url, user, pass);
			 st = con.createStatement();
			try {
				  String table = 
						  "CREATE TABLE players " +
				                   "(id INTEGER NOT NULL AUTO_INCREMENT, " +
				                   " state TINYINT(1), " +
				                   " username VARCHAR(16), " +
				                   " uuid VARCHAR(50), " +
				                   " filter VARCHAR(10000), " +
				                   " PRIMARY KEY ( id ))";
				  prep = con.prepareStatement(table);
				  prep.executeUpdate();
				  //st.executeUpdate(table);
				  System.out.println("[ItemFilterPickup] Database Table Created! - player");
			 }
			 catch(SQLException s){
				 System.out.println("[ItemFilterPickup] Database Table Exist! - players");
			 }
		 
			System.out.println("[ItemFilterPickup] Success! Connected To Database!");
	    } catch (SQLException e) {
	    	e.printStackTrace();
		}finally {
			try {
		      if (rs != null) {
		          
		          rs.close();
		        }
	
		      if (prep != null) {
		          prep.close();
		        }
		      if (con != null) {
		          con.close();
		        }
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}
	
	
	
	
	
	public static boolean checkDatabase(Player p, ItemStack itemA) {
		
        	try {
        		quietConnect();
        		if (p.hasPermission("itemfilterpickup.user") || p.hasPermission("itemfilterpickup.user.canfilter")) {
	        		prep = con.prepareStatement("SELECT state, filter FROM players WHERE uuid = ?");
	        		prep.setString(1, p.getUniqueId().toString());
	        		rs = prep.executeQuery();
	        		if (rs.next()) {
	        			if (rs.getInt("state") == 1) {
	        					int b = 1;
	        					int max = listener.getMaxFilter(p);
	        					for (String filter : rs.getString("filter").split(";")) {
	        						if (max == -1) {
	        							ItemStack a = new ItemStack(Material.matchMaterial(filter.split(":")[0]), itemA.getAmount(), (byte) Integer.parseInt(filter.split(":")[1]));
	        							if (itemA.equals(a)) {
	        								return true;
	        							}
	        							b++;
	        						} else if (b <= max && max != -1) { 
	        							ItemStack a = new ItemStack(Material.matchMaterial(filter.split(":")[0]), itemA.getAmount(), (byte) Integer.parseInt(filter.split(":")[1]));
	        							if (itemA.equals(a)) {
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
	        			} else {
	        				return false;
	        			}
        		} else {
        			return false;
        		}
        	
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        }finally {
    		try {
    		      if (rs != null) {
    		          
    		          rs.close();
    		        }

    		      if (prep != null) {
    		          prep.close();
    		        }
    		      if (con != null) {
    		          con.close();
    		        }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
			return false;
	}

	public static void setState(Player p, int state) {
		try { 
			quietConnect();
			prep = con.prepareStatement("UPDATE players SET state = ?, username = ? WHERE uuid = ?");
			prep.setInt(1, state);
			prep.setString(2, p.getName());
			prep.setString(3, p.getUniqueId().toString());
			prep.executeUpdate();
		} catch (SQLException ex) {
	        System.out.println(ex.getMessage());
	
	    }finally {
			try {
			      if (rs != null) {
			          
			          rs.close();
			        }
	
			      if (prep != null) {
			          prep.close();
			        }
			      if (con != null) {
			          con.close();
			        }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static void attemptAddorUpdate(Player p) {
		try {
    		quietConnect();
    		prep = con.prepareStatement("SELECT state, filter FROM players WHERE uuid = ?");
    		prep.setString(1, p.getUniqueId().toString());
    		rs = prep.executeQuery();
    		if (!rs.next()) {
    			prep = con.prepareStatement("INSERT INTO players (id, state, username, uuid, filter) VALUES (NULL, 0, ?, ?, ?)");
    			prep.setString(1, p.getName());
    			prep.setString(2, p.getUniqueId().toString());
    			prep.setString(3, "");
    			prep.executeUpdate();
    		}
		} catch (SQLException e) {
	    	e.printStackTrace();
		}finally {
			try {
		      if (rs != null) {
		          
		          rs.close();
		        }
	
		      if (prep != null) {
		          prep.close();
		        }
		      if (con != null) {
		          con.close();
		        }
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	public static boolean getState(Player p) {
	try {
    		quietConnect();
    		prep = con.prepareStatement("SELECT state FROM players WHERE uuid = ?");
    		prep.setString(1, p.getUniqueId().toString());
    		rs = prep.executeQuery();
    		if (rs.next()) {
    			if (rs.getInt("state") == 0) {
    				return false;
    			} else {
    				return true;
    			}
    		}
   	} catch (SQLException e) {
    	e.printStackTrace();
	}finally {
		try {
	      if (rs != null) {
	          
	          rs.close();
	        }

	      if (prep != null) {
	          prep.close();
	        }
	      if (con != null) {
	          con.close();
	        }
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
		return false;
	}

	public static boolean addToFilter(Player p, String string) {
		try {
			String filterOut = "";
    		quietConnect();
    		prep = con.prepareStatement("SELECT state, filter FROM players WHERE uuid = ?");
    		prep.setString(1, p.getUniqueId().toString());
    		rs = prep.executeQuery();
    		if (rs.next()) {
    			prep = con.prepareStatement("UPDATE players SET filter = ?, username = ? WHERE uuid = ?");
    			if (rs.getString("filter").equalsIgnoreCase("")) {
    				filterOut = string;
    			} else {
    				if (rs.getString("filter").contains(string)) {
    					return false;
    				} else {
    					filterOut = rs.getString("filter") + ";" + string;
    					
    				}
    			}
    			prep.setString(1, filterOut);
    			prep.setString(2, p.getName());
    			prep.setString(3, p.getUniqueId().toString());
    			prep.executeUpdate();
    			return true;
    		}
		} catch (SQLException e) {
	    	e.printStackTrace();
		}finally {
			try {
		      if (rs != null) {
		          
		          rs.close();
		        }
	
		      if (prep != null) {
		          prep.close();
		        }
		      if (con != null) {
		          con.close();
		        }
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		return false;
	}

	public static boolean removeItem(Player p, String string) {
		try {
    		quietConnect();
    		prep = con.prepareStatement("SELECT filter FROM players WHERE uuid = ?");
    		prep.setString(1, p.getUniqueId().toString());
    		rs = prep.executeQuery();
    		if (rs.next()) {
    			prep = con.prepareStatement("UPDATE players SET filter = ?, username = ? WHERE uuid = ?");
    			boolean found = false;
    			if (rs.getString("filter").equalsIgnoreCase("")) {
    				return false;
    			} else {
    				if (rs.getString("filter").contains(";")) {
    					String output = "";
    					for (String item : rs.getString("filter").split(";")) {
    						if (!item.equals(string)) {
    							if (output.equals("")) {
    								output = item;
    							} else {
    								output = output + ";" + item;
    							}
    						} else { found = true; }
    					}
    					prep.setString(1, output);
    	    			prep.setString(2, p.getName());
    	    			prep.setString(3, p.getUniqueId().toString());
    	    			prep.executeUpdate();
    					return found;
    				}  else {
    					if (rs.getString("filter").equals(string)) { 
    						prep.setString(1, "");
        	    			prep.setString(2, p.getName());
        	    			prep.setString(3, p.getUniqueId().toString());
        	    			prep.executeUpdate();
        	    			return true; 
        	    		}
    				}
    			}
    			
    		}
		} catch (SQLException e) {
	    	e.printStackTrace();
		}finally {
			try {
		      if (rs != null) {
		          
		          rs.close();
		        }
	
		      if (prep != null) {
		          prep.close();
		        }
		      if (con != null) {
		          con.close();
		        }
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		return false;
	}

	public static List<String> getFilterList(Player p) {
		try {
    		quietConnect();
    		prep = con.prepareStatement("SELECT filter FROM players WHERE uuid = ?");
    		prep.setString(1, p.getUniqueId().toString());
    		rs = prep.executeQuery();
    		if (rs.next()) {
    			if (rs.getString("filter").contains(";")) {
    				List<String> filter = Arrays.asList(rs.getString("filter").split(";"));
        			return filter;
    			} else {
    				List<String> filter = new ArrayList<String>();
    				filter.add(rs.getString("filter"));
    				return filter;
    			}
    			
    		}
    			
		} catch (SQLException e) {
	    	e.printStackTrace();
		}finally {
			try {
		      if (rs != null) {
		          
		          rs.close();
		        }
	
		      if (prep != null) {
		          prep.close();
		        }
		      if (con != null) {
		          con.close();
		        }
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		return new ArrayList<String>();
	}
}
