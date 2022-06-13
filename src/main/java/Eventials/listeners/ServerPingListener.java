package Eventials.listeners;

//import org.apache.commons.jexl3.JexlBuilder;
//import org.apache.commons.jexl3.JexlEngine;
//import org.apache.commons.jexl3.JexlExpression;
//import org.apache.commons.jexl3.MapContext;

import EvLib.Text;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.ServerPing.*;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerPingListener implements Listener{
	EventialsBungee plugin;
//	JexlExpression maxPlayerCount, onlinePlayerCount;
	PlayerInfo[] sample;

	public ServerPingListener(){
		plugin = EventialsBungee.getPlugin();
//		JexlEngine jexl = new JexlBuilder().cache(256).silent(true).create();
//		
//		maxPlayerCount = jexl.createExpression(plugin.getConfig().getString("max-player-count").toLowerCase());
//		onlinePlayerCount = jexl.createExpression(plugin.getConfig().getString("online-player-count").toLowerCase());

		String[] hoverMsg = plugin.getConfig().getString("hover-message", "").split("\n");
		sample = new PlayerInfo[hoverMsg.length];
		for(int i = 0; i < sample.length; ++i){
			sample[i] = new PlayerInfo(Text.translateAlternateColorCodes('&', hoverMsg[i]), ""+i);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onProxyPing(ProxyPingEvent evt){
		if(evt.getResponse() != null && evt.getResponse().getPlayers() != null){
			//plugin.getLogger().info("PingedProxy");
			//plugin.getLogger().info("Description: "+evt.getResponse().getDescription());
			//plugin.getLogger().info("Players: "+evt.getResponse().getPlayers().getOnline());
			evt.getResponse().getPlayers().setOnline(plugin.getProxy().getOnlineCount());
		}
	}

	/*@EventHandler(priority = EventPriority.HIGH)
	public void onServerPing(ServerPing evt){
		if(evt.getPlayers() != null){
			plugin.getLogger().info("PingedServer");
			//plugin.getLogger().info("Description: "+evt.getDescription());
			//plugin.getLogger().info("Players: "+evt.getPlayers().getOnline());
			evt.getPlayers().setOnline(plugin.getProxy().getOnlineCount());
		}
//		ServerInfo target = plugin.getProxy().getServerInfo("");
//		MapContext context = new MapContext();
//		context.set("sum", plugin.getProxy().getPlayers().size());
//		context.set("server", evt.getPlayers().getOnline());
//		int max = ((Number)maxPlayerCount.evaluate(context)).intValue();
//		int online = ((Number)onlinePlayerCount.evaluate(context)).intValue();

//		Callback<ServerPing> callback = new Callback<ServerPing>(){
//			@Override public void done(ServerPing result, Throwable error){
//			}
//		};
//		target.ping(callback);
//
//		evt.setDescriptionComponent(new TextComponent(target.getMotd()));
//		evt.getPlayers().setMax(25);
//		evt.getPlayers().setOnline(plugin.getProxy().getOnlineCount());
//		evt.getPlayers().setSample(sample);
	}*/
}
