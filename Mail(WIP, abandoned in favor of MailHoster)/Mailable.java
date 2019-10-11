package Mail;

import java.util.UUID;
import EvLib.UUIDLookupUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Mailable{
	enum MailableType{ OFFLINE_PLAYER, PLAYER, ADMIN, SERVER, PROXY }
	String name, uuid;
	MailableType type;
	public String displayName(){return name;}
	public String uniqueName(){return uuid;}
	public MailableType getType(){return type;}
	private Mailable(String name, String uuid, MailableType type){
		this.name = name; this.uuid = uuid; this.type = type;
	}

	@Override public boolean equals(Object o){
		return o != null && o instanceof Mailable
				&& ((Mailable)o).uuid == uuid
				&& ((Mailable)o).type.equals(type);
	}

	@Override public String toString(){return uuid;}

	static boolean matchesAdmin(String name){
		final String[] adminAliases = new String[]{"admin", "staff", "op", "operator", "mod"};
		name = name.toLowerCase();
		for(String alias : adminAliases) if(name.equals(alias)) return true;
		return false;
	}
	public static Mailable getByName(String uniqueName, boolean forceUnique){
		ProxyServer proxy = ProxyServer.getInstance();
		if(uniqueName.equalsIgnoreCase(proxy.getName())){
			return new Mailable(proxy.getName(), proxy.getName(), MailableType.PROXY);
		}
		for(ServerInfo server : proxy.getServers().values()) if(uniqueName.equalsIgnoreCase(server.getName())){
			return new Mailable(server.getName(), server.getName(), MailableType.SERVER);
		}
		if(matchesAdmin(uniqueName)){
			return new Mailable("Admin", "admin", MailableType.ADMIN);
		}
		ProxiedPlayer player = null;
		if(!forceUnique) player = proxy.getPlayer(uniqueName);
		if(player == null){
			try{player = proxy.getPlayer(UUID.fromString(uniqueName));}
			catch(IllegalArgumentException ex){player = null;}
		}
		if(player != null){
			return new Mailable(player.getName(), player.getUniqueId().toString(), MailableType.PLAYER);
		}
		UUID uuid = UUIDLookupUtil.getUUID(uniqueName);
		if(uuid != null){
			return new Mailable(uniqueName, uuid.toString(), MailableType.OFFLINE_PLAYER);
		}
		return null;
	}
}