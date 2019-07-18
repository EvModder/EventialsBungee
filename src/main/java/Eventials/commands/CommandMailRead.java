package Eventials.commands;

import java.util.ArrayList;
import EvLib.EvBungeeCommand;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandMailRead extends EvBungeeCommand{
	final int NUM_PAGES = 5; //TODO
	final String[] froms=new String[]{"bobo"}, tos=new String[]{"ifriend"}, withs=new String[]{"bobo","ifriend"};//TODO
	public CommandMailRead(String name, String permission, String aliases[]){
		super(name, permission, aliases);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args){
		ArrayList<String> completes = new ArrayList<String>();
		if(args.length == 1){
			for(int i=1; i<=NUM_PAGES; ++i) completes.add(""+i);
			for(String fromP : froms) completes.add("from "+fromP);
			for(String toP : tos) completes.add("to "+toP);
			for(String withP : withs) completes.add("with "+withP);
			if(sender.hasPermission("eventials.mail.read.others")){
				for(ProxiedPlayer p : EventialsBungee.getPlugin().getProxy().getPlayers()){
					completes.add(p.getName());
				}
			}
			return completes;
		}
		else if(args.length == 2){
			args[1] = args[1].toLowerCase();
			if(args[1].equals("from")) for(String fromP : froms) completes.add(fromP);
			else if(args[1].equals("to")) for(String toP : tos) completes.add(toP);
			else if(args[1].equals("with")) for(String withP : withs) completes.add(withP);
		}
		return completes;
	}
}
