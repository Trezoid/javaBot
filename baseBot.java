import org.jibble.pircbot.*;
import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;

public class baseBot extends PircBot {
	private String owner = null;
	private String botName = null;
	private String ircNet = null;
	private ArrayList<String> channels = new ArrayList<String>();

	public baseBot(){
		try{
			File settings = new File("settings.txt");
			FileReader fr = new FileReader(settings);
			BufferedReader br = new BufferedReader(fr);

			owner = br.readLine();
			botName = br.readLine();
			ircNet = br.readLine();
			String channel = br.readLine();
			while(channel != null) {
				channels.add(channel);
				channel = br.readLine();
			}
		}
		catch(Exception e){System.out.println(e.getMessage());}
			

		setName(botName);
	}

	public static void main(String[] args){
		baseBot bot = new baseBot();
		bot.setVerbose(true);
		try{
			bot.connect(bot.ircNet);

		}
		catch (Exception e){
			System.out.println("Can't connect: "+e);
		}
		for(String c : bot.channels) {
			bot.joinChannel(c);
		}
	}
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		log(channel, sender, message);
		if(message.equalsIgnoreCase(botName+": time"))
		{
			String time = new java.util.Date().toString();
			sendMessage(channel, sender + ": " + time);
		}
		else if(message.equalsIgnoreCase(botName+": about"))
		{
			sendMessage(channel, sender+": I am a bot written in java, operated by "+ owner);
		}
		else if(message.equalsIgnoreCase(botName+": quit") && sender.equalsIgnoreCase(owner))
		{
			sendMessage(channel, "Goodbye");
			disconnect();
		}
		else if(message.indexOf(botName+": join") > -1 && sender.equalsIgnoreCase(owner))
		{
			String newChan = message.split("join ")[1];
			joinChannel(newChan);
		}
		else if(message.indexOf(botName) < 1 && message.indexOf(botName) > -1)
		{
			sendMessage(channel, "You wanted an asinine comment, and now you have it. Happy?");
		}
	}
	public void onPart(String channel, String sender, String login, String hostname)
	{
		log(channel, sender, "--Has quit--");
	}

	public void onJoin(String channel, String sender, String login, String hostname)
	{
		log(channel, sender, "--Has joined--");
	}

	
	public void onDisconnect()
	{

		System.exit(0);
	}

	public void log(String channel, String sender, String message)
	{
		DateFormat df = DateFormat.getInstance();

		String time = df.format(new java.util.Date());
		try{
			boolean s = (new File("logs").mkdir());
			File f = new File("logs/"+channel+".txt");
			FileWriter fw = new FileWriter(f, true);
			fw.write(time + "| " + sender + ": " + message + "\n");
			fw.close();
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
}
