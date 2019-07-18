package EvLib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FileIO{
	public static final String DIR = "./plugins/EvFolder/";

	public static String loadFile(String filename, InputStream defaultValue){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(DIR + filename));
		}
		catch(FileNotFoundException e){
			if(defaultValue == null) return null;

			// Create Directory
			File dir = new File(DIR);
			if(!dir.exists()) dir.mkdir();

			// Create the file
			File conf = new File(DIR + filename);
			StringBuilder builder = new StringBuilder();
			String content = null;
			try{
				conf.createNewFile();
				reader = new BufferedReader(new InputStreamReader(defaultValue));

				String line = reader.readLine();
				builder.append(line);
				while(line != null){
					builder.append('\n').append(line);
					line = reader.readLine();
				}
				reader.close();

				BufferedWriter writer = new BufferedWriter(new FileWriter(conf));
				writer.write(content = builder.toString());
				writer.close();
			}
			catch(IOException e1){
				e1.printStackTrace();
			}
			return content;
		}
		StringBuilder file = new StringBuilder();
		if(reader != null) {
			try{
				String line = reader.readLine();
				while(line != null){
					line = line.trim().replace("//", "#");
					int cut = line.indexOf('#');
					if(cut == -1) file.append('\n').append(line);
					else if(cut > 0) file.append('\n').append(line.substring(0, cut).trim());
					line = reader.readLine();
				}
				reader.close();
			}
			catch(IOException e){}
		}
		return file.length() == 0 ? "" : file.substring(1);
	}

	public static String loadFile(String filename, String defaultContent){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(DIR + filename));
		}
		catch(FileNotFoundException e){
			if(defaultContent == null || defaultContent.isEmpty()) return defaultContent;

			// Create Directory
			File dir = new File(DIR);
			if(!dir.exists()) dir.mkdir();

			// Create the file
			File conf = new File(DIR + filename);
			try{
				conf.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(conf));
				writer.write(defaultContent);
				writer.close();
			}
			catch(IOException e1){
				e1.printStackTrace();
			}
			return defaultContent;
		}
		StringBuilder file = new StringBuilder();
		if(reader != null) {
			try{
				String line;
				while((line = reader.readLine()) != null){
					line = line.trim().replace("//", "#");
					int cut = line.indexOf('#');
					if(cut == -1) file.append('\n').append(line);
					else if(cut > 0) file.append('\n').append(line.substring(0, cut).trim());
				}
				reader.close();
			}
			catch(IOException e){}
		}
		return file.length() == 0 ? "" : file.substring(1);
	}

	public static boolean saveFile(String filename, String content){
		return saveFile(filename, content, false);
	}
	public static boolean saveFile(String filename, String content, boolean genDirs){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(DIR + filename));
			writer.write(content);
			writer.close();
			return true;
		}
		catch(IOException e){
			return false;
		}
	}

	public static String loadResource(Object pl, String filename){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(pl.getClass().getResourceAsStream("/" + filename)));

			StringBuilder file = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null){
				line = line.trim().replace("//", "#");
				int cut = line.indexOf('#');
				if(cut == -1) file.append('\n').append(line);
				else if(cut > 0) file.append('\n').append(line.substring(0, cut).trim());
			}
			reader.close();
			return file.substring(1);
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return "";
	}

	public static boolean deleteFile(String filename){
		File file = new File(DIR+filename);
		return file.exists() && file.delete();
	}

	public static Configuration loadConfig(Plugin pl, String configName, InputStream defaultConfig){
		if(!configName.endsWith(".yml")) {
			pl.getLogger().severe("Invalid config file!");
			pl.getLogger().severe("Configuation files must end in .yml");
			return null;
		}
		File file = new File("./plugins/EvFolder/" + configName);
		if(!file.exists()) {
			pl.getLogger().info("Could not locate configuration file!");
			pl.getLogger().info("Generating a new one with default settings.");

			if(defaultConfig != null) {
				try{
					// Create Directory
					File dir = new File("./plugins/EvFolder");
					if(!dir.exists()) dir.mkdir();

					// Load contents of default config
					BufferedReader reader = new BufferedReader(new InputStreamReader(defaultConfig));
					String line = reader.readLine();
					StringBuilder builder = new StringBuilder(line);
					while((line = reader.readLine()) != null) builder.append('\n').append(line);
					reader.close();

					//Write default config content to new config
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(builder.toString());
					writer.close();

					return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
				}
				catch(IOException ex){
					pl.getLogger().severe(ex.getStackTrace().toString());
				}
			}
			else{
				pl.getLogger().severe("Unable to locate a default config!");
				pl.getLogger().severe("Could not find /config.yml in plugin's .jar");
			}
		}
		else{
			try{
				return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
}