package Threads;

import Handler.*;
import Main.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class UnChokeThread implements Runnable{
	
	public ScheduledFuture<?> schedule = null;
    public ScheduledExecutorService exec_scheduler = null;
    private static UnChokeThread unchoke_thread = null;
    private InitController init_controller = null;
    private Log logs = null;

	public void run()
	{
		ArrayList<String> choke_list = init_controller.chokedPeers;

		if(choke_list.size() > 0)
		{
			Random random = new Random();
			init_controller.unchoke_pear(choke_list.get(random.nextInt(choke_list.size())));
		}

		init_controller.downloaded_state();
		if(init_controller.msg_handler.check_download_state() == true)
		{
			System.out.println("Download completed for Peer ["+init_controller.getPeerID()+"].");
			logs.info("Download completed for Peer ["+init_controller.getPeerID()+"].");
			init_controller.send_exit_signal();
		};
	}
	private boolean init()
	{
		exec_scheduler = Executors.newScheduledThreadPool(1);
		return true;
	}
    
    public static synchronized UnChokeThread createInstance(InitController controller)
    {
    	if(unchoke_thread == null)
    	{
    		if(controller == null)
    		{
    			return null;
    		}
			unchoke_thread = new UnChokeThread();
    		boolean init_state = unchoke_thread.init();
    		
    		if(init_state == false)
    		{
				unchoke_thread.schedule.cancel(true);
				unchoke_thread = null;
    			return null;
    		}
			unchoke_thread.init_controller = controller;
			unchoke_thread.logs = controller.getLogger();
    	}	
    	
    	return unchoke_thread;
    }
    

    

}