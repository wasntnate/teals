package FracCalc;
import java.io.*;

/**
 * FracCalc - AP Computer Science Project 1
 * 	Requirements create a simple program that reads fractions from the command line and performs basic math actions on them
 * 
 * 	Expected input will be in the form: 1_1/3 + 3/2 - 8_9/2 * 4 / 6_1_2
 * 	There is no concept of order of operations for simplicity
 * 	
 * @author nateb [at] microsoft.com  
 * @since 2014-07-03
*/
public class Program {
	
	/**
	 * Set this equal to true to print debug information to standard error (stderr)
	 */
	static Boolean enableTrace =true;
		
	/**
	 * This is the entry to the program, all things begin here.
	 */
	public static void main(String[] args) 
	{	
		//
		//	Create input reader
		trace("Creating input reader");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(converter);
		
		//
		//	Main program loop
		while(true)
		{
			trace("Begin Main Loop");
			String input = readLine(reader);
			trace(String.format("Input received was %s", input));
			
			String output = parseInput(input);
			if(output == "quit"){
				trace("last command was 'quit', good bye");
				return;
			}
			
			display(output);
		}
	}
	
	/**
	 * Read one line from an input reader
	 */
	static String readLine(BufferedReader reader)
	{
		//
		//	Get the next line of text
		String line;
		try 
		{
			line = reader.readLine();
		} 
		catch (IOException e) 
		{
			
			//
			//	This is unlikely to happen with keyboard, but can be issue with file
			//		example: java FracCalc.class < myinput.txt
			System.err.println("Error: Unable to read from input device");
			line = "error";
		}
		
		return line;
	}
	
	/**
	 * Process one line of input and return the computed value
	 * 	
	 * For example an input line might read: 
	 * 			1_2/3 + 4_3/5
	 * 
	 * This would become
	 * 			Token 1: 1_2/3
	 * 			Token 2: +
	 * 			Token 3: 4_3/5
	 * 
	 *  Tokens are then processed or shifted over for the next action
	 */
	static String parseInput(String input)
	{	
		String token=null;
		String lastToken=null;
		
		//
		//	Read one token at a time until there is nothing left to read
		while((token = nextToken(input)) != "")
		{
			trace(String.format("Next Token: %s", token));
			
			//
			//	Advance the reader by the token length
			input = input.substring(token.length());
			
			//
			//	Remove whitespace and check which operation this is
			switch(token.trim())
			{
				case "+":
					if(lastToken != null){
						lastToken = add(lastToken, token);
					}break;
				case "-":
					if(lastToken != null){
						lastToken = subtract(lastToken, token);
					} break;
				case "*":
					if(lastToken != null){
						lastToken = multiply(lastToken, token);
					}break;
				case "/":
					if(lastToken != null){
						lastToken = divide(lastToken, token);
					}break;
				default:
					lastToken = token;
					break;				
			}
		}
		
		return lastToken;
	}
	
	/**
	 * 	Given an input string, get the next token from the series
	 */
	static String nextToken(String input)
	{
		String token="";			
		for(int index=0; index<input.length(); index++)
		{
			switch(input.charAt(index))
			{
				case '*':
				case '/':
					if(token.length() != 0){
						return token;
					}else{
						return input.charAt(index)+ "";
					}
				case '+':
					if(token.length() != 0){
						return token;
					}
					else{
						//
						//	Ignore random + characters such as:
						//		1 -+2
						continue;
					}
				case '-':
					//
					//	Determine if negative is operation 
					//		or the numeric value is negative
					if(token.length() != 0){
						return token;
					} else{
						token = "-";
					}break;
				default:
					//
					//	Append the character and advance forward
					token += input.charAt(index);
					break;
			}
		}
		
		//
		//	Reached the end of the string, give back the rest
		return token;		
	}
	
	
	/**
	 * Add two tokens and produce a token containing the resulting value
	 */	
	static String add(String token1, String token2)
	{
		trace(String.format("Add %s to %s", token1, token2));
		Integer num_1=0;
		Integer num_2=0;
		Integer denominator=0;		
		get_gcd(token1, token2, num_1, num_2, denominator);
		
		Integer numerator = num_1 + num_2;
		return String.format("%d/%d", numerator, denominator);		
	}
	
	
	/**
	 * Subtract two tokens and produce a token containing the resulting value
	 */	
	static String subtract(String token1, String token2)
	{
		trace(String.format("Subtract %s by %s", token1, token2));
		Integer num_1=0;
		Integer num_2=0;
		Integer denominator=0;		
		get_gcd(token1, token2, num_1, num_2, denominator);
		
		Integer numerator = num_1 - num_2;
		return String.format("%d/%d", numerator, denominator);
	}
	
