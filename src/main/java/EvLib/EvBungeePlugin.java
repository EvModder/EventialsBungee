package EvLib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public abstract class EvBungeePlugin extends Plugin{
	protected Configuration config; public Configuration getConfig(){return config;}
	public void saveConfig(){
		try{
			ConfigurationProvider.getProvider(YamlConfiguration.class)
				.save(config, new File("./plugins/EvFolder/config-"+getDataFolder().getName()+".yml"));
		}
		catch(IOException e){e.printStackTrace();}
	}
	public void reloadConfig(){
		InputStream defaultConfig = getClass().getResourceAsStream("/config.yml");
		if(defaultConfig != null) config = FileIO.loadConfig(this, "config-"+getDataFolder().getName()+".yml", defaultConfig);
	}
	
	@Override public final void onEnable(){
//		getLogger().info("Loading " + getDescription().getFullName());
//		new Updater(this, projectID, this.getFile(), Updater.UpdateType.DEFAULT, false);
		reloadConfig();
		onEvEnable();
	}
	
	@Override public final void onDisable(){
		onEvDisable();
	}
	
	public void onEvEnable(){}
	public void onEvDisable(){}
}
