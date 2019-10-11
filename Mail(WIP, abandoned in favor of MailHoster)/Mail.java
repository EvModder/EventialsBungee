package Mail;

public class Mail{
	long timestamp;
	Mailable from, to;
	String message;
	boolean unread;

	// [x] (4w6d11h) Name: message here <Attachment>
	public Mail(long timestamp, Mailable from, Mailable to, String message, boolean unread){
		this.timestamp = timestamp;
		this.from = from; this.to = to;
		this.message = message;
		this.unread = unread;
	}
	@Override public String toString(){
		return new StringBuilder("").append(timestamp).append('@')
						.append(from).append('>').append(to)
						.append(':').append(message).toString();
	}

	public static Mail fromString(String str){
		int i = str.indexOf('@');
		if(i == -1) return null;
		long timestamp;
		try{timestamp = Long.parseLong(str.substring(0, i));}
		catch(IllegalArgumentException ex){return null;}
		str = str.substring(i+1);
		i = str.indexOf('>');
		if(i == -1) return null;
		Mailable from = Mailable.getByName(str.substring(0, i), true);
		if(from == null) return null;
		str = str.substring(i+1);
		i = str.indexOf('!');
		boolean unread = (i != -1);
		str = str.substring(i+1);
		i = str.indexOf(':');
		if(i == -1) return null;
		Mailable to = Mailable.getByName(str.substring(0, i), true);
		if(to == null) return null;
		str = str.substring(i+1);
		return new Mail(timestamp, from, to, str, unread);
	}

	public Mailable getTo(){return to;}
	public Mailable getFrom(){return from;}
	public String getMessage(){return message;}
	public long getTimestamp(){return timestamp;}


	@Override public boolean equals(Object o){
		return o != null && o instanceof Mail
				&& ((Mail)o).timestamp == timestamp
				&& ((Mail)o).from.equals(from) && ((Mail)o).to.equals(to)
				&& ((Mail)o).message.equals(message);
	}
	@Override public int hashCode(){
		return (int)(timestamp ^ (timestamp >>> 32)) + from.hashCode()*to.hashCode();
	}
}