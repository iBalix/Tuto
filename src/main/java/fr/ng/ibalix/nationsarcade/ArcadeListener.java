package fr.ng.ibalix.nationsarcade;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;

import main.java.fr.ng.ibalix.firebase.error.FirebaseException;
import main.java.fr.ng.ibalix.firebase.error.JacksonUtilityException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.google.common.collect.Lists;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarBattleWinEvent;
import com.tommytony.war.event.WarPlayerDeathEvent;

public final class ArcadeListener implements Listener {
	
	private final NationsArcade p;
	
	private ArrayList<String> godModed = new ArrayList<String>();
	
	public ArcadeListener(NationsArcade p) {
		this.p = p;
		// Register Events
		p.getServer().getPluginManager().registerEvents(this, p);
	}
	
	// Event onDeath
		@EventHandler(priority = EventPriority.MONITOR)
		public void onDeath(WarPlayerDeathEvent event) {

			if(p.getConfig().getBoolean("is_main")) {
				Player player = (Player) event.getVictim();

				Player killer = (Player) player.getKiller();

				if (killer != null) {

					Team teamVictim = Team.getTeamByPlayerName(player.getName());
					Team teamKiller = Team.getTeamByPlayerName(killer.getName());

					// If players in different teams
					if (!teamVictim.equals(teamKiller)) {
						if (p.partie.containsKey(killer.getName())) {
							HashMap actuel = p.partie.get(killer.getName());
							actuel.put("kills", Integer.parseInt(actuel.get("kills").toString()) + 1);
							p.partie.put(killer.getName(), actuel);
						} else {
							HashMap nouveau = new HashMap();
							nouveau.put("kills", 1);
							nouveau.put("morts", 0);
							nouveau.put("victoires", 0);
							nouveau.put("defaites", 0);
							nouveau.put("allyKills", 0);

							p.partie.put(killer.getName(), nouveau);
						}

						if (p.partie.containsKey(player.getName())) {
							HashMap actuel = p.partie.get(player.getName());
							actuel.put("morts", Integer.parseInt(actuel.get("morts").toString()) + 1);
							p.partie.put(player.getName(), actuel);
						} else {
							HashMap nouveau = new HashMap();
							nouveau.put("kills", 0);
							nouveau.put("morts", 1);
							nouveau.put("victoires", 0);
							nouveau.put("defaites", 0);
							nouveau.put("allyKills", 0);

							p.partie.put(player.getName(), nouveau);
						}

						p.getServer().broadcastMessage(ChatColor.GREEN + killer.getName() + ChatColor.RESET + " a tué " + ChatColor.RED + player.getName());
					} else {
						// Si player in same team
						if (!killer.equals(player)) {
							p.getServer().broadcastMessage(ChatColor.DARK_RED + killer.getName() + ChatColor.RED + " a tué son allié " + ChatColor.DARK_RED + player.getName());
							killer.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "Vous perdez 1 points au classement pour avoir tué votre allié.");

							if (p.partie.containsKey(killer.getName())) {
								HashMap actuel = p.partie.get(killer.getName());
								actuel.put("kills", Integer.parseInt(actuel.get("kills").toString()) - 1);
								actuel.put("allyKills", Integer.parseInt(actuel.get("allyKills").toString()) + 1);

								p.partie.put(killer.getName(), actuel);

								if (Integer.parseInt(actuel.get("allyKills").toString()) == 8) {
									p.getServer().setWhitelist(true);
									killer.kickPlayer(ChatColor.RED + "Vous avez été exclu pour avoir tué plus de 8 alliés !");
									p.getServer().broadcastMessage(ChatColor.DARK_RED + killer.getName() + ChatColor.RED + " vient d'étre exclu pour avoir tué plus de 8 alliés.");
								} else {
									int nb = 8 - Integer.parseInt(actuel.get("allyKills").toString());
									killer.sendMessage(ChatColor.RED + "Plus que " + nb + " kills allié avant sanction.");
								}

							} else {
								HashMap nouveau = new HashMap();
								nouveau.put("kills", 0);
								nouveau.put("morts", 0);
								nouveau.put("victoires", 0);
								nouveau.put("defaites", 0);
								nouveau.put("allyKills", 1);

								p.partie.put(killer.getName(), nouveau);
							}
						} else {
							// Si le joueur se suicide
							if (p.partie.containsKey(player.getName())) {
								HashMap actuel = p.partie.get(player.getName());
								actuel.put("morts", Integer.parseInt(actuel.get("morts").toString()) + 1);
								p.partie.put(player.getName(), actuel);
							} else {
								HashMap nouveau = new HashMap();
								nouveau.put("kills", 0);
								nouveau.put("morts", 1);
								nouveau.put("victoires", 0);
								nouveau.put("defaites", 0);
								nouveau.put("allyKills", 0);

								p.partie.put(player.getName(), nouveau);
							}
						}
					}

				} else {
					if (p.partie.containsKey(player.getName())) {
						HashMap actuel = p.partie.get(player.getName());
						actuel.put("morts", Integer.parseInt(actuel.get("morts").toString()) + 1);
						p.partie.put(player.getName(), actuel);
					} else {
						HashMap nouveau = new HashMap();
						nouveau.put("kills", 0);
						nouveau.put("morts", 1);
						nouveau.put("victoires", 0);
						nouveau.put("defaites", 0);
						nouveau.put("allyKills", 0);

						p.partie.put(player.getName(), nouveau);
					}
				}

				godModed.add(player.getName());

				new PlayerPotionTask(p, player).runTaskLater(p, 20L);

				new PlayerTask(p, player, godModed).runTaskLater(p, 100L);
			}
		}
	
	// Event damage
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(p.getConfig().getBoolean("is_main")) {
			if (event.getEntity() instanceof Player) {
				if (godModed.contains(((Player) event.getEntity()).getName())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(p.getConfig().getBoolean("is_main")) {
			Iterator it = p.lobbyGamesPlayers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();

				ArrayList<String> listPlayers = (ArrayList<String>)pair.getValue();

				if(listPlayers.contains(event.getPlayer().getName())) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + pair.getKey() + " 3 &e" + listPlayers.size() + "/12");
					listPlayers.remove(event.getPlayer().getName());
				}
			}

			final String file = "./world/players/" + event.getPlayer().getName() + ".dat";
			Bukkit.getServer().getScheduler().runTaskAsynchronously(p, new Runnable() {
				public void run() {
					File f1 = new File(file);
					f1.delete();
				}
			});
		}
	}

		@EventHandler(priority = EventPriority.MONITOR)
	public void onEndGame(WarBattleWinEvent event) {
		if(p.getConfig().getBoolean("is_main")) {
			List<Team> teams = event.getWinningTeams();

			List<String> winners = null;

			Team teamWin = null;

			for (Team t : teams) {
				winners = t.getPlayerNames();
				teamWin = t;
			}

			Iterator it = p.partie.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				if (winners.contains(pair.getKey().toString())) {
					((HashMap) pair.getValue()).put("victoires", 1);
				} else {
					((HashMap) pair.getValue()).put("defaites", 1);
				}
			}

			p.getServer().setWhitelist(true);

			p.getServer().broadcastMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "L'équipe " + teamWin.getName() + " remporte la partie !");

			// /leave et /spawn
			new BukkitRunnable() {

				@Override
				public void run() {
					// TP all to spawn

					List<Player> list = Lists.newArrayList();
					for (World world : Bukkit.getWorlds()) {
						list.addAll(world.getPlayers());
					}

					for (Player pl : list) {
						pl.performCommand("leave");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + pl.getName());
					}
				}
			}.runTaskLater(p, 40L);

			// Affichage score final
			new ScoreTask(p, true).runTaskLater(p, 60L);

			// Kick des joueurs
			new BukkitRunnable() {

				@Override
				public void run() {
					List<Player> list = Lists.newArrayList();
					for (World world : Bukkit.getWorlds()) {
						list.addAll(world.getPlayers());
					}

					for (Player pl : list) {
						// TODO Send player back to hub
						pl.kickPlayer(ChatColor.GOLD + "Fin de la session, bon retour sur NationsGlory ;)");
					}
				}
			}.runTaskLater(p, 400L);

			new BukkitRunnable() {
				@Override
				public void run() {
					Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("status", "out");
					try {
						p.firebase.put("ArcadeStatus/" + Bukkit.getServer().getPort(), dataMap);
					} catch (FirebaseException | UnsupportedEncodingException e) {
						System.out.println(e.getMessage());
					} catch (JacksonUtilityException e) {
						e.printStackTrace();
					}
				}
			}.runTaskLaterAsynchronously(p, 500L);

			// Update sql
			new BukkitRunnable() {
				@Override
				public void run() {
					Iterator it = p.partie.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();

						String name = (String) pair.getKey();
						int kills = Integer.parseInt(((HashMap) pair.getValue()).get("kills").toString());
						int morts = Integer.parseInt(((HashMap) pair.getValue()).get("morts").toString());
						int victoires = Integer.parseInt(((HashMap) pair.getValue()).get("victoires").toString());
						int defaites = Integer.parseInt(((HashMap) pair.getValue()).get("defaites").toString());

						p.sql().executeQuery("INSERT INTO arcade_stats(nom, kills, morts, victoires, defaites) VALUES('" + name + "', " + kills + ", " + morts + ", " + victoires + ", " + defaites + ") ON DUPLICATE KEY UPDATE kills = kills+" + kills + ", morts = morts+" + morts + ", victoires = victoires+" + victoires + ", defaites = defaites+" + defaites);

						it.remove(); // avoids a ConcurrentModificationException
					}
				}
			}.runTaskLater(p, 600L);

			// Delete .dat
			new BukkitRunnable() {

				@Override
				public void run() {
					String worldName = "world";
					File playerFilesDir = new File(worldName + "/players");
					if (playerFilesDir.isDirectory()) {
						String[] playerDats = playerFilesDir.list();
						for (int i = 0; i < playerDats.length; i++) {
							File datFile = new File(playerFilesDir, playerDats[i]);
							datFile.delete();
						}
					}

					p.getServer().shutdown();
				}
			}.runTaskLater(p, 1400L);
		}
	}
}
