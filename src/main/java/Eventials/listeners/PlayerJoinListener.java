package Eventials.listeners;

import java.util.concurrent.TimeUnit;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener{
	final private EventialsBungee plugin;
	public PlayerJoinListener(){
		plugin = EventialsBungee.getPlugin();
	}

	@EventHandler
	public void onPlayerJoin(ServerConnectedEvent evt){
//		if(!evt.getConnection().getName().equals("MCI"))
		if(evt.getPlayer().hasPermission("evbungee.silentjoin")) return;

		final String joinedServerName = evt.getServer().getInfo().getName();
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable(){@Override public void run(){
			for(ProxiedPlayer p : plugin.getProxy().getPlayers()){
				if(p.getName().equals(evt.getPlayer().getName())) continue;
	
				TranslatableComponent joinMsg = new TranslatableComponent("multiplayer.player.joined");
				if(p.getServer().getInfo().getName().equals(joinedServerName)){
					// bungee selectors dont work (boooo)
					//"@p[name="+evt.getPlayer().getName()+"]"
					//@p
					//evt.getPlayer().getUniqueId().toString()
//					joinMsg.addWith(new SelectorComponent(evt.getPlayer().getUniqueId().toString()));
//					p.sendMessage(new ComponentBuilder(joinMsg).color(ChatColor.YELLOW).create());
				}
				else{
					joinMsg.addWith(new TextComponent(evt.getPlayer().getDisplayName()));
					joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD+"Server: "+ChatColor.DARK_GREEN+joinedServerName)));
					p.sendMessage(new ComponentBuilder("["+joinedServerName.charAt(0)+"] ").color(ChatColor.GRAY).append(joinMsg).color(ChatColor.GRAY).create());
				}
			}
		}}, 2000, TimeUnit.MILLISECONDS);
	}
}