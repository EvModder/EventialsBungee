package Eventials;

import java.util.ArrayList;
import java.util.List;
import EvLib.EvBungeePlugin;
import Eventials.commands.*;
import Eventials.listeners.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class EventialsBungee extends EvBungeePlugin {
	private static EventialsBungee plugin; public static EventialsBungee getPlugin(){return plugin;}

	@Override
	public void onEvEnable() {
		plugin = this;
		getProxy().registerChannel("Eventials");//wat dis do

		//Listeners
		if(config.getBoolean("custom-ping"))
			getProxy().getPluginManager().registerListener(this, new ServerPingListener());
		if(config.getBoolean("global-join-message"))
			getProxy().getPluginManager().registerListener(this, new PlayerJoinListener());
		if(config.getBoolean("global-quit-message"))
			getProxy().getPluginManager().registerListener(this, new PlayerQuitListener());
		
		if(!config.getStringList("hide-tab-complete-commands-for-default").isEmpty()){
			try{
				Class.forName("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent");
				getProxy().getPluginManager().registerListener(this, new ProxySendCommandsListener());
			}
			catch(ClassNotFoundException e){}
		}

		//Commands
		if(config.getBoolean("enable-command-teleport")){
			List<String> command = config.getStringList("command-teleport");
			getProxy().getPluginManager().registerCommand(this, new CommandTeleport(command.remove(0), 
					"eventials.teleport", command.toArray(new String[command.size()])));
		}
		/*if(config.getBoolean("enable-command-mail")){
			List<String> command = config.getStringList("command-mail");
			getProxy().getPluginManager().registerCommand(this, new CommandMail(command.remove(0), 
					"eventials.mail", command.toArray(new String[command.size()])));
		}*/
		if(config.contains("server-aliases")){
			ArrayList<String> hiddenTabAliases = new ArrayList<>();
			Configuration serverAliases = config.getSection("server-aliases");
			for(final String serverName : serverAliases.getKeys()){
				List<String> aliases = serverAliases.getStringList(serverName);
				hiddenTabAliases.addAll(aliases);
				getProxy().getPluginManager().registerCommand(this, new Command(serverName,
						"bungeecord.command.server", new String[]{}/*aliases.toArray(new String[aliases.size()])*/){
					@Override public void execute(CommandSender sender, String[] args){
						//String command = "server " + String.join(" ", args);
						getProxy().getPluginManager().dispatchCommand(sender, "server "+serverName);
					}
				});
			}
			// Hide tab-completions for aliases
//			plugin.getProxy().getPluginManager().registerListener(plugin, new Listener(){
//				@EventHandler public void onTabCompleteEvent(TabCompleteEvent evt){
//					evt.getSuggestions().removeAll(hiddenTabAliases);
//				}
//			});
		}
	}
}