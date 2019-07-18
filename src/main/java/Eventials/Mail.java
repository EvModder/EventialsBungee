package Eventials;

public class Mail{
	String from, to;
	String message;
	long timestamp, id;
	public Mail(long id, String from, String to, long ts, String msg){
		this.id = id;
		this.from = from; this.to = to;
		timestamp = ts;
		message = msg;
	}
	public static Mail fromString(String str){
		int i = str.indexOf(':');
		if(i == -1) return null;
		long id;
		try{id = Long.parseLong(str.substring(0, i));}
		catch(IllegalArgumentException ex){return null;}
		str = str.substring(i+1);
		i = str.indexOf('>');
		if(i == -1) return null;
//		UUID from;
		String from = str.substring(0, i);
//		try{from = UUID.fromString(str.substring(0, i));}//TODO: UUIDs for Admin/Console/All?
//		catch(IllegalArgumentException ex){return null;}
		str = str.substring(i+1);
		i = str.indexOf('@');
		if(i == -1) return null;
//		UUID to;
		String to = str.substring(0, i);
//		try{to = UUID.fromString(str.substring(0, i));}
//		catch(IllegalArgumentException ex){return null;}
		str = str.substring(i+1);
		i = str.indexOf(':');
		if(i == -1) return null;
		long ts;
		try{ts = Long.parseLong(str.substring(0, i));}
		catch(NumberFormatException ex){return null;}
		//Remaining string is the actual message -- TODO: escape special characters :>@\n
		str = str.substring(i+1);
		return new Mail(id, from, to, ts, str);
	}

	@Override public String toString(){
		return new StringBuilder("").append(id).append(':').append(from).append('>').append(to)
						.append('@').append(timestamp).append(':').append(message).toString();
	}
	@Override public boolean equals(Object o){
		return o != null && o instanceof Mail
				&& ((Mail)o).id == id
				&& ((Mail)o).timestamp == timestamp
				&& ((Mail)o).to.equals(to)
				&& ((Mail)o).from.equals(from)
				&& ((Mail)o).message.equals(message);
	}
	@Override public int hashCode(){
		return (int)id + from.hashCode() * to.hashCode();
	}
}