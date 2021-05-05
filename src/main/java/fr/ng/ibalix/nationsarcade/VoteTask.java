package fr.ng.ibalix.nationsarcade;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteTask extends BukkitRunnable {

    private final NationsArcade plugin;

	//private long startTime;
	
	private String enCours;
	
	private NationsArcadeCommandExecutor exec;

    public VoteTask(NationsArcade plugin, NationsArcadeCommandExecutor exec) {
        this.plugin = plugin;
        this.exec = exec;
    }

    @Override
    public void run() {
		String bestName = "";
		int bestVote = 0;
		
		Iterator it = plugin.votes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry map = (Map.Entry) it.next();
			if(bestVote < Integer.parseInt(map.getValue().toString())) {
				bestVote = Integer.parseInt(map.getValue().toString());
				bestName = map.getKey().toString();
			}
		}
		
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "Fin des votes ! Vous avez plébiscité la map " + ChatColor.YELLOW + bestName + ChatColor.GOLD + " avec " + ChatColor.YELLOW + bestVote + " vote(s).");
		
		exec.teleportAllPlayersToBattle(bestName);
		
		plugin.mapVoted = bestName;
    }

}
