package fr.ng.ibalix.tuto;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TutoCommandExecutor implements CommandExecutor {

	private final Tuto p;
	private StoneListener stone;
	
	public TutoCommandExecutor(Tuto p) {
		this.p = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		
		/*
		 * Commande Hello
		 * Check si joueur/console et retourne un message
		 */
		if(cmd.getName().equalsIgnoreCase("hello")) {
			if(sender instanceof Player) {			
				Player player = (Player) sender;			
				player.sendMessage(ChatColor.GOLD + "Helloo, " + player.getName() + "!");
			} else {
				sender.sendMessage(ChatColor.AQUA + "Tu n'est pas un joueur !");
			}
			return true;
		} 
		
		/*
		 * Commande Hello2
		 * Gestion argument
		 */
		
		else if(cmd.getName().equalsIgnoreCase("hello2")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(args.length > 1) {
					player.sendMessage(args[0]);
				} else {
					player.sendMessage(ChatColor.DARK_RED + "Erreur, il faut au moins 1 argument");
				}
			} else {
				sender.sendMessage(ChatColor.AQUA + "Tu n'est pas un joueur !");
			}
			return true;
		}
		
		/*
		 * Commande testperm
		 * Test si permission tuto.test
		 */
		
		else if(cmd.getName().equalsIgnoreCase("testperm")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("tuto.test")) {
					player.sendMessage(ChatColor.GREEN + "Vous avez bien la permission !");
				} else {
					player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission !");
				}
			}
			return true;
		}
		
		/*
		 * Commande startstone
		 * Active stone 5 blocs au dessus
		 */
		
		else if(cmd.getName().equalsIgnoreCase("stone")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.GREEN + "Stone en folieee !!!");
				this.stone = new StoneListener(this.p);
			}
			return true;
		}
		
		/*
		 * Commande stopstone
		 * Desactive stone au dessus
		 */
		
		else if(cmd.getName().equalsIgnoreCase("stoneoff")) {
			PlayerMoveEvent.getHandlerList().unregisterAll(this.p);
			if(sender instanceof Player && this.stone != null) {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.RED + "Fin de la folie !!");
				PlayerMoveEvent.getHandlerList().unregister(this.stone);
			} else {
				sender.sendMessage(ChatColor.GOLD + "Console ou inactif !");
			}
			return true;
		}
		
		return false;
	}
}
