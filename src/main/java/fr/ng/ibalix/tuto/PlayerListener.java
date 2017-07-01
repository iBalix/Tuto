package fr.ng.ibalix.tuto;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class PlayerListener implements Listener {
	
	private final Tuto p;
	
	public PlayerListener(Tuto p) {
		this.p = p;
		// Register des Events
		p.getServer().getPluginManager().registerEvents(this, p);
	}
	
	// Event onLogin
	@EventHandler(priority = EventPriority.HIGH)
	public void onLogin(PlayerLoginEvent event) {
		p.getLogger().log(Level.INFO, "Player " + event.getPlayer().getName() + " is logging in !");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		p.getLogger().log(Level.INFO, "Le bloc "+ event.getBlock() + " vient d'être cassé !");
	}
}
