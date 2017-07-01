package fr.ng.ibalix.tuto;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class StoneListener implements Listener {
	
	private Tuto p;
	
	public StoneListener(Tuto p) {
		this.p = p;
		// Register des Events
		p.getServer().getPluginManager().registerEvents(this, p);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
	    // Get the player's location.
	    Location loc = event.getPlayer().getLocation();
	    // Sets loc to five above where it used to be. Note that this doesn't change the player's position.
	    loc.setY(loc.getY() - 1);
	    // Gets the block at the new location.
	    Block b = loc.getBlock();
	    // Sets the block to type id 1 (stone).
	    b.setType(Material.STONE);
	}
}
