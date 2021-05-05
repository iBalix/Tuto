package fr.ng.ibalix.nationsarcade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerPotionTask extends BukkitRunnable {

    private final NationsArcade plugin;

	//private long startTime;
	
	private String enCours;
	
	private Player player;

    public PlayerPotionTask(NationsArcade plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
    	player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 1000));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1000));
    }

}
