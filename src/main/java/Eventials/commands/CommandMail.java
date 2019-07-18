package Eventials.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import EvLib.EvBungeeCommand;
import Eventials.EventialsBungee;
import Eventials.MailUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.util.CaseInsensitiveMap;

public class CommandMail extends EvBungeeCommand{
	final EventialsBungee plugin;
	final MailUtils mailer;
//	String consoleName, adminName;

	public CommandMail(String name, String permission, String aliases[]){
		super(name, permission, aliases);
		plugin = EventialsBungee.getPlugin();
		mailer = new MailUtils(plugin);
		addSubCommand(new CommandBase("server", "eventials.mail.admin", this::commandReload));
		addSubCommand(new CommandHide());
		addSubCommand(new CommandFakePlayers());
		addSubCommand(new CommandBase("status", null, this::commandStatus));
		addSubCommand(new CommandBase("help", null, this::commandHelp, "?"));
		List<String> readAliases = plugin.getConfig().getStringList("mail-read-aliases");
		addSubCommand(new EvBungeeCommand("read", null, readAliases.toArray(new String[readAliases.size()])){
			@Override
			public Iterable<String> onTabComplete(CommandSender sender, String[] args){
		});
		addSubCommand(new CommandBase("read", null, this::commandReadMail,
				readAliases.toArray(new String[readAliases.size()])));
		setDefaultAction(this::commandHelp);
//		consoleName = plugin.getConfig().getString("mail-server-name", "@Console");
//		adminName = plugin.getConfig().getString("mail-admin-name", "@Admin");
//		if(adminName.charAt(0) != '@') adminName = "@"+adminName;
	}

	private void commandHelp(CommandSender sender){
		
	}

	private void commandReadMail(CommandSender sender){
		
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args){
		if(args.length == 1) {
			return subCommands.keySet().stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());
		}
		else if(args.length > 1) {
			Command command = subCommands.get(args[0]);
			if(command != null && command instanceof TabExecutor) {
				if(command.getPermission() == null || sender.hasPermission(command.getPermission())) {
					((TabExecutor)command).onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
				}
			}
		}
		return Collections.emptyList();
	}

