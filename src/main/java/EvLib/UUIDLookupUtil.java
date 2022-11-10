package EvLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UUIDLookupUtil{
	static final String mojangLookupURL = "https://api.mojang.com/users/profiles/minecraft/";
	static final ConcurrentHashMap<String, UUID> uuidFinder = new ConcurrentHashMap<String, UUID>();

	UUIDLookupUtil(){
		//TODO: Load cache
	}

	@SuppressWarnings("deprecation")
	static public UUID lookupUUID(String name, boolean original){
		try{
			//TODO: return properly capitalized name as well as UUID
			String uuid = (((JsonObject) new JsonParser().parse(
					new BufferedReader(new InputStreamReader(
						new URL(mojangLookupURL + name + (original ? "?at=0" : "")).openStream(), "UTF-8")))
					).get("id")).getAsString();
			if(uuid != null) UUID.fromString(uuid.replaceFirst(
					"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
					"$1-$2-$3-$4-$5"));
			return null;
		}
		catch(MalformedURLException e) {
			System.out.println("Failed to get uuid because of a malformed url!");
			System.out.println("Please report this to the developer: evdoc@altcraft.net");
			System.out.println("Name: \"" + name + "\"");
			e.printStackTrace();
		}
		catch(IOException e) {
			System.out.println("Looks like there is a problem with the connection with mojang. Please retry later.");
			if(e.getMessage().contains("HTTP response code: 429")) {
				System.out.println("You have reached the request limit of the mojang api! Please retry later!");
			}
			e.printStackTrace();
		}
		return null;
	}

	static public UUID getUUID(String name){
		UUID uuid = uuidFinder.get(name.toLowerCase());
		if(uuid != null) return uuid;
		uuid = lookupUUID(name, false);
		if(uuid == null) uuid = lookupUUID(name, false);
		if(uuid != null) uuidFinder.put(name.toLowerCase(), uuid);
		return uuid;
	}

	static public void cacheUUID(String name, UUID uuid){
		uuidFinder.put(name.toLowerCase(), uuid);
	}
}