package fr.ng.ibalix.tuto;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Tuto extends JavaPlugin {
	@Override
	public void onEnable() {	
		getLogger().info("Plugin Tuto activé");
		
		// Génération config
		this.saveDefaultConfig();
		
		// Initialisation des Events
		new PlayerListener(this);
		
		// Initialisation des Commandes
		this.getCommand("hello").setExecutor(new TutoCommandExecutor(this));
		this.getCommand("hello2").setExecutor(new TutoCommandExecutor(this));
		this.getCommand("testperm").setExecutor(new TutoCommandExecutor(this));
		this.getCommand("stone").setExecutor(new TutoCommandExecutor(this));
		this.getCommand("stoneoff").setExecutor(new TutoCommandExecutor(this));
		
		// Accès un la config
		getLogger().info(this.getConfig().getString("msg_hello")+ "ggggggg");
	}
	
	// Activation du plugin	
	@Override
	public void onDisable() {
		getLogger().info("Plugin Tuto désactivé");		
	}
}
