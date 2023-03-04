package Eventials.listeners;

import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener{
	final private EventialsBungee plugin;
	final boolean REJOIN_LAST_SERVER;

	public PlayerQuitListener(){
		plugin = EventialsBungee.getPlugin();
		REJOIN_LAST_SERVER = plugin.getConfig().getBoolean("rejoin-last-server", true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent evt){
		final String quitServerName = evt.getPlayer().getServer().getInfo().getName();
		if(REJOIN_LAST_SERVER) evt.getPlayer().setReconnectServer(evt.getPlayer().getServer().getInfo());
		if(evt.getPlayer().hasPermission("evbungee.silentquit")) return;

		for(ProxiedPlayer p : plugin.getProxy().getPlayers()){
			if(p.getName().equals(evt.getPlayer().getName())) continue;

			TranslatableComponent joinMsg = new TranslatableComponent("multiplayer.player.left");
			if(p.getServer().getInfo().getName().equals(quitServerName)){
				// bungee selectors don't work (boooo)
//				joinMsg.addWith(new SelectorComponent(evt.getPlayer().getUniqueId().toString()));
//				p.sendMessage(new ComponentBuilder(joinMsg).color(ChatColor.YELLOW).create());
			}
			else{
				joinMsg.addWith(new TextComponent(evt.getPlayer().getDisplayName()));
				joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD+"Server: "+ChatColor.DARK_GREEN+quitServerName)));
				p.sendMessage(new ComponentBuilder("["+quitServerName.charAt(0)+"] ").color(ChatColor.GRAY).append(joinMsg).color(ChatColor.GRAY).create());
			}
		}
	}
}