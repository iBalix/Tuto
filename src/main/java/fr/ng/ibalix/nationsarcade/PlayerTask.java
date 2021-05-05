package fr.ng.ibalix.nationsarcade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTask extends BukkitRunnable {

    private final NationsArcade plugin;

	//private long startTime;
	
	private String enCours;
	
	private Player player;
	
	private ArrayList<String> godModed;

    public PlayerTask(NationsArcade plugin, Player player, ArrayList<String> godModed) {
        this.plugin = plugin;
        this.player = player;
        this.godModed = godModed;
    }

    @Override
    public void run() {
    	int index = godModed.indexOf(player.getName());
    	godModed.remove(index);
    }

}
