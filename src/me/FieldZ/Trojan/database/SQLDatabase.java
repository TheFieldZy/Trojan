package me.FieldZ.Trojan.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import me.FieldZ.Trojan.TrojanCore;
import me.FieldZ.Trojan.TrojanPlayer;

public class SQLDatabase {
	
	File dbFile;
	TrojanCore plugin;
	Logger log = Logger.getLogger("Minecraft");
	
	String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `players` ("+ 
		"playername varchar(32)," +
		"level SMALLINT UNSIGNED," +
		"exp SMALLINT UNSIGNED" +
		");";
	
	public Connection getSQLConnection() {
		String dbname = "players.db";
		String maindir = "plugins/Trojan/";
		File dataFolder = new File(maindir, dbname);
		if (!dataFolder.exists()) {
			try {
				dataFolder.createNewFile();
				Class.forName("org.sqlite.JDBC");
				Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
				Statement st = conn.createStatement();
				st.execute(CREATE_TABLE);
				return conn;
			} catch (IOException ex) {
					ex.printStackTrace();
			} catch (SQLException ex) {
		            ex.printStackTrace();
		    } catch (ClassNotFoundException ex) {
		        	ex.printStackTrace();
		    }
		}
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return conn;
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public void initialize(TrojanCore plugin) {
		this.plugin = plugin;
		Connection conn = getSQLConnection();
		if (conn == null) {
			log.log(Level.SEVERE, "* TrojanCore * Could not connect to SQL database, disabling");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;
			Statement st = null;
			
			try {
				DatabaseMetaData dbm = conn.getMetaData();
				rs = dbm.getTables(null, null, "players", null);
				if (!rs.next()){
					conn.setAutoCommit(false);
					st = conn.createStatement();
					st.execute(CREATE_TABLE);
					conn.commit();
				} 
				try {
					while(rs.next()) {
						String pName = rs.getString("playername").toLowerCase();
					}
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			try {
				if (!plugin.isEnabled())
					return;
				conn.close();
				log.info("TrojanCore: initialized DB");
			} catch (SQLException ex) {
				ex.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
	}
	
	public int getLevel(Player player) {
		try {
			int level = 0;
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT level from `players` WHERE playername = '" + player.getName() + "';");
			while(rs.next()) {
				level = rs.getInt("level");
			}
			return level;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	
	public void registerLevel(Player player) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT level from `players` WHERE playename = '" + player.getName() + "';");
			int prev = 0;
			while(rs.next()) {
				prev = rs.getInt("level");
			}
			TrojanPlayer pl = new TrojanPlayer(SpoutManager.getPlayer(player), plugin);
			int currLevel = player.getLevel();
			st.execute("replace into `players` (playername, level, exp) VALUES ('" + player.getName() + "', " + currLevel + ", " + pl.getExp() + ");");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public int getExp(Player player) {
		try {
			int exp = 0;
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT exp from `players` WHERE playername = '" + player.getName() + "';");
			while(rs.next()) {
				exp = rs.getInt("exp");
			}
			return exp;
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	
	public void registerExp(Player player) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT exp from `players` WHERE playername = '" + player.getName() + "';");
			int prev = 0;
			while(rs.next()) {
				prev = rs.getInt("exp");
			}
			TrojanPlayer pl = new TrojanPlayer(SpoutManager.getPlayer(player), plugin);
			int currExp = pl.getExp();
			st.execute("REPLACE into `players` (exp, , ");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void playerNew(Player player) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT playername from `players` WHERE playername = '" + player.getName() + "';");
			if (!rs.next()) {
				st.execute("INSERT INTO `players` (playername, level, exp) VALUES ('" + player.getName() + "', 1, 1);");
				TrojanPlayer pl = new TrojanPlayer(SpoutManager.getPlayer(player), plugin);
				pl.setExp(0);
				pl.setLevel(1);
				log.info("Adding player to database: " + player.getName());
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
