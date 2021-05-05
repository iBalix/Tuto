package fr.ng.ibalix.nationsarcade;

import main.java.fr.ng.ibalix.firebase.error.FirebaseException;
import main.java.fr.ng.ibalix.firebase.error.JacksonUtilityException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StartGameTask extends BukkitRunnable {

    private int timer = 5;

    private String targetPort;

    public StartGameTask(String targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public void run() {
        if(NationsArcade.instance.lobbyGamesPlayers.containsKey(targetPort) && NationsArcade.instance.lobbyGamesPlayers.get(targetPort).size() >= 10) {
            if (timer > 0) {
                for(String playerInGame : NationsArcade.instance.lobbyGamesPlayers.get(targetPort)) {
                    if(Bukkit.getPlayer(playerInGame) != null) {
                        Bukkit.getPlayer(playerInGame).sendMessage(ChatColor.YELLOW + "Début de la partie dans " + ChatColor.GOLD + timer + ChatColor.YELLOW + " sec...");
                    }
                }
            } else {
                NationsArcade.instance.lobbyGamesStatus.put(targetPort, "in_progress");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + targetPort + " 3 &e" + NationsArcade.instance.lobbyGamesPlayers.get(targetPort).size() + "/12");

                for(String playerInGame : NationsArcade.instance.lobbyGamesPlayers.get(targetPort)) {
                    if(Bukkit.getPlayer(playerInGame) != null) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "changeserver " + playerInGame + " event2.nationsglory.fr " + targetPort);
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                        dataMap.put("status", "in_progress");
                        try {
                            NationsArcade.instance.firebase.put("ArcadeStatus/" + targetPort, dataMap);
                        } catch (FirebaseException | UnsupportedEncodingException e) {
                            System.out.println(e.getMessage());
                        } catch (JacksonUtilityException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(NationsArcade.instance);

                this.cancel();
            }

            timer--;
        } else {
            this.cancel();
        }
    }

}
