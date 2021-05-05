package fr.ng.ibalix.nationsarcade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.tommytony.war.event.WarBattleWinEvent;

public class ScoreTask extends BukkitRunnable implements Listener {

    private final NationsArcade p;
	
	private boolean finish;

    public ScoreTask(NationsArcade plugin, boolean finish) {
        this.p = plugin;
        this.finish = finish;
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void run() {
    	HashMap<String, Integer> score = new HashMap<String, Integer>();
    	
    	Iterator it = p.partie.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        
	        score.put(pair.getKey().toString(), Integer.parseInt(((HashMap)pair.getValue()).get("kills").toString()));
	        
	    }
	        
        Object[] a = score.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                           .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });
        
        String output = "";
        
        if(finish == true) {
	        output = ChatColor.YELLOW + "--- Classement final ---\n";	        	
        } else {
        	output = ChatColor.YELLOW + "--- Classement actuel ---\n";
        }
        
        int counter = 1;
        for (Object e : a) {        	
        	output += ChatColor.GOLD + "" + counter + " - " + ChatColor.YELLOW + ((Map.Entry<String, Integer>) e).getKey() + ChatColor.GOLD + " > " + ChatColor.YELLOW + ((Map.Entry<String, Integer>) e).getValue() + " kills\n";
        	counter += 1;
        }
        
        output += ChatColor.YELLOW + "---------------------";
        
        p.getServer().broadcastMessage(output);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
	public void onGameFinished(WarBattleWinEvent event) {
    	this.cancel();
    }

}
