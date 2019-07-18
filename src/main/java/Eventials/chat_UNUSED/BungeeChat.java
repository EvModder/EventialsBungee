package Eventials.chat_UNUSED;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import Eventials.EventialsBungee;

public class BungeeChat {
	EventialsBungee plugin;
	private ChatParser chatParser;

	public BungeeChat(){
		plugin = EventialsBungee.getPlugin();
		chatParser = new BBCodeChatParser();
	}

	public void globalBroadcast(String message) {
		plugin.getProxy().broadcast(chatParser.parse(replaceRegex(message)));
	}

	public String replaceRegex(String str) {
		if(plugin.getConfig().getList("regex") == null) return str;
		
		for(Object entry : plugin.getConfig().getList("regex")) {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>)entry;
			str = str.replaceAll(map.get("search"), map.get("replace"));
		}
		return str;
	}

	public String preparePlayerChat(String text, ProxiedPlayer player) {
		if(!player.hasPermission("evbungeechat.chat.color")) {
			text = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));
		}
		if(!player.hasPermission("evbungeechat.chat.bbcode")) {
			text = BBCodeChatParser.stripBBCode(text);
		}
		return text;
	}
}