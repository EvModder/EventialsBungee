package EvLib;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.util.CaseInsensitiveMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EvBungeeCommand extends Command implements TabExecutor{
	public static Consumer<CommandSender> playerCommand(Consumer<ProxiedPlayer> cmd){
		return sender -> {
			if(sender instanceof ProxiedPlayer) cmd.accept(((ProxiedPlayer)sender));
			else sender.sendMessage(Text.parseBBCode("&cThis command can only be used ingame."));
		};
	}
	public class CommandBase extends Command{
		private final BiConsumer<CommandSender, String[]> action;

		public CommandBase(String name, String permission, Consumer<CommandSender> action, String... aliases){
			this(name, permission, (a, b) -> action.accept(a), aliases);
		}
		public CommandBase(String name, String permission, BiConsumer<CommandSender, String[]> action, String... aliases){
			super(name, permission, aliases);
			this.action = action;
		}

		@Override public void execute(CommandSender sender, String[] args){
			action.accept(sender, args);
		}
	}

	private final Map<String, Command> subCommands = new CaseInsensitiveMap<>();
	private Consumer<CommandSender> defaultAction = null;

	public EvBungeeCommand(String name){
		super(name);
	}

	public EvBungeeCommand(String name, String permission, String... aliases){
		super(name, permission, aliases);
	}

	public void addSubCommand(Command command){
		subCommands.put(command.getName(), command);
		for(String alias : command.getAliases()) subCommands.put(alias, command);
	}

	public void setDefaultAction(Consumer<CommandSender> defaultAction){
		this.defaultAction = defaultAction;
	}

	@Override
	public void execute(CommandSender sender, String[] args){
		if(args.length > 0 && subCommands.containsKey(args[0])){
			Command command = subCommands.get(args[0]);
			if(command.getPermission() == null || sender.hasPermission(command.getPermission())){
				command.execute(sender, Arrays.copyOfRange(args, 1, args.length));
			}
			else{
				sender.sendMessage(new TextComponent(ProxyServer.getInstance().getTranslation("no_permission")));
			}
		}
		else if(defaultAction != null){
			defaultAction.accept(sender);
		}
		else{
			sender.sendMessage(Text.parseBBCode("&cIncorrect usage!"));
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args){
		if(args.length == 1){
			return subCommands.keySet().stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());
		}
		else if(args.length > 1){
			Command command = subCommands.get(args[0]);
			if(command != null && command instanceof TabExecutor){
				if(command.getPermission() == null || sender.hasPermission(command.getPermission())){
					((TabExecutor)command).onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
				}
			}
		}
		return Collections.emptyList();
	}
}