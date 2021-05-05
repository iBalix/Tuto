package fr.ng.ibalix.nationsarcade;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;

import main.java.fr.ng.ibalix.firebase.Firebase;
import main.java.fr.ng.ibalix.firebase.error.FirebaseException;
import main.java.fr.ng.ibalix.firebase.error.JacksonUtilityException;
import main.java.fr.ng.ibalix.firebase.service.FirebaseService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import fr.ng.ibalix.nationsarcade.ArcadeListener;
import net.milkbowl.vault.economy.Economy;

public class NationsArcade extends JavaPlugin {
	
	public Economy econ = null;
	
	public HashMap<String, Integer> votes;
	
	public ArrayList<String> voters = new ArrayList<String>();
	
	public boolean started;
	
	public String mapVoted = null;

	public FirebaseService firebase;
	
	private MySQL mySQL;
	
	public HashMap<String, HashMap> partie  = new HashMap<String, HashMap>();

	public static NationsArcade instance;

	public HashMap<String, ArrayList<String>> lobbyGamesPlayers = new HashMap<>();

	public HashMap<String, String> lobbyGamesStatus = new HashMap<>();

	@Override
	public void onEnable() {
		
		// G�n�ration config
		this.saveDefaultConfig();
		
		// Initialisation des Commandes
		this.getCommand("vote").setExecutor(new NationsArcadeCommandExecutor(this));
		this.getCommand("game").setExecutor(new NationsArcadeCommandExecutor(this));
		this.getCommand("nareload").setExecutor(new NationsArcadeCommandExecutor(this));
		this.getCommand("joingame").setExecutor(new NationsArcadeCommandExecutor(this));

		votes = new HashMap<String, Integer>();
		
		initVotes();
		
		started = false;
		
		// Init event
		new ArcadeListener(this);
		
		// MySQL

		try
		{
			this.mySQL = new MySQL("jdbc:mysql://sql.nationsglory.fr:35250/stats", "stats", "8w78PgYkP3i3Gp");
			//mySQL.executeQuery("CREATE TABLE IF NOT EXISTS Stats(varchar(100) name NOT NULL,kills INT,deaths INT,victories INT,PRIMARY KEY(name))ENGINE=MYISAMDEFAULT CHARSET='utf8';");
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			this.mySQL = new MySQL("jdbc:mysql://sql.nationsglory.fr:35250/stats", "stats", "8w78PgYkP3i3Gp");
		}

		try {
			this.firebase = new FirebaseService(Firebase.firebaseUrl);
		} catch (FirebaseException e) {
			e.printStackTrace();
		}

		instance = this;

		if(this.getConfig().getBoolean("is_main")) {
			new RefreshServerTask().runTaskTimerAsynchronously(this, 60L, 200L);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					System.out.println("Update arcade status firebase");
					Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("status", "available");
					try {
						firebase.put("ArcadeStatus/" + Bukkit.getServer().getPort(), dataMap);
					} catch (FirebaseException | UnsupportedEncodingException e) {
						System.out.println(e.getMessage());
					} catch (JacksonUtilityException e) {
						e.printStackTrace();
					}
				}
			}.runTaskLaterAsynchronously(this, 1200L);
		}
	}

	// Activation du plugin	
	@Override
	public void onDisable() {
		getLogger().info("Plugin NationsArcade d�sactiv�");
	}
	
	public void initVotes() {
		List listMaps = getConfig().getList("maps");
		
		for (int i = 0; i < listMaps.size(); i++) {			
			votes.put(listMaps.get(i).toString(), 0);
		}
	}
	
	public MySQL sql() {
		return mySQL;
	}
}
