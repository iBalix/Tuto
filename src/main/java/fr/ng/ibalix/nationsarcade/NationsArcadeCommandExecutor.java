package fr.ng.ibalix.nationsarcade;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.Commandwarp;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.google.common.collect.Lists;
import com.tommytony.war.War;
import com.tommytony.war.event.WarBattleWinEvent;

import net.ess3.api.InvalidWorldException;
import net.milkbowl.vault.economy.EconomyResponse;

public class NationsArcadeCommandExecutor implements CommandExecutor, Listener {

	private final NationsArcade p;
	
	public NationsArcadeCommandExecutor(NationsArcade p) {
		this.p = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		
		if(cmd.getName().equalsIgnoreCase("vote")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(args.length > 0) {
					if(checkIfMapExist(args[0])) {
						if(!p.voters.contains(player.getName())) {
							int voteMap = p.votes.get(args[0]);
						
							p.votes.put(args[0], voteMap + 1);
							
							if(!p.started) {
								p.started = true;
								p.getServer().broadcastMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "---- Vote Map ----\n" + ChatColor.GOLD + "Le vote pour la map vient de d�buter, il vous reste 30 secondes pour voter !\n" + ChatColor.YELLOW + "----> /vote <map>\n" + ChatColor.BOLD + ChatColor.YELLOW + "---- ------- ----");
								new VoteTask(p, this).runTaskLater(p, 600L);
								new ScoreTask(p, false).runTaskTimer(p, 1600L, 600L);
							}
							
							p.voters.add(player.getName());
							
				    		String output = "";
				    		Iterator it = p.votes.entrySet().iterator();
				    		while (it.hasNext()) {
				    			Map.Entry map = (Map.Entry) it.next();
				    			output += map.getKey() + "(" + map.getValue() + ")" + ", ";
				    		}
				    		
				    		output = output.substring(0, output.length() - 2);
				    		
				    		p.getServer().broadcastMessage(ChatColor.GOLD + "Etat du vote: " + ChatColor.YELLOW + output);
						} else {
							player.sendMessage(ChatColor.RED + "Vous ne pouvez pas voter plusieurs fois !");
						}
						
					} else {
						player.sendMessage(ChatColor.RED + "Cette map n'existe pas !");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Vous devez pr�ciser la map sur laquelle vous souhaitez jouer !" + ChatColor.DARK_RED + " /vote <map>");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("game")) {
			
			if(sender instanceof Player) {
				Player player = (Player) sender;

				if(p.mapVoted != null) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "warp "+p.mapVoted+" "+player.getName());
				} else {
					player.sendMessage(ChatColor.RED + "Il n'y a pas de partie en cours !");
				}
			}
			return true;
		} else if(cmd.getName().equalsIgnoreCase("nareload")) {
			sender.sendMessage(ChatColor.GREEN + "La config a bien �t� reload !!");
			p.reloadConfig();
			return true;
		} else if(cmd.getName().equalsIgnoreCase("joingame")) {
			if(args.length > 0 && sender instanceof Player) {
				String targetPort = args[0];
				if(p.lobbyGamesPlayers.containsKey(targetPort)) {
					if(p.lobbyGamesPlayers.get(targetPort).size() < 12) {
						if (!p.lobbyGamesPlayers.get(targetPort).contains(sender.getName())) {
							// Remove player from other games
							Iterator it = p.lobbyGamesPlayers.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry)it.next();

								ArrayList<String> listPlayers = (ArrayList<String>)pair.getValue();

								if(listPlayers.contains(sender.getName())) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + pair.getKey() + " 3 &e" + listPlayers.size() + "/12");
									listPlayers.remove(sender.getName());
								}
							}

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + targetPort + " 3 &e" + p.lobbyGamesPlayers.get(targetPort).size() + "/12");
							p.lobbyGamesPlayers.get(targetPort).add(sender.getName());
							sender.sendMessage(ChatColor.GREEN + "Vous venez de rejoindre la partie.");

							// Check if party is ready to launch
							if (p.lobbyGamesPlayers.get(targetPort).size() == 10) {
								new StartGameTask(targetPort).runTaskTimer(p, 20L, 20L);
							}
						} else {
							p.lobbyGamesPlayers.get(targetPort).remove(sender.getName());
							sender.sendMessage(ChatColor.RED + "Vous venez de quitter la partie.");
						}
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean checkIfMapExist(String vote) {
		List maps = p.getConfig().getList("maps");
		
		for (int i = 0; i < maps.size(); i++) {			
			String map = maps.get(i).toString();
			if(vote.toLowerCase().equals(map.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public void teleportAllPlayersToBattle(String mapName) {
	    List<Player> list = Lists.newArrayList();
	    for (World world : Bukkit.getWorlds()) {
	        list.addAll(world.getPlayers());
	    }

	    for(Player pl : list) {
	    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "warp "+mapName+" "+pl.getName());
	    }
	}
	
	public static void kickAllPlayers() {
	    List<Player> list = Lists.newArrayList();
	    for (World world : Bukkit.getWorlds()) {
	        list.addAll(world.getPlayers());
	    }
	    
	    for(Player pl : list) {
	    	pl.kickPlayer("Fin de la session, bon retour sur NationsGlory ;)");
	    }
	}
}
