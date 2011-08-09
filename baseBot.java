import org.jibble.pircbot.*;

public class baseBot extends PircBot {
	public baseBot(){
		setName("trezBot");
	}

	public static void main(String[] args){
		baseBot bot = new baseBot();
		bot.setVerbose(true);
		try{
			bot.connect("roddenberry.freenode.net");
		}
		catch (Exception e){
			System.out.println("Can't connect: "+e);
		}
		bot.joinChannel("##trezoidsHideout");
	}
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		if(message.equalsIgnoreCase("time"))
		{
			String time = new java.util.Date().toString();
			sendMessage(channel, sender + ": " + time);
		}
		if(message.equalsIgnoreCase("trezBot: about"))
		{
			sendMessage(channel, sender+": I am a bot written in java, operated by Trezoid");
		}
		if(message.equalsIgnoreCase("trezBot: quit") && sender.equalsIgnoreCase("trezoid"))
		{
			sendMessage(channel, "Goodbye");
			disconnect();
		}
	}
	public void onDisconnect()
	{

		System.exit(0);
	}
}
