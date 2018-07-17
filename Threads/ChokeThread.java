package Threads;

import Detail.*;
import Handler.*;
import Main.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ChokeThread implements Runnable
{
	private ScheduledExecutorService exec_scheduler = null;
	private static ChokeThread choke_thread = null;
	private InitController threadController = null;
	private Log logs = null;
	public ScheduledFuture<?> schedule = null;

	public void start(int startDelay, int intervalDelay)
	{
		schedule = exec_scheduler.scheduleAtFixedRate(this, startDelay, intervalDelay, TimeUnit.SECONDS);
	}
	public void run() 
	{

		Integer assumenears = 0;
		HashMap<String, Double> speed_list = threadController.download_speads();
		
		if (Tokens.returnProperty("NumberOfPreferredNeighbors") != null)
			assumenears = Integer.parseInt(Tokens.returnProperty("NumberOfPreferredNeighbors"));
		else{
		}

		if (assumenears <= speed_list.size())
		{
			Entry<String, Double>[] temp_list = new Entry[speed_list.size()];
			LinkedHashMap<String, Double> linked_map = new LinkedHashMap<String, Double>();
			Set<Entry<String, Double>> entry_list = speed_list.entrySet();
			temp_list = entry_list.toArray(temp_list);
			ArrayList<String> unchoked_list = new ArrayList<String>();
			int count = 0;

			for (int i = 0; i < temp_list.length; i++)
			{
				for (int j = i + 1; j < temp_list.length; j++)
				{
					if (temp_list[i].getValue().compareTo(temp_list[j].getValue()) == -1)
					{
						Entry<String, Double> tempEntry = temp_list[i];
						temp_list[i] = temp_list[j];
						temp_list[j] = tempEntry;
					}
				}
			}
			for (int i = 0; i < temp_list.length; i++)
			{
				linked_map.put(temp_list[i].getKey(), temp_list[i].getValue());
			}
			for (Entry<String, Double> entry : linked_map.entrySet())
			{
				String key = entry.getKey();
				unchoked_list.add(key);
				count++; 
				if (count == assumenears)
				{
					break;
				}
			}
			for (String peerID : unchoked_list)
			{
				linked_map.remove(peerID);
			}
			ArrayList<String> choked_list = new ArrayList<String>();
			choked_list.addAll(linked_map.keySet());
			String log = "Peer ["+threadController.getPeerID()+"] has nears [";
			for (String peerID : unchoked_list)
			{
				log += peerID + " , ";
			}
			log +="]";
			logs.info(log);
			threadController.unchoke_peer_list(unchoked_list);
			threadController.chock_peer(choked_list);
		}
	}

	public static synchronized ChokeThread createInstance(InitController controller)
	{
		if (choke_thread == null)
		{
			if (controller == null)
			{
				return null;
			}
			choke_thread = new ChokeThread();
			choke_thread.exec_scheduler = Executors.newScheduledThreadPool(1);
			choke_thread.logs = controller.getLogger();
			choke_thread.threadController = controller;
		}
		return choke_thread;
	}
}