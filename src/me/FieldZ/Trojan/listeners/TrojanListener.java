package me.FieldZ.Trojan.listeners;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import me.FieldZ.Trojan.TrojanCore;

import me.FieldZ.Trojan.TrojanPlayer;
import me.FieldZ.Trojan.database.SQLDatabase;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.getspout.spoutapi.SpoutManager;

public class TrojanListener implements Listener{
	TrojanCore plugin;
	SQLDatabase db;
	public TrojanListener(TrojanCore instance) {
		plugin = instance;
		db = new SQLDatabase();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void killEntity(EntityDeathEvent ev) {
		if (!ev.getEntity().getLastDamageCause().equals(DamageCause.ENTITY_ATTACK)) 
			return;
		
		Entity victim = ev.getEntity();
		Entity attacker = (Entity) ev.getEntity().getLastDamageCause();
		
		TrojanPlayer pl = null;
		if (attacker instanceof Player) {
			pl = new TrojanPlayer(SpoutManager.getPlayer((Player) attacker), plugin);
		} else {
			return;
		}
		int exp = pl.getExp();
		
		pl.setExp(exp + plugin.config.getInt("Exp." + "CREATURE_" + victim.toString().toUpperCase()));
		
		//Quick test
		if (victim instanceof Zombie) {
			pl.addExp(50);
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void mineBlock(BlockBreakEvent ev) {
		if (ev.isCancelled()) return;
		TrojanPlayer player = new TrojanPlayer(SpoutManager.getPlayer(ev.getPlayer()), plugin);
		Block b = ev.getBlock();
		if (!(plugin.config.contains("Exp.MINE_" + b.getType().toString().toUpperCase()))) 
			return;
		
		if (b.equals(Material.STONE)) {
			player.addExp(50);
		}
		
		player.addExp(plugin.config.getInt("Exp.MINE_" + b.getType().toString().toUpperCase()));
	}
	
	@EventHandler
	public void playerJoin(PlayerLoginEvent ev) {
		db.playerNew(ev.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerShoot(EntityShootBowEvent ev) {
		Entity e = ev.getEntity();
		Arrow ar = (Arrow) ev.getProjectile();
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.isOp()) {
				Vector vec1 = ar.getVelocity();
				Vector vec2 = new Vector(0, 1, 0);
				ar.getWorld().spawnArrow(ar.getLocation(), vec1.subtract(vec2), 0.6f, 12);
				ar.getWorld().spawnArrow(ar.getLocation(), vec1.add(vec2), 0.6f, 12);
			}
		}
	}
}
