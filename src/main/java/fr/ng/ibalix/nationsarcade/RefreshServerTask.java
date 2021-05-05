package fr.ng.ibalix.nationsarcade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.fr.ng.ibalix.firebase.error.FirebaseException;
import main.java.fr.ng.ibalix.firebase.model.FirebaseResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RefreshServerTask extends BukkitRunnable {

    public RefreshServerTask() {}

    @Override
    public void run() {
        try {
            System.out.println("Update arcade servers firebase");
            FirebaseResponse response = NationsArcade.instance.firebase.get("ArcadeStatus");

            if(response.getSuccess() && !response.getRawBody().equals("null")) {
                HashMap<String, HashMap<String, String>> status = new Gson().fromJson(response.getRawBody(), new TypeToken<HashMap<String, HashMap<String, String>>>() {}.getType());

                Iterator it = status.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    Iterator it2 = ((HashMap<String, String>)pair.getValue()).entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry)it2.next();
                        String statusString = (String)pair2.getValue();

                        if(!NationsArcade.instance.lobbyGamesPlayers.containsKey((String)pair.getKey())) {
                            NationsArcade.instance.lobbyGamesPlayers.put((String)pair.getKey(), new ArrayList<String>());
                        }

                        NationsArcade.instance.lobbyGamesStatus.put((String)pair.getKey(), statusString);

                        if(statusString.equals("available")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + pair.getKey() + " 2 &aDisponible");
                        } else if(statusString.equals("in_progress")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + pair.getKey() + " 2 &6Partie_en_cours");
                        } else if(statusString.equals("out")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + pair.getKey() + " 2 &cNon-disponible");
                        }
                    }
                }
            }


        } catch (FirebaseException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
