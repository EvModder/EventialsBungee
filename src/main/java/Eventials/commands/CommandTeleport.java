package Eventials.commands;

import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTeleport extends Command {
	EventialsBungee plugin;
	
	public CommandTeleport(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
		plugin = EventialsBungee.getPlugin();
	}

	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0){
			sender.sendMessage(new ComponentBuilder("You must specify a player").color(ChatColor.RED).create());
			return;
		}
		ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
		if(target == null){
			sender.sendMessage(new ComponentBuilder("Player not found").color(ChatColor.RED).create());
			return;
		}
		//send them to the target's server
		((ProxiedPlayer)sender).connect(target.getServer().getInfo());
		
		//TODO: make them teleport to the target
		//This doesn't currently work because they are still changing servers
		((ProxiedPlayer)sender).chat("/tp "+target.getName());
	}
}