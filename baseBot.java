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
		if(message.equalsIgnoreCase("time"))
		{
			String time = new java.util.Date().toString();
			sendMessage(channel, sender + ": " + time);
		}
		if(message.equalsIgnoreCase(botName+": about"))
		{
			sendMessage(channel, sender+": I am a bot written in java, operated by "+ owner);
		}
		if(message.equalsIgnoreCase(botName+": quit") && sender.equalsIgnoreCase(owner))
		{
			sendMessage(channel, "Goodbye");
			disconnect();
		}
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
