package Main;

import Detail.*;
import Handler.*;
import Msg.*;
import Threads.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class InitController {

	public static InitController init_controller = null;
	public ChokeThread choke_thread = null;
	public UnChokeThread unchoke_thread = null;
	public MsgId msg_id = null;
	public PieceController msg_handler = null;
	public Log logs = null;
	public PeerConfig peer_config = null;
	public ServerThread server_thread;

	public boolean connected_state = false;
	public String peer_id;

	public ArrayList<PeerThread> near_thread = null;
	public ArrayList<String> peer_list = new ArrayList<String>();
	public ArrayList<String> chokedPeers = new ArrayList<String>();

	public String getPeerID() 
	{
		return peer_id;
	}

	public int connected_numbers() {

		Set<String> id_array= peer_config.getPeerInfoMap().keySet();

		int peer_num = 0;

		for (String neerID : id_array)
		{
			if (Integer.valueOf(neerID) > Integer.valueOf(peer_id))
			{
				peer_num++;
			}
		}

		return peer_num;
	}

	public void send_msgInfo(int piece_index, String peer_id)
	{
		//Set having Message
		MsgInfo msg_info = MsgInfo.createInstance();
		msg_info.setPieceIndex(piece_index);

		msg_info.setMsgType(4);

		for (PeerThread peerHandler : near_thread) {

			if (peer_id.equals(peerHandler.peer_id) != true) {
				peerHandler.send_have_msg(msg_info);
			}
		}
	}

	public HashMap<String, Double> download_speads()
	{
		HashMap<String, Double> peer_speed = new HashMap<String, Double>();

		for (PeerThread peerHandler : near_thread)
		{
			peer_speed.put(peerHandler.peer_id, peerHandler.get_speed());
		}
		return peer_speed;
	}


	public void send_exit_signal()
	{
		if (connected_state == false || server_thread.serve_complete_state == false) {

			return;
		}

		MsgInfo shutdown_mag = MsgInfo.createInstance();

		//Set shutdown message
		shutdown_mag.setMsgType(100);

		for (PeerThread peerHandler : near_thread)
		{
			peerHandler.send_exit_msg(shutdown_mag);
		}
		peer_list.add(peer_id);
	}
	private void connect_peers()
	{
		Set<String> peerid_list = peer_config.getPeerInfoMap().keySet();

		for (String nearId : peerid_list)
		{
			if (Integer.parseInt(nearId) < Integer.parseInt(peer_id))
			{
				PeerInfo peer_details= peer_config.getPeerInfoMap().get(nearId);
				int near_portNum = peer_details.getportNum();
				String near_host = peer_details.getadr();
				logs.info("Peer [" + peer_id + "] makes a connection to Peer [" + nearId + "]");

				try
				{
					Socket near_socket = new Socket(near_host, near_portNum);
					PeerThread near_peer_handler = PeerThread.newConnection(near_socket, this);
					near_peer_handler.setPeerID(peer_details.getid());
					near_thread.add(near_peer_handler);
					new Thread(near_peer_handler).start();
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		this.connected_state = true;
	}
	

	public void startThread() 
	{
		new Thread(server_thread).start();
		connect_peers();
		choke_thread = ChokeThread.createInstance(this);
		int chock_interval = Integer.parseInt(Tokens.returnProperty("UnchokingInterval"));
		choke_thread.start(0, chock_interval);
		unchoke_thread= UnChokeThread.createInstance(this);
		int unchoke_interval = Integer.parseInt(Tokens.returnProperty("OptimisticUnchokingInterval"));
		unchoke_thread.schedule = unchoke_thread.exec_scheduler.scheduleAtFixedRate(unchoke_thread, 10, unchoke_interval, TimeUnit.SECONDS);
	}



	public boolean downloaded_state()
	{
		System.out.println("Checking Peer  " + peer_id + "  for downloaded");
		if (connected_state == false || server_thread.serve_complete_state == false)
		{
			return false;
		}
		if (peer_config.getPeerInfoMap().size() == peer_list.size())
		{
			System.out.println(peer_list.size());
			choke_thread.schedule.cancel(true);
			unchoke_thread.schedule.cancel(true);
			logs.exit_log();
			msg_handler.exit_stream();
			System.out.println("Exit");
			try{
			System.exit(0);
			}
			catch(Exception e) {
			System.out.println("ERROR");
			}
		}

		return false;
	}

	public void unchoke_peer_list(ArrayList<String> peer_list)
	{
		//Set Unchoke message
		MsgInfo unchock_msg = MsgInfo.createInstance();
		unchock_msg.setMsgType(1);
		for (String peerToUnchoke : peer_list)
		{
			for (PeerThread peerHandler : near_thread)
			{
				if (peerHandler.peer_id.equals(peerToUnchoke))
				{
					if (peerHandler.ack_received_state == true)
					{
						peerHandler.send_unchoke_msg(unchock_msg);
					}
					break;
				}
			}
		}
	}

	public MsgInfo get_pieceMsg(int index)
	{
		PieceInfo piece_info = msg_handler.get_piece_data(index);

		if (piece_info != null)
		{
			//Set Piece message
			MsgInfo message = MsgInfo.createInstance();
			message.setMsgType(7);
			message.setPieceIndex(index);
			message.setPieceInfo(piece_info);
			return message;
		}
		else
		{
			return null;
		}
	}

	public void chock_peer(ArrayList<String> peer_list)
	{
		//Set Choke Message
		chokedPeers = peer_list;
		MsgInfo choke_msg = MsgInfo.createInstance();
		choke_msg.setMsgType(0);
		for (String peerToBeChoked : peer_list)
		{
			for (PeerThread peerHandler : near_thread)
			{
				if (peerHandler.peer_id.equals(peerToBeChoked))
				{
					if (peerHandler.ack_received_state == true)
					{
						peerHandler.send_chock_msg(choke_msg);
					}
					break;
				}
			}
		}
	}

	public synchronized MsgInfo get_bit_message()
	{
		MsgInfo msg_info = MsgInfo.createInstance();
		msg_info.setBitFieldhandler(msg_handler.returnBitHandler());
		if (msg_info.getBitHandler() == null)
		{
			
		}
		//Set Bit Message
		msg_info.setMsgType(5);
		return msg_info;
	}

	public static synchronized InitController set_peer(String id)
	{
		if (init_controller == null)
		{
			boolean init_state=false;
			init_controller = new InitController();
			init_controller.peer_config = PeerConfig.createInstance();
			init_controller.peer_id = id;
			if (init_controller.peer_config == null)
			{
				init_controller=null;
				init_state = false;
				return null;
			}
			init_controller.msg_id = MsgId.createIdentfier();
			if (init_controller.msg_id == null)
			{
				init_controller=null;
				init_state = false;
				return null;
			}
			if (PeerConfig.createInstance().getPeerInfoMap().get(id).file_exit_state() == true)
			{
				init_controller.msg_handler = PieceController.create_piece_handler(true, id);
			}
			else
			{
				init_controller.msg_handler = PieceController.create_piece_handler(false, id);
			}
			if (init_controller.msg_handler == null)
			{
				init_controller=null;
				init_state = false;
				return null;
			}
			init_controller.logs = Log.getLog(id);
			init_controller.near_thread = new ArrayList<PeerThread>();
			if (init_controller.logs == null)
			{
				System.out.println("logger initialization error");
				init_controller=null;
				init_state = false;
				return null;
			}
			init_controller.server_thread = ServerThread.init(id, init_controller);
			init_controller.logs = Log.getLog(id);
			init_state=true;
			if (init_state == false)
			{
				init_controller = null;
			}
		}
		return init_controller;
	}


	public synchronized void save_piece(MsgInfo piese_msg, String src_id)
	{
		msg_handler.write_piece(piese_msg.getPieceIndex(), piese_msg.getPieceInfo());
		logs.info("Peer [" + init_controller.getPeerID() + "] has downloaded the piece [" + piese_msg.getPieceIndex() + "] from [" + src_id + "]. Bit length : " + (msg_handler.returnBitHandler().getBitsLength()));
	}

	public synchronized Log getLogger()
	{
		return logs;
	}

	public void unchoke_pear(String unchockedPeer)
	{
		//Set Unchoke message
		MsgInfo unChokeMessage = MsgInfo.createInstance();
		unChokeMessage.setMsgType(1);

		logs.info("Peer [" + peer_id + "] has unchoked neighbor [" + unchockedPeer + "]");
		
		for (PeerThread peerHandler : near_thread)
		{
			if (peerHandler.peer_id.equals(unchockedPeer))
			{
				if (peerHandler.ack_received_state == true)
				{
					peerHandler.send_unchoke_msg(unChokeMessage);
				}
				break;
			}
		}
	}
}