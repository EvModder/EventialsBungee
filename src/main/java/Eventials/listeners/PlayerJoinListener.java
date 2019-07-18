package Eventials.listeners;

import EvLib.UUIDLookupUtil;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {
	EventialsBungee plugin;
	UUIDLookupUtil uuidFinder;
	public PlayerJoinListener(){
		plugin = EventialsBungee.getPlugin();
		uuidFinder = UUIDLookupUtil.getInstance();
	}

	@EventHandler
	public void onPlayerJoin(PostLoginEvent evt) {
//		if(!evt.getConnection().getName().equals("MCI"))

		if(!evt.getPlayer().hasPermission("evbungee.silentjoin")){
			for(ProxiedPlayer p : plugin.getProxy().getPlayers()){
				if(!p.getName().equals(evt.getPlayer().getName())){
					p.sendMessage(new ComponentBuilder(
							evt.getPlayer().getName()+" joined the game").color(ChatColor.GOLD).create());
				}
			}
		}
		uuidFinder.cacheUUID(evt.getPlayer().getName(), evt.getPlayer().getUniqueId());
	}
}