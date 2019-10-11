package Eventials.commands;

import java.util.ArrayList;
import EvLib.EvBungeeCommand;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandMailRead extends EvBungeeCommand{
	//TODO: click to open attachment, click to delete, click name to reply, hover timestamp for "time since sent"
	// ----- Page 1 of K -----
	//echo &8[&cx&8] &7(&64&cw&66&cd&611&ch&7)&a Name&7:&f message here &e<&bAttachment&e>
	//=> [x] (4w6d11h) Name: message here <Attachment>
	final int NUM_PAGES = 5; //TODO
	final String[] froms=new String[]{"bobo"},
					tos=new String[]{"ifriend"},
					withs=new String[]{"bobo","ifriend"};//TODO
	public CommandMailRead(String name, String permission, String aliases[]){
		super(name, permission, aliases);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args){
		ArrayList<String> completes = new ArrayList<String>();
		if(args.length == 1){
			completes.add("all");//TODO: non-paginated display?
			completes.add("sent");
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

	@Override
	public void execute(CommandSender sender, String[] args){
		//Read All (From/To/With) Name
		//Read Page (From/To/With) Name
		//Read (From/To/With) (Name) Page
		//Read (From/To/With) (Name) All
		//Read Sent (Page/All)
		//Read All Sent
		//Read<name> Page/Sent/All/From/To
	}
}