	/**
	 * Multiply two tokens and produce a token containing the resulting value
	 */	
	static String multiply(String token1, String token2)
	{
		trace(String.format("Multiply %s by %s", token1, token2));
		Integer num_1=0;
		Integer num_2=0;
		Integer denominator=0;				
		get_gcd(token1, token2, num_1, num_2, denominator);
		
		//
		//	Equals a/d * c/d = (a*c) / (d*d)
		Integer numerator = num_1 * num_2;
		denominator *= denominator;
		
		return String.format("%d/%d", numerator, denominator);		
	}
	/**
	 * Divide two tokens and produce a token containing the resulting value
	 */	
	static String divide(String token1, String token2)
	{
		trace(String.format("Divide %s by %s", token1, token2));
		Integer num_1=0;
		Integer num_2=0;
		Integer denominator=0;		
		get_gcd(token1, token2, num_1, num_2, denominator);
		//
		//	Equals (a/d) / (c/d) = ad/cd = a/c		
		return String.format("%d/%d", num_1, num_2);
	}
	
	/**
	 * Given two tokens, convert them to values of a common denominator
	 */
	static void get_gcd(String token1, String token2, Integer num_1, Integer num_2, Integer denominator)
	{
		trace(String.format("GCD of %s and %s", token1, token2));
		Integer den_1=0;
		Integer den_2=0;
		get_fraction(token1, num_1, den_1);
		get_fraction(token2, num_2, den_2);
		
		//
		//	If they are equal score!
		if(den_1 == den_2)
		{
			denominator = den_1;
			return;
		}
	
		//
		//	This is equal to: a/b + c/d = ad/bd + bc/bd
		num_1 *= den_2;
		num_2 *= den_1;
		denominator = den_1 * den_2;
		trace(String.format("GCD is %s/%d and %s/%d", token1, token2, denominator));
	}		

	
	/**
	 * Convert a token into a fraction such as "1_1/3" returns numerator=4, denominator =3 
	 */
	static void get_fraction(String token, int numerator, int denominator)
	{
		trace(String.format("get_fraction of %s", token));		
		token = token.trim();
		
		//
		//	Find the position separators
		//		example: 1_3/4
		int position_underscore = token.indexOf("_");
		int position_slash = token.indexOf("/");
		
		//
		//	Get the whole number amount
		int wholeNumber =1;
		if(position_underscore != notfound || position_slash == notfound)
		{
			String wholeNumberText = token.substring(0, position_underscore);
			wholeNumber = Integer.parseInt(wholeNumberText);
			
			//
			//	Shorten the string to leave only the fraction
			token = token.substring(position_underscore+1);
		}
		
		//
		//	Now check if there is a fractional part
		position_slash = token.indexOf("/");
		if(position_slash == notfound)
		{
			numerator = wholeNumber;
			denominator = 1;
			return;
		}
				
		String numeratorText = token.substring(0, position_slash);
		String denominatorText= token.substring(position_slash);
		
		numerator = Integer.parseInt(numeratorText);
		denominator = Integer.parseInt(denominatorText);
		
		//
		//	Now convert any mixed fractions to an improper fraction
		numerator = denominator * wholeNumber + numerator;
		trace(String.format("get_fraction returns %d/%d", numerator,denominator));
	}	
	
	/**
	 * Print a message to the debug channel, if enabled
	 * @param message what to send
	 */
	static void trace(String message)
	{
		if(enableTrace){
			System.err.println("message");
		}	
	}
	
	/**
	 *	Write a simple message to the screen (standard output) 
	 */
	static void display(String output)
	{
		System.out.println(output);
	}
	
	/**
	 * This is a constant to make the code more readable
	 */
	static final int notfound = -1;
	
}
