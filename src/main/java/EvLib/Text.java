package EvLib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Text {
	static char colorSymbol = ChatColor.WHITE.toString().charAt(0);
	static Character[] SET_VALUES = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
										  'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};
	public static final Set<Character> colorChars = new HashSet<Character>(Arrays.asList(SET_VALUES));
	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate){
		char[] msg = textToTranslate.toCharArray();
		for(int i = 1; i < msg.length; ++i){
			if(msg[i-1] == altColorChar && colorChars.contains(msg[i]) && (i == 1 || msg[i-2] != '\\')){
				msg[i-1] = colorSymbol;
			}
		}
		return new String(msg);
	}

	public static LinkedList<String> toListFromString(String string){
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(Arrays.asList(string.substring(1, string.lastIndexOf(']')).split(", ")));
		if(list.size() == 1 && list.get(0).isEmpty()) list.clear();
		return list;
	}

	public static TextComponent parseBBCode(String str){
		return new TextComponent(ChatColor.translateAlternateColorCodes('&', str));
	}
}
