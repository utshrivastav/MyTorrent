package Detail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class Tokens
{
	
	private static final Hashtable<String, String> token_list = new Hashtable<String, String>();

	static
	{
		try 
		{
			BufferedReader f_reader =  new BufferedReader(new InputStreamReader(new FileInputStream("Common.cfg")));
			
			String line_str = f_reader.readLine();
			
			while(line_str != null)
			{
				String split_array[] = line_str.trim().split(" ");
				token_list.put(split_array[0].trim(), split_array[1].trim());
				line_str = f_reader.readLine();
			}
			f_reader.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Exception: "+e.getMessage());
			throw new ExceptionInInitializerError("Error Loading properties");
		}
	}
	
	public static String returnProperty(String str)
	{
		return token_list.get(str);
	}
}