import java.util.Random;

public class Games
{
	public Games()
	{
	}

		public String roll(String message)
		{
			int numDice = 1;
			int numSides = 6;

			String rollSet = message.split("roll ")[1];
			String[] dice = rollSet.toLowerCase().split("d");
			
			numDice = Integer.parseInt(dice[0]);
			if(numDice > 20)
			{
				return "I'm sorry, the number of dice you wanted is too large. Please choose a smaller number and try again.";
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
			
			 return output;
			}
		}
}

