package Handler;



import Detail.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log extends Logger
{
	private String id;
	private FileHandler fileHandler;
	private SimpleDateFormat date_format = null;
	private String file_adr;
	public static Log log = null;

	@Override
	public synchronized void log(Level lvl, String msg)
	{
		super.log(lvl, msg+"\n");
	}

	public static Log getLog(String id)
	{
		if (log == null)
		{
			String dir = "Logs";
			File file = new File(dir);
			file.mkdir();
			log = new Log(id, dir + "/log_peer_" + id + ".log", "logger.name");
			try
			{
				log.init();
			} catch (Exception e) {
				log.exit_log();
				log = null;
				System.out.println("Logger is not initialized");
				e.printStackTrace();
			}
		}
		return log;
	}

	public void exit_log()
	{
		try
		{
			if(fileHandler != null)
			{
				fileHandler.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("Logger is not existed.");
			e.printStackTrace();
		}
	}

	
	public void init() throws SecurityException, IOException
	{
		fileHandler = new FileHandler(file_adr);
		fileHandler.setFormatter(new SimpleFormatter());
		date_format= new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss a");
		this.addHandler(fileHandler);
	}

	public synchronized void info(String msg)
	{
		Calendar c = Calendar.getInstance();
		String dateFormat = date_format.format(c.getTime());
		this.log(Level.INFO, "["+dateFormat+"] : "+msg);
	}
	


	public Log(String id, String file_adr, String name) {
		super(name, null);
		this.file_adr= file_adr;
		this.setLevel(Level.FINEST);
		this.id = id;
	}
}
