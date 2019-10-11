package Mail;
import java.util.Collection;
import java.util.Vector;
import EvLib.FileIO;
import Eventials.EventialsBungee;

public class UserMailbox{
	final String uuid;
	final Vector<Mail> from, to;

	public UserMailbox(String uuid){
		this.uuid = uuid;
		from = new Vector<Mail>();
		to = new Vector<Mail>();
	}
	
	public static Collection<Mail> loadMailFrom(String uuid){
		String[] lines = FileIO.loadFile("mail/" + uuid + ".txt", "").split("\n");
		Collection<Mail> mails = new Vector<Mail>();
		Mail firstSelfSent = null;
		for(String line : lines){
			Mail mail = Mail.fromString(line);
			if(mail == null) EventialsBungee.getPlugin().getLogger()
				.severe("Unable to parse file: plugins/EvFolder/mail/" + uuid + ".txt"
						+ "\nLine "+line);
			else if(uuid.equals(mail.from)){
				if(uuid.equals(mail.to)){
					if(firstSelfSent == null) firstSelfSent = mail;
					else if(mail.equals(firstSelfSent)) break;
				}
				mails.add(mail);
			}
			else break;
		}
		return mails;
	}

	public static Collection<Mail> loadMailTo(String uuid){
		String[] lines = FileIO.loadFile("mail/" + uuid + ".txt", "").split("\n");
		Collection<Mail> mails = new Vector<Mail>();

		int i = 0;
		Mail firstSelfSent = null;
		for(; i < lines.length; ++i){
			Mail mail = Mail.fromString(lines[i]);
			if(mail == null) EventialsBungee.getPlugin().getLogger()
				.severe("Unable to parse file: plugins/EvFolder/mail/" + uuid + ".txt"
						+ "\nLine[" + i + "]: "+lines[i]);
			else if(uuid.equals(mail.to)){
				if(uuid.equals(mail.from)){
					if(firstSelfSent == null) firstSelfSent = mail;
					else if(mail.equals(firstSelfSent)) break;
				}
				else break;
			}
		}
		for(; i < lines.length; ++i){
			Mail mail = Mail.fromString(lines[i]);
			if(mail == null) EventialsBungee.getPlugin().getLogger()
				.severe("Unable to parse file: plugins/EvFolder/mail/" + uuid + ".txt"
						+ "\nLine[" + i + "]: "+lines[i]);
			else mails.add(mail);
		}
		return mails;
	}

	public static UserMailbox loadUserMail(String uuid){
		String[] lines = FileIO.loadFile("mail/" + uuid + ".txt", "").split("\n");
		UserMailbox mails = new UserMailbox(uuid);
		Mail mail;
		int i = 0;
		Mail firstSelfSent = null;
		for(; i < lines.length; ++i){
			if((mail = Mail.fromString(lines[i])) == null) EventialsBungee.getPlugin().getLogger()
				.severe("Unable to parse file: plugins/EvFolder/mail/" + uuid + ".txt"
				+ "\nLine[" + i + "]: "+lines[i]);
			else if(uuid.equals(mail.from)){
				if(uuid.equals(mail.to)){
					if(firstSelfSent == null) firstSelfSent = mail;
					else if(mail.equals(firstSelfSent)) break;
				}
				mails.from.add(mail);
			}
			else break;
		}
		for(; i < lines.length; ++i){
			if((mail = Mail.fromString(lines[i])) == null) EventialsBungee.getPlugin().getLogger()
				.severe("Unable to parse file: plugins/EvFolder/mail/" + uuid + ".txt"
						+ "\nLine[" + i + "]: "+lines[i]);
			else mails.to.add(mail);
		}
		return mails;
	}

	public static void saveUserMail(UserMailbox mails){
		StringBuilder builder = new StringBuilder("");
		for(Mail mail : mails.from) builder.append(mail.toString()).append('\n');
		for(Mail mail : mails.to) builder.append(mail.toString()).append('\n');
		FileIO.saveFile("mail/" + mails.uuid + ".txt", builder.toString());
	}
}