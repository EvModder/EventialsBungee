package Eventials.chat_UNUSED;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class BBCodeChatParser implements ChatParser {
	private static final Pattern pattern = Pattern.compile(
			"(?is)(?=\\n)|(?:[&\247](?<color>[0-9A-FK-OR]))|(?:\\[(?<tag>/?(?:b|i|u|s|nocolor" +
			"|nobbcode)|(?:url|command|hover|suggest|color)=(?<value>(?:(?:[^]\\[]*)\\[(?:[^]" +
			"\\[]*)\\])*(?:[^]\\[]*))|/(?:url|command|hover|suggest|color))\\])|(?:\\[(?<impl" +
			"icitTag>url|command|suggest)\\](?=(?<implicitValue>.*?)\\[/\\k<implicitTag>\\]))"
	);
	private static final Pattern strip_bbcode_pattern = Pattern.compile(
			"(?is)(?:\\[(?<tag>/?(?:b|i|u|s|nocolor|nobbcode)|(?:url|command|hover|suggest|co" +
			"lor)=(?<value>(?:(?:[^]\\[]*)\\[(?:[^]\\[]*)\\])*(?:[^]\\[]*))|/(?:url|command|h" +
			"over|suggest|color))\\])|(?:\\[(?<implicitTag>url|command|suggest)\\](?=(?<impli" +
			"citValue>.*?)\\[/\\k<implicitTag>\\]))"
	);

	@SuppressWarnings("deprecation")
	public BaseComponent[] parse(String text) {
		Matcher matcher = pattern.matcher(text);
		TextComponent current = new TextComponent();
		List<TextComponent> components = new LinkedList<TextComponent>();
		int forceBold = 0, forceItalic = 0, forceUnderlined = 0, forceStrikethrough = 0;
		int nocolorLevel = 0, nobbcodeLevel = 0;
		Deque<ChatColor> colorDeque = new LinkedList<ChatColor>();
		Deque<ClickEvent> clickEventDeque = new LinkedList<ClickEvent>();
		Deque<HoverEvent> hoverEventDeque = new LinkedList<HoverEvent>();
		while(matcher.find()) {
			boolean parsed = false;
			StringBuffer stringBuffer = new StringBuffer();
			matcher.appendReplacement(stringBuffer, "");
			TextComponent component = new TextComponent(current);
			current.setText(stringBuffer.toString());
			components.add(current);
			current = component;
			String group_color = matcher.group("color");
			String group_tag = matcher.group("tag");
			String group_value = matcher.group("value");
			String group_implicitTag = matcher.group("implicitTag");
			String group_implicitValue = matcher.group("implicitValue");
			if(group_color != null && nocolorLevel <= 0) {
				char color = group_color.charAt(0);
				switch(group_color.charAt(0)) {
					case 'k': // '\001'
						current.setObfuscated(true);
						break;
					case 'l': // '\002'
						current.setBold(true);
						break;
					case 'n': // '\003'
						current.setStrikethrough(true);
						break;
					case 'm': // '\004'
						current.setUnderlined(true);
						break;
					case 'i': // '\005'
						current.setItalic(true);
						break;
					case 'r': // '\006'
						color = 'f';
						// fall through
					default:
						current = new TextComponent();
						current.setColor(ChatColor.getByChar(color));
						current.setBold(Boolean.valueOf(forceBold > 0));
						current.setItalic(Boolean.valueOf(forceItalic > 0));
						current.setUnderlined(Boolean.valueOf(forceUnderlined > 0));
						current.setStrikethrough(Boolean.valueOf(forceStrikethrough > 0));
						
						if(!colorDeque.isEmpty()) current.setColor((ChatColor)colorDeque.peek());
						
						if(!clickEventDeque.isEmpty()) current.setClickEvent((ClickEvent)clickEventDeque.peek());
						
						if(!hoverEventDeque.isEmpty()) current.setHoverEvent((HoverEvent)hoverEventDeque.peek());
						
						break;
					}
					parsed = true;
			}
			if(group_tag != null && nobbcodeLevel <= 0) {
				if(group_tag.matches("(?is)^b$")) {
					
					current.setBold(++forceBold > 0);
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/b$")) {
					current.setBold(--forceBold > 0);
					parsed = true;
				}
				if(group_tag.matches("(?is)^i$")){
					current.setItalic(++forceItalic > 0);
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/i$")) {
					current.setItalic(--forceItalic > 0);
					parsed = true;
				}
				if(group_tag.matches("(?is)^u$")){
					current.setUnderlined(++forceUnderlined > 0);
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/u$")) {
					current.setUnderlined(--forceUnderlined > 0);
					parsed = true;
				}
				if(group_tag.matches("(?is)^s$")) {
					current.setStrikethrough(++forceStrikethrough > 0);
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/s$")){
					current.setStrikethrough(--forceStrikethrough > 0);
					parsed = true;
				}
				if(group_tag.matches("(?is)^color=.*$")) {
					ChatColor color = null;
					for(ChatColor c : ChatColor.values()) {
						if(c.getName().equalsIgnoreCase(group_value)) color = c;
					}

					colorDeque.push(current.getColor());
					if(color != null && color != ChatColor.BOLD && color != ChatColor.ITALIC && color != ChatColor.MAGIC
							&& color != ChatColor.RESET && color != ChatColor.STRIKETHROUGH && color != ChatColor.UNDERLINE)
					{
						colorDeque.push(color);
						current.setColor(color);
					}
					else {
						colorDeque.push(ChatColor.WHITE);
						current.setColor(ChatColor.WHITE);
					}
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/color$")) {
					if(!colorDeque.isEmpty()) {
						colorDeque.pop();
						current.setColor((ChatColor)colorDeque.pop());
					}
					parsed = true;
				}
				if(group_tag.matches("(?is)^url=.*$")) {
					String url = group_value;
					url = url.replaceAll("(?is)\\[/?nobbcode\\]", "");
					if(!url.startsWith("http")) url = (new StringBuilder()).append("http://").append(url).toString();
					
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
				if(group_tag.matches("(?is)^/(?:url|command|suggest)$")) {
					if(!clickEventDeque.isEmpty()) clickEventDeque.pop();
					
					current.setClickEvent(clickEventDeque.isEmpty() ? null : clickEventDeque.peek());
					parsed = true;
				}
				if(group_tag.matches("(?is)^command=.*")) {
					group_value = group_value.replaceAll("(?is)\\[/?nobbcode\\]", "");
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, group_value);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
				if(group_tag.matches("(?is)^suggest=.*")) {
					group_value = group_value.replaceAll("(?is)\\[/?nobbcode\\]", "");
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, group_value);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
				if(group_tag.matches("(?is)^hover=.*$")) {
					BaseComponent components1[] = parse(group_value);
					if(!hoverEventDeque.isEmpty()) {
						BaseComponent components2[] = hoverEventDeque.getLast().getValue();
						BaseComponent components3[] = new BaseComponent[components1.length + components2.length + 1];
						
						int i=0;
						for(BaseComponent baseComponent : components2) components3[i++] = baseComponent;

						components3[i++] = new TextComponent("\n");
						
						for(BaseComponent baseComponent : components1) components3[i++] = baseComponent;

						components1 = components3;
					}
					HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, components1);
					hoverEventDeque.push(hoverEvent);
					current.setHoverEvent(hoverEvent);
					parsed = true;
				}
				else if(group_tag.matches("(?is)^/hover$")) {
					if(!hoverEventDeque.isEmpty()) hoverEventDeque.pop();
					
					current.setHoverEvent(hoverEventDeque.isEmpty() ? null : hoverEventDeque.peek());
					
					parsed = true;
				}
			}
			if(group_implicitTag != null && nobbcodeLevel <= 0) {
				if(group_implicitTag.matches("(?is)^url$")) {
					String url = group_implicitValue;
					if(!url.startsWith("http")) url = (new StringBuilder()).append("http://").append(url).toString();
					
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
				if(group_implicitTag.matches("(?is)^command$")) {
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, group_implicitValue);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
				if(group_implicitTag.matches("(?is)^suggest$")) {
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, group_implicitValue);
					clickEventDeque.push(clickEvent);
					current.setClickEvent(clickEvent);
					parsed = true;
				}
			}
			if(group_tag != null) {
				if(group_tag.matches("(?is)^nocolor$")) {
					++nocolorLevel;
					parsed = true;
				}
				if(group_tag.matches("(?is)^/nocolor$")) {
					--nocolorLevel;
					parsed = true;
				}
				if(group_tag.matches("(?is)^nobbcode$")) {
					++nobbcodeLevel;
					parsed = true;
				}
				if(group_tag.matches("(?is)^/nobbcode$")) {
					--nobbcodeLevel;
					parsed = true;
				}
			}
			if(!parsed) {
				TextComponent comp = new TextComponent(current);
				current.setText(matcher.group(0));
				components.add(current);
				current = comp;
			}
		}//while
		StringBuffer stringBuffer = new StringBuffer();
		matcher.appendTail(stringBuffer);
		current.setText(stringBuffer.toString());
		components.add(current);
		return (BaseComponent[])components.toArray(new BaseComponent[components.size()]);
	}

	public static String stripBBCode(String string)
	{
		return strip_bbcode_pattern.matcher(string).replaceAll("");
	}
}
