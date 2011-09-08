import java.net.*;
import java.io.*;
import java.util.HashMap;

public class AI
{
	private HashMap<String,String> res;
	private String botName = "";
	private boolean localAI = false;
	public AI(String Name)
	{
		res = new HashMap<String,String>();
		populateRes();
		botName = Name;
	}

	public String getAIResponse(String message, String sender)
	{
			String toAI = message.split(botName+": ")[1];
			return ai(toAI, sender);
	}

	private void populateRes()
	{
		try{

			File lR = new File("ai.txt");
			if(lR.exists())
			{
				localAI = true;
			}

			FileReader fr = new FileReader(lR);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null)
			{
				String[] resMap = line.split("|");
				res.put(resMap[0], resMap[1]);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public String ai(String message, String sender)
	{
		 String aiMessage;

		if(localAI == true)
		{
			if((aiMessage = res.get(message.split(botName+": ")[1])) != null)
			{
				return aiMessage;
			}
			String[] sentence = message.toLowerCase().split(".");
			for(int i = 1; i < sentence.length; i++)
			 {
				 if((aiMessage = res.get(sentence[i]))!= null)
				 {
					 return aiMessage;
				 }
			 }
			String[] question = message.toLowerCase().split("?");
			for(int i = 1; i < question.length; i++)
			 {
				 if((aiMessage = res.get(question[i]+"?"))!= null)
				 {
					 return aiMessage;
				 }
			 }


			String[] in = message.toLowerCase().split(" ");
			for(int i = 1; i < in.length; i++)
			 {
				 if((aiMessage = res.get(in[i]))!= null)
				 {
					 return aiMessage;
				 }
			 }
		}
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
		aiMessage = splitResult.split("</h2>")[0];
		aiMessage = aiMessage.replace("&#8217;", "'").replace("&#8221;", "\"").replace("&#8220;", "\"").replace("#&8212;", "-").replace("&lt;br/&gt;", "\n").replace("Kato", botName).replace("undefined", sender);
		return aiMessage;
		}
	catch(Exception e){System.out.println(e.getMessage());return "My brain seems to be asleep. Sorry about that.";}

	}

}
