import org.jibble.pircbot.*;
import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;
import java.util.Random;
public class baseBot extends PircBot {
	private String owner = null;
	private String botName = null;
	private String ircNet = null;
	private ArrayList<String> channels = new ArrayList<String>();

	public baseBot(){
		try{

			File settings = new File("settings.txt");
			if(!settings.exists())
			{
				firstRun();
			}

			FileReader fr = new FileReader(settings);
			BufferedReader br = new BufferedReader(fr);

			owner = br.readLine();
			botName = br.readLine();
			ircNet = br.readLine();
			String channel = br.readLine();
			while(channel != null) {
				channels.add(channel);
				log(channel, "", "====Bot restarted====");
				channel = br.readLine();
			}
		}
		catch(Exception e){System.out.println(e.getMessage());}
			

		setName(botName);
		log("","","=====Bot started=====");
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
		bot.setVerbose(false);
		for(String c : bot.channels) {
			bot.joinChannel(c);
		}
	}

	public void firstRun()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("It appears that you haven't set up your bots settings. Please do that now.");
		System.out.println("First, enter you IRC nick. This will set the owner of the bot, used for some commands.");
		String owner = scan.nextLine();
		System.out.println("Now, enter a name for your bot. It needs to be unique on the IRC network you are using.");
		String botName = scan.nextLine();
		System.out.println("Thirdly, enter the network URL of the IRC network you are using (eg irc.freenode.net).");
		String ircNet = scan.nextLine();
		System.out.println("now enter the names of the chatrooms you want to include, with all leading characters, seperated by spaces.");
		String[] channel = scan.nextLine().split(" ");

		try{
			File f = new File("settings.txt");
			FileWriter fw = new FileWriter(f, true);
			fw.write(owner +"\n");
			fw.write(botName+"\n");
			fw.write(ircNet+"\n");
			for(int i = 0; i < channel.length; i++)
			{
				fw.write(channel[i] + "\n");
			}
			fw.close();
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
		 

	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		DateFormat df = DateFormat.getInstance();

		String time = df.format(new java.util.Date());
		System.out.println(channel + ", " + time + " | " + sender + ": " + message);
	
		log(channel, sender, message);
		if(message.equalsIgnoreCase(botName+": time"))
		{
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
		else if(message.indexOf(botName+": say") > -1 && sender.equalsIgnoreCase(owner))
		{
			String[] parts = message.split(" ");
			String chan = parts[2];
			String toSend = "";
			String temp = "";
			for(int i = 3; i < parts.length; i++)
			{
				toSend += " "+parts[i];
			}
			sendMessage(chan, toSend);
		}

		else if(message.indexOf(botName+": join") > -1 && sender.equalsIgnoreCase(owner))
		{
			String newChan = message.split("join ")[1];
			joinChannel(newChan);
		}
		else if(message.indexOf(botName+": roll") > -1)
		{
			int numDice = 1;
			int numSides = 6;

			String rollSet = message.split("roll ")[1];
			String[] dice = rollSet.toLowerCase().split("d");
			
			numDice = Integer.parseInt(dice[0]);
			if(numDice > 20)
			{
				sendMessage(channel, sender+": I'm sorry, the number of dice you wanted is too large. Please choose a smaller number and try again.");
			}
			else{
			numSides = Integer.parseInt(dice[1]);
		
			String allRolls = "";		
			int totalRoll = 0;
			Random r = new Random();
			int[] rolls = new int[numDice];
			for(int i = 0; i < numDice; i++)
			{
				rolls[i] = r.nextInt(numSides + 1);
				totalRoll += rolls[i];
				allRolls += rolls[i]+ ", ";	
			}

			String output = "You have rolled "+numDice+" Dice, each with "+numSides+" sides. The total is "+totalRoll+" which is made up of " + allRolls;
			
			sendMessage(channel, sender + ": " + output);
			}
		}
		else if(message.indexOf(botName) < botName.length() && message.indexOf(botName) > -1)
		{
			String toAI = message.split(botName+": ")[1];
			sendMessage(channel, sender + ": " + ai(toAI));
		}

		else if(message.indexOf("http://") > -1 || message.indexOf("https://") > -1)
		{
			String urlGrab = message.substring(message.indexOf("http"), message.length());
			urlGrab = urlGrab.split(" ")[0];
			sendMessage(channel, "URL TITLE: " + getTitle(urlGrab));
		}
	}
	public void onPart(String channel, String sender, String login, String hostname)
	{
		System.out.println(channel+": ====="+sender+" has left====");
		log(channel, sender, "--Has quit--");
	}

	public void onJoin(String channel, String sender, String login, String hostname)
	{
		System.out.println(channel+": ===="+sender+" has joined====");
		log(channel, sender, "--Has joined--");
	}

	
	public void onDisconnect()
	{

		System.exit(0);
	}
	
	public void onKick(String channel, String kicker, String kickerLogin, String kickerHost,String nick, String reason)
	{
		if(nick.equalsIgnoreCase(botName))
		{
			System.out.println("======Bot kicked by "+kicker+" because "+reason+"=====");
			System.out.println("======Rejoining=======");
			joinChannel(channel);
		}
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


	public String ai(String message)
	{
		String result =  "";
		try{ 
			URL url = new URL("http://kato.botdom.com");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(true);	
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			String cleanMessage = message.replace("?", "%3F");
			wr.write("m="+cleanMessage);
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;	
			while ((line = rd.readLine()) != null) 
			{
				result += line + "\r\n";
			}
			rd.close();
			wr.close();
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		try{
		String splitResult = result.split("<h2>")[1];
		String aiMessage = splitResult.split("</h2>")[0];
		aiMessage = aiMessage.replace("&#8217;", "'").replace("&#8221;", "\"").replace("&#8220;", "\"").replace("#&8212;", "-").replace("&lt;br/&gt;", "\n");
		return aiMessage;
		}
	catch(Exception e){System.out.println(e.getMessage());return "Message not found";}

	}
	public String getTitle(String grabbedURL)
	{
		String result = "";
		try{ 
			URL url = new URL(grabbedURL);
			URLConnection conn = url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String prevLine = "";	
			while ((line = rd.readLine()) != null) 
			{
				if(prevLine.indexOf("</title>") < 0)
				{
					result += line + "\r\n";
					prevLine = line;
				}
				else{rd.close();}
			}
			rd.close();
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		result = result.split("<title>")[1].split("</title>")[0];
		result = result.replace("&amp;", "&");
		return result;
	}	
}