	public void execute(CommandSender sender, String[] args){
		sender.sendMessage(new TextComponent(ChatColor.GRAY
				+"This command has not been fully implemented yet\nIn the meantime, use /email"));
		if(args.length < 10) return;

//		String myUUID = mailer.getUUID(sender);

		//cmd:	/mail [read/inbox/send/write/sent/clear/delete]
		//args[0]: read/inbox/<no args>, send, sent
		/* Examples:
		 * /mail send EvDoc hey please send me a test mail
		 * /mail read => '[EvDoc] hi this is a test mail'
		 * /mail sent => '[Me->EvDoc] hey please send me a test mail'
		 *
		 * Feel free to deviate from format in any/every way.
		 * Other (extra) ideas: Timestamps, conversation chains, multiple recipients/senders
		 */
		if(args.length > 1 && (args[1]=args[1].toLowerCase()).equals("all")) args[1] = "@a";

		else if(args.length == 0 || (args[0]=args[0].toLowerCase()).equals("read")){
			if(!sender.hasPermission("eventials.mail.read")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			String target = "";
			if(args.length > 1){
				target = args[1];
				if(target.equals("sent")){
					execute(sender, Arrays.copyOfRange(args, 1, args.length));
					return;
				}
				//TODO: if(target.equals("sentas")){}
				for(int i=2; i<args.length; ++i) target += ","+args[i];
				target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");//del begin/trailing, replace 2+ w/ 1
			}
			if(target.isEmpty() || target.equals("all")) target = "@a";
//			else if(target.equals("admin")) target = mailer.ADMIN_NAME;
			mailer.readInbox(sender, target);
		}
/*		else if(args[0].equals("readas")){
			if(!sender.hasPermission("eventials.mail.readas")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			if(args.length == 1){
				sender.sendMessage(new TextComponent(ChatColor.RED
						+"Please specify whose mail you would like to read"));
			}
			String target = "";
			for(int i=2; i<args.length; ++i){target += args[i]+",";}
			if(target.startsWith("sent")) target = target.substring(4).toLowerCase();
			target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");

//			if(target.isEmpty() || target.equals("all")) target = "@a";
//			else if(target.equals("admin")) target = mailer.ADMIN_NAME;
			mailer.readInbox(args[1], target);
		}
		else if(args[0].equals("sentas")){
			if(!sender.hasPermission("eventials.mail.read.sent")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			String target = "";
			for(int i=1; i<args.length; ++i){target += args[i]+","; }
			target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");

//			if(target.isEmpty() || target.equals("all")) target = "@a";
//			else if(target.equals("admin")) target = mailer.ADMIN_NAME;
			mailer.readSent(args[1], target);
		}*/
		else if(args[0].equals("sent")){
			if(!sender.hasPermission("eventials.mail.read.sent")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			String target = "";
			for(int i=1; i<args.length; ++i) target += args[i]+",";
			target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");

//			if(target.isEmpty() || target.equals("all")) target = "@a";
//			else if(target.equals("admin")) target = mailer.ADMIN_NAME;
			mailer.readSent(sender, target);
		}
		else if(args[0].equals("send") || args[0].equals("write")){
			if(!sender.hasPermission("eventials.mail.send")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			String target = "";
			args[1] = args[1]+",";
			int i = 1;
			for(; i<args.length && args[i].endsWith(","); ++i) target += args[i];
			target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");

			Collection<String> targets;
			if(target.isEmpty()){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Please specify a recipient"));
				if(sender.hasPermission("eventials.mail.sendall"))
					sender.sendMessage(new TextComponent(ChatColor.RED+"Use @a to target all players"));
			}
			else if(i == args.length){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Please supply a message"));
			}
			else if((targets = mailer.getTargets(target)).isEmpty()){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Unable to find the specified recipient(s)"));
			}
			else{
//				if(target.equals("all")) target = "@a";
//				else if(target.equals("admin")) target = mailer.ADMIN_NAME;
				String message = args[i];
				while(++i < args.length) message += " " + args[i];
				mailer.sendMail(sender, targets, message);
			}
		}
/*		else if(args[0].equals("sendas") || args[0].equals("writeas")){
			if(!sender.hasPermission("eventials.mail.read.sendas")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			if(args.length == 1){
				sender.sendMessage(new TextComponent(ChatColor.RED
						+"Please specify who you are sending mail for"));
			}
			String target = "";
			int i=2;
			while(i < args.length){target += args[i]; if(!args[i++].endsWith(",")) break;}
			target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");

			if(target.isEmpty()){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Please specify a recipient"));
				if(sender.hasPermission("eventials.mail.sendall"))
					sender.sendMessage(new TextComponent(ChatColor.RED+"Use @a to target all players"));
			}
			else if(i == args.length){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Please supply a message"));
			}
			else{
				if(target.equals("all")) target = "@a";
				else if(target.equals("admin")) target = mailer.ADMIN_NAME;
				String message = args[i];
				while(++i < args.length) message += " " + args[i];
				mailer.sendMail(args[1], target, message);
			}
		}*/
		else if(args[0].equals("clear") || args[0].equals("delete")){
			if(!sender.hasPermission("eventials.mail.read")){
				sender.sendMessage(new TextComponent(ChatColor.RED+"Insufficient permission :("));
				return;
			}
			String target = "";
			if(args.length > 1){
				target = args[1];
				for(int i=1; i<args.length; ++i) target += args[i]+",";
				target = target.replaceAll("^,+|,+$", "").replaceAll(",{2,}", ",");
			}

//			if(target.isEmpty() || target.equals("all")) target = "@a";
//			else if(target.equals("admin")) target = mailer.ADMIN_NAME;
			if(target.startsWith("sent")){
				mailer.clearSent(sender, target.substring(5));
			}
			else mailer.clearInbox(sender, target, false);
		}
		else{
			sender.sendMessage(new ComponentBuilder("Invalid command format").color(ChatColor.RED).create());
		}
	}
}