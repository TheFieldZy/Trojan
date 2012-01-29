package me.FieldZ.Trojan;

import me.FieldZ.Trojan.GUI.TrojanHUD;
import me.FieldZ.Trojan.database.SQLDatabase;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TrojanPlayer {
	SpoutPlayer player;
	int level;
	int exp;
	SQLDatabase sql;
	TrojanCore plugin;
	TrojanHUD hud;
	
	public TrojanPlayer(SpoutPlayer player, TrojanCore instance) {
		this.player = player;
		sql = new SQLDatabase();
		plugin = instance;
	}
	
	public TrojanPlayer(SpoutPlayer player, int level, TrojanCore instance) {
		this.player = player;
		this.level = level;
		sql = new SQLDatabase();
		plugin = instance;
	}
	
	public TrojanPlayer(SpoutPlayer player, int level, int exp, TrojanCore instance) {
		this.player = player;
		this.level = level;
		this.exp = exp;
		sql = new SQLDatabase();
		plugin = instance;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getExp() {
		return exp;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public void levelUp() {
		setLevel(this.level++);
		int exp = plugin.config.getInt("Levels.Level-" + player.getLevel()) - getExp();
		setExp(exp);
		sql.registerLevel(player);
		player.sendMessage(ChatColor.GOLD + "Level Up: Reached level:" + getLevel());
	}
	
	public void addExp(int exp) {
		setExp(this.exp + exp);
		int expLevel = plugin.config.getInt("Levels.Level-" + player.getLevel());
		int currentExp = getExp();
		if (getLevel() == 1) {
			expLevel = 50;
		}
		if (currentExp > expLevel) {
			levelUp();
		}
		sql.registerExp(player);
	}
	
}
