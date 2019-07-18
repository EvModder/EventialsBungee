package Eventials.listeners;

import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {
	EventialsBungee plugin;
	boolean rejoinLastServer;
	
	public PlayerQuitListener(){
		plugin = EventialsBungee.getPlugin();
		rejoinLastServer = plugin.getConfig().getBoolean("rejoin-last-server", true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent evt) {
		if(!evt.getPlayer().hasPermission("evbungee.silentquit")){
			plugin.getProxy().broadcast(
					new ComponentBuilder(evt.getPlayer().getName()+" left the game").color(ChatColor.GOLD).create());
		}
		if(rejoinLastServer){
			evt.getPlayer().setReconnectServer(evt.getPlayer().getServer().getInfo());
		}
	}
}