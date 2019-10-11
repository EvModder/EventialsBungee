package Mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import EvLib.FileIO;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MailManager{
	final String MAIL_DIR;
	public MailManager(){
		MAIL_DIR = FileIO.DIR+"/mail/";
		File mailDir = new File(MAIL_DIR);
		if(!mailDir.exists()) mailDir.mkdir();
		EventialsBungee pl = EventialsBungee.getPlugin();
		pl.getProxy().getPluginManager().registerListener(pl, new Listener(){
			@EventHandler
			public void onPlayerJoin(PostLoginEvent evt) {
//				if(!evt.getConnection().getName().equals("MCI"))
				try{if(new File(MAIL_DIR+evt.getPlayer().getUniqueId()+".txt").createNewFile()){
					pl.getLogger().info("Created a mail file for "+evt.getPlayer().getName());
				}}
				catch(IOException e){e.printStackTrace();}
			}
		});
	}

	public void saveMail(Mail mail){
		if(new File(MAIL_DIR+mail.getTo().uniqueName()+".txt").exists())
			FileIO.appendFile("mail/"+mail.getTo().uniqueName()+".txt", "\n"+mail.toString());
		else
			FileIO.saveFile("mail/"+mail.getTo().uniqueName()+".txt", mail.toString());
		if(new File(MAIL_DIR+mail.getFrom().uniqueName()+".txt").exists())
			FileIO.appendFile("mail/"+mail.getFrom().uniqueName()+".txt", "\n"+mail.toString());
		else
			FileIO.saveFile("mail/"+mail.getFrom().uniqueName()+".txt", mail.toString());
	}

	public Collection<Mail> getMails(Mailable target){
		String[] mailFile = FileIO.loadFile("mail/"+target.uniqueName()+".txt", "").split("\n");
		ArrayList<Mail> mails = new ArrayList<Mail>();
		for(String line : mailFile){
			Mail mail = Mail.fromString(line);
			if(mail != null) mails.add(mail);
		}
		return mails;
	}

	public boolean deleteMail(Mailable target, Mail targetMail, boolean firstOnly){
		String[] mailFile = FileIO.loadFile("mail/"+target.uniqueName()+".txt", "").split("\n");
		ArrayList<Mail> mails = new ArrayList<Mail>();
		boolean found = false, alreadyDeleted = false;
		for(String line : mailFile){
			Mail mail = Mail.fromString(line);
			if(mail != null){
				if(alreadyDeleted || !mail.equals(targetMail)) mails.add(mail);
				else{
					found = true;
					alreadyDeleted = firstOnly;
				}
			}
		}
		return found;
	}
	public boolean deleteMail(Mailable target){
		boolean hadMail = !FileIO.loadFile("mail/"+target.uniqueName()+".txt", "").trim().isEmpty();
		try{new PrintWriter("mail/"+target.uniqueName()+".txt").close();}
		catch(FileNotFoundException e){return false;}
		return hadMail;
	}

	public Iterable<Mail> getMailsFrom(Mailable target){
		return getMails(target).stream().filter(mail -> mail.getFrom().equals(target)).collect(Collectors.toList());
	}
	public Iterable<Mail> getMailsTo(Mailable target){
		return getMails(target).stream().filter(mail -> mail.getTo().equals(target)).collect(Collectors.toList());
	}
	public Iterable<Mail> getMailsFromTo(Mailable from, Mailable to){
		return getMails(to).stream()
				.filter(mail -> mail.getFrom().equals(from) && mail.getTo().equals(to))
				.collect(Collectors.toList());
	}
}
