package Eventials.listeners;

import java.util.ArrayList;
import Eventials.EventialsBungee;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxySendCommandsListener implements Listener{
	private final ArrayList<String> hiddenCmds;

	public ProxySendCommandsListener(){
		hiddenCmds = new ArrayList<>(EventialsBungee.getPlugin().getConfig().getStringList("hide-tab-complete-commands-for-default"));
	}

	@EventHandler public void onProxySendCommands(ProxyDefineCommandsEvent evt){
		//TODO: only apply this filtering for players in the default group
		for(String cmdName : hiddenCmds) evt.getCommands().remove(cmdName);
	}
}