package me.FieldZ.Trojan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import me.FieldZ.Trojan.database.SQLDatabase;
import me.FieldZ.Trojan.listeners.TrojanListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TrojanCore extends JavaPlugin{
	
	Logger log = Logger.getLogger("Minecraft");
	SQLDatabase db = new SQLDatabase();
	File configFile;
	public FileConfiguration config;

	public void onEnable() {
		configFile = new File(getDataFolder(), "config.yml");
		try {
			firstRun();
		} catch(Exception e) {
			e.printStackTrace();
		}
		config = new YamlConfiguration();
		loadYamls();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new TrojanListener(this), this);
		
		db.initialize(this);
		log.info("Trojan has been enabled");
	}
	
	public void onDisable() {
		saveYamls();
		log.info("Trojan has been disabled");
	}
	
	private void firstRun() throws Exception {
	    if(!configFile.exists()){
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
	    }
	}
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void saveYamls() {
	    try {
	        config.save(configFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void loadYamls() {
	    try {
	        config.load(configFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
