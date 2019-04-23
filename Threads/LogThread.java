package Threads;

import java.io.BufferedReader;
import java.io.IOException;

public class LogThread implements Runnable
{
	String peer_id;
	BufferedReader buf_reader;
	public void run()
	{
		try
		{
			String l = null;
			while( (l = buf_reader.readLine()) != null )
			{
				System.out.println("["+peer_id+"]: "+l);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}