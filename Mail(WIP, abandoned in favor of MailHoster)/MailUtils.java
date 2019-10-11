package Mail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.UUID;
import java.util.logging.Logger;
import EvLib.UUIDLookupUtil;
import Eventials.EventialsBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MailUtils{
	final Logger logger;
//	final UUIDLookupUtil uuidFinder;
//	final Has hMap<String, UserMails> onlineUsers;//TODO: this might be nice
	final public String[] CONSOLE_NAME, ADMIN_NAME, ALL_NAME;
//	final HashSet<String> ADMIN_NAMES;//TODO: populate

	int SOME_TODO_UNIQUE_ID = 0;

	public MailUtils(EventialsBungee pl){
		logger = pl.getLogger();
//		uuidFinder = UUIDLookupUtil.getInstance();
//		onlineUsers = new HashMap<String, UserMails>();

		CONSOLE_NAME = pl.getConfig().getString("mail-server-name", "@Console,console,@Server,Server").split(",");
		ADMIN_NAME = pl.getConfig().getString("mail-admin-name", "@Admin,Admin,@op").split(",");
		ALL_NAME = pl.getConfig().getString("mail-all-name", "@a,@all,all").split(",");

		if(!CONSOLE_NAME[0].startsWith("@")) CONSOLE_NAME[0] = "@"+CONSOLE_NAME[0];
		if(!ADMIN_NAME[0].startsWith("@")) ADMIN_NAME[0] = "@"+ADMIN_NAME[0];
		if(!ALL_NAME[0].startsWith("@")) ALL_NAME[0] = "@"+ALL_NAME[0];

		for(int i=0; i<CONSOLE_NAME.length; ++i) CONSOLE_NAME[i] = CONSOLE_NAME[i].toLowerCase();
		for(int i=0; i<ADMIN_NAME.length; ++i) ADMIN_NAME[i] = ADMIN_NAME[i].toLowerCase();
		for(int i=0; i<ALL_NAME.length; ++i) ALL_NAME[i] = ALL_NAME[i].toLowerCase();

//		onlineUsers.put(CONSOLE_NAME[0], loadUserMail(CONSOLE_NAME[0]));
//		onlineUsers.put(ADMIN_NAME[0], loadUserMail(ADMIN_NAME[0]));
//		onlineUsers.put(ALL_NAME[0], loadUserMail(ALL_NAME[0]));//TODO: Should ALL just dump in everyone's inbox?
	}
	/*
	 * Mails are saved like so:
	 * _id:x>g@02:msg
	 * 002:x>a@03:msg
	 * 003:x>x@05:msg <<< special note
	 * 004>f@43:msg
	 * 005:x>a@93:msg
	 * 006:a>x@01:msg
	 * 007:d>x@02:msg
	 * 008:x>x@05:msg <<< special note
	 * 009:u>x@04:msg
	 * 101:b>x@68:msg
	 */

	/**
	 * Returns the name/UUID that is used to represent this {@code CommandSender} in the mail system
	 * <p>
	 * For {@code Player}s, this returns the player's UUID by default.
	 * For {@code ConsoleSender}, this returns the configured Console name (default is \@Console)
	 *
	 * @param sender   a {@code CommandSender}, the entity whose mail-name will be looked up
	 * @return The name/UUID used to represent the sender within the mail system
	 */
	public String getMailName(CommandSender sender){
		return sender instanceof ProxiedPlayer ? ((ProxiedPlayer)sender).getUniqueId().toString() : CONSOLE_NAME[0];
	}

	/**
	 * Combines two lists of (timestamp-sorted) mails into a single sorted list
	 * <p>
	 * The input mail collections don't have to be sorted, but if they
	 * are not sorted then there is no guarantee the output will be sorted
	 *
	 * @param list1   a {@code Collection<Mail>} representing the first list of Mails
	 * @param list2   a {@code Collection<Mail>} representing the second list of Mails
	 * @return A combinations of the two lists of mail, sorted by timestamp
	 */
	public Collection<Mail> mergeMails(Collection<Mail> list1, Collection<Mail> list2){
		Collection<Mail> merged = new Vector<Mail>();
		Iterator<Mail> it1 = list1.iterator(), it2 = list2.iterator();
		Mail next1 = null, next2 = null;
		while(it1.hasNext() && it2.hasNext()){
			if(next1 == null) next1 = it1.next();
			if(next2 == null) next2 = it2.next();

			while(next1.timestamp < next2.timestamp){
				merged.add(next1);
				if(it1.hasNext()) next1 = it1.next();
				else{next1 = null; break;}
			}
			if(next1 == null){merged.add(next2); break;}
			
			while(next2.timestamp < next1.timestamp){
				merged.add(next2);
				if(it2.hasNext()) next2 = it2.next();
				else{next2 = null; break;}
			}
			if(next2 == null){merged.add(next1); break;}
		}
		while(it1.hasNext()) merged.add(it1.next());
		while(it2.hasNext()) merged.add(it2.next());
		return merged;
	}

	//TODO: Nice API/doc/comment blocks
	public Collection<Mail> getInbox(String sender, Collection<String> targets){
		return filterByTargets(UserMailbox.loadMailTo(sender), targets);
	}
	public Collection<Mail> getInbox(CommandSender sender, Collection<String> targets){
		return getInbox(getMailName(sender), targets);
	}

	public Collection<Mail> getSent(String sender, Collection<String> targets){
		return filterByTargets(UserMailbox.loadMailFrom(sender), targets);
	}
	public Collection<Mail> getSent(CommandSender sender, Collection<String> targets){
		return getInbox(getMailName(sender), targets);
	}

	/**
	 * Send out mail(s) from a given sender to intended recipients
	 * <p>
	 * All data about sender[from], receiver[to], timestamp, id,
	 * and message must be already populated with correct values
	 *
	 * @param sender  the {@code CommandSender} who is sending out this mail
	 * @param targets all players/entities who will receive this mail
	 * @param message the contents of the mail (AKA the user's message)
	 */
	public void sendMail(CommandSender sender, Collection<String> targets, String message){
		if(targets.isEmpty()){
			sender.sendMessage(new TextComponent(ChatColor.RED+
					"Unable to send message -- No recipients where specified"));
			return;
		}
		String from = getMailName(sender);
		long timestamp = System.currentTimeMillis();
//		for(String to : targets) sendMail(new Mail(SOME_TODO_UNIQUE_ID, from, to, timestamp, message));
	}

	/**
	 * Send a single, fully-defined {@code Mail}
	 * <p>
	 * All data about sender[from], receiver[to], timestamp, id,
	 * and message must be already populated with correct values
	 *
	 * @param mail      the {@code Mail} to be sent
	 */
	public void sendMail(Mail mail){
//		UserMailbox fromUser = UserMailbox.loadUserMail(mail.from);
//		UserMailbox toUser = UserMailbox.loadUserMail(mail.to);
//		fromUser.to.add(mail);
//		toUser.from.add(mail);
//		UserMailbox.saveUserMail(fromUser);
//		UserMailbox.saveUserMail(toUser);
	}

	public void clearInbox(CommandSender sender, String targets, boolean clearSent){
		if(clearSent) /* Delete sent as well */;
		
	}

	public void clearSent(CommandSender sender, String targets){
		
	}

	/**
	 * Filter a list of mails by a set target players/entities
	 * <p>
	 * The target values in the set generally identify Players,
	 * but can also represent Admin, Console, or All.
	 *
	 * @param mails     the {@code Collection<Mail>} to be filtered
	 * @param targets   a set of target to/from player(s)
	 * @return the list of mails after being filtered based on the {@code targets}
	 */
	public Collection<Mail> filterByTargets(Collection<Mail> mails, Collection<String> targets){
		if(targets.contains(ALL_NAME[0])) return mails;
		Collection<Mail> result = new Vector<Mail>();
		for(Mail mail : mails){
			//TODO: What if I'm an admin and the ADMIN mailbox has new msgs?
			if(targets.contains(mail.to) || targets.contains(mail.from)) result.add(mail);
		}
		return result;
	}

	/**
	 * Returns a set of UUIDs and/or names representing the intended targets
	 * <p>
	 * The target values in the set generally identify Players,
	 * but can also represent Admin, Console, or All.
	 *
	 * @param sender   a {@code CommandSender}.
	 * @param target   a string containing a comma-separated list of targets.
	 * @return a set of pseudo-UUIDs in string form
	 */
	public Collection<String> getTargets(/*CommandSender sender, */String target){
		Collection<String> targets = new HashSet<String>();
		for(String str : target.toLowerCase().split("(,|\\s)+")){
			if(str.isEmpty()) continue;
			for(String all : ALL_NAME) if(all.equals(str)){
				targets.clear();
				targets.add(ALL_NAME[0]);
				return targets;
			}
			if(!targets.contains(ADMIN_NAME[0]))
			for(String admin : ADMIN_NAME) if(admin.equals(str)){
				targets.add(ADMIN_NAME[0]);
				str = null;
				break;
			}
			if(str == null) continue;

			if(!targets.contains(CONSOLE_NAME[0]))
			for(String console : CONSOLE_NAME) if(console.equals(str)){
				targets.add(CONSOLE_NAME[0]);
				str = null;
				break;
			}
			if(str == null) continue;

			try{targets.add(UUID.fromString(str).toString());}
			catch(IllegalArgumentException ex){
//				UUID uuid = uuidFinder.getUUID(str);
//				if(uuid != null) targets.add(uuid.toString());
				//TODO: else{ problem }
			}
		}
//		if(targets.isEmpty()) targets.add(ALL_NAME[0]);
		return targets;
	}


	public void readSent(CommandSender sender, String target){
		// TODO Auto-generated method stub
	}

	public void readInbox(CommandSender sender, String target){
		// TODO Auto-generated method stub
	}
}