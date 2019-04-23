package Threads;

import Detail.*;
import Handler.*;
import Msg.*;
import Main.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class PeerThread implements Runnable
{
	public boolean msgsent_state = false;
	public boolean choked_state;
	public boolean last_msg_received_state = true;
	public boolean ack_received_state = false;
	public boolean peerChoked;
	public boolean chunk_started_state = false;


	public MessageThread msg_sender;
	public PieceThread piece_thread;
	public String peer_id;
	public MsgId msg_id;
	public InitController threadController;

	public ObjectOutputStream output_stream;

	public long start_time;
	public int data_size;
	public Log logs = null;

	private Socket near_socket;
	private ObjectInputStream input_stream;

	public void run()
	{
		byte[] data = new byte[1000];
		ByteBuffer buffer = ByteBuffer.allocate(40000);

		if(peer_id != null)
		{
			send_hand_msg();
		}

		try
		{
			while(true)
			{
				Msg message = (Msg)input_stream.readObject();

				int returnType = message.getMsgType();

				MsgInfo peer2PeerMsg;

				switch (returnType)
				{
					case 0:
						//in case of choke
						peer2PeerMsg = (MsgInfo)message;
						peerChoked=true;
						logs.info("Peer ["+threadController.getPeerID()+"] is choked by ["+peer_id+"]");
						break;
					case 1:
						//in case of unchoke
						peer2PeerMsg = (MsgInfo)message;
						peerChoked = false;
						logs.info("Peer ["+threadController.getPeerID()+"] is unchoked by ["+peer_id+"]");
						try
						{
							piece_thread.msg_queue.put(peer2PeerMsg);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						break;
					case 2:
						//in case of interested
						peer2PeerMsg = (MsgInfo)message;
						receive_interested_msg(peer2PeerMsg);
						break;
					case 3:
						//in case of not interested
						peer2PeerMsg = (MsgInfo)message;
						receive_not_interested_msg(peer2PeerMsg);
						break;
					case 4:
						//in case of have message
						peer2PeerMsg = (MsgInfo)message;
						msg_have_control(peer2PeerMsg);
						break;
					case 5:
						//in case of bit field
						p2p_msg_control((MsgInfo)message);
						break;
					case 6:
						//in case of request
						peer2PeerMsg = (MsgInfo)message;
						msg_request_control(peer2PeerMsg);
						break;
					case 7:
						//in case of piece
						peer2PeerMsg = (MsgInfo)message;
						get_piece_msg(peer2PeerMsg);
						break;
					case 9:
						//in case of handshake
						if(message instanceof ShakeMsg)
						{
							ShakeMsg handshakeMessage = (ShakeMsg)message;
							detect_hand_msg(handshakeMessage);
						}
						else
						{
							System.out.println("Message is not a Handshake Message");
						}
						break;
					case 100:
						//in case of shutdown
						peer2PeerMsg = (MsgInfo)message;
						threadController.peer_list.add(peer_id);
						break;
				}
			}
		}
		catch(SocketException e){
			System.out.println("Connection Reset.!!");
		}
		catch (EOFException e) {
			System.out.println("Peer "+peer_id+" is disconnected.!!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public void send_not_interested_msg(MsgInfo not_interested_msg)
	{
		try
		{
			msg_sender.send_msg(not_interested_msg);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void send_interested_msg(MsgInfo interested_msg)
	{
		try
		{
			if(peerChoked == false)
			{
				msg_sender.send_msg(interested_msg);
			}
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public void send_chock_msg(MsgInfo chock_msg)
	{
		try
		{
			if(choked_state == false)
			{
				start_time = System.currentTimeMillis();
				data_size = 0;

				set_choke(true);
				msg_sender.send_msg(chock_msg);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void send_request_msg(MsgInfo request_msg)
	{
		try 
		{
			if(peerChoked == false)
			{
				msg_sender.send_msg(request_msg);
			}			
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public void send_have_msg(MsgInfo have_msg)
	{
		try
		{
			msg_sender.send_msg(have_msg);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void send_unchoke_msg(MsgInfo unchoke_msg)
	{
		try 
		{
			if(choked_state == true)
			{
				start_time = System.currentTimeMillis();
				data_size = 0;

				set_choke(false);
				msg_sender.send_msg(unchoke_msg);
			}
			
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public boolean lastPieceMsgReceivedState()
	{
		return last_msg_received_state;
	}
	
	public void send_exit_msg(MsgInfo exit_msg)
	{
		try 
		{
			msg_sender.send_msg(exit_msg);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public double get_speed()
	{
		long time = System.currentTimeMillis() - start_time;
		if(time != 0)
		{
			return ((data_size * 1.0) / (time * 1.0) );
		}
		else
		{
			return 0;
		} 
	}

	public void set_last_received_msg(boolean prev_received_msg_state)
	{
		this.last_msg_received_state = prev_received_msg_state;
	}

	public synchronized void setchunk_started(boolean chunk_request_state)
	{
		this.chunk_started_state = chunk_request_state;
	}


	synchronized public void setPeerID(String peer_id)
	{
		this.peer_id = peer_id;
	}
	synchronized boolean send_bid()
	{
		try
		{
			MsgInfo msg = threadController.get_bit_message();
			msg_sender.send_msg(msg);
			Thread.sleep(2000);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	synchronized boolean send_hand_msg()
	{
		try
		{
			ShakeMsg msg = ShakeMsg.createNewInstance();
			msg.setPeerID(threadController.getPeerID());
			msg_sender.send_msg(msg);
			msgsent_state = true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	synchronized public void close()
	{
		try
		{
			if(input_stream != null)
			{
				input_stream.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	synchronized public static PeerThread newConnection(Socket socket, InitController controller)
	{
		boolean is_init = false;
		PeerThread new_peer = new PeerThread();
		new_peer.threadController = controller;
		new_peer.near_socket = socket;

		if(new_peer.near_socket == null)
		{
			new_peer.close();
			new_peer = null;
			return null;
		}

		try
		{
			new_peer.output_stream = new ObjectOutputStream(new_peer.near_socket.getOutputStream());
			new_peer.input_stream = new ObjectInputStream(new_peer.near_socket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			new_peer.close();
			new_peer = null;
			return null;
		}

		new_peer.msg_id = MsgId.createIdentfier();

		if(new_peer.msg_id == null)
		{
			new_peer.close();
			return null;
		}

		if(controller == null)
		{
			new_peer.close();
			return null;
		}

		new_peer.msg_sender = MessageThread.instanceCreate(new_peer.output_stream,new_peer);

		if(new_peer.msg_sender == null)
		{
			new_peer.close();
			return null;
		}

		new Thread(new_peer.msg_sender).start();

		new_peer.piece_thread = PieceThread.createInstance(controller, new_peer);

		new_peer.logs = controller.getLogger();
		is_init=true;

		if(is_init == false){
			new_peer.close();
			new_peer = null;
		}
		return new_peer;
	}


	private void set_choke(boolean msg_state)
	{
		choked_state = msg_state;
	}

	private void msg_have_control(MsgInfo have_msg)
	{
		logs.info("Peer ["+threadController.getPeerID()+"] received the 'have' message from ["+peer_id+"] for the piece: "+have_msg.getPieceIndex());

		try
		{
			piece_thread.msg_queue.put(have_msg);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void get_piece_msg(MsgInfo piece_msg)
	{
		threadController.send_msgInfo(piece_msg.getPieceIndex(),peer_id);
		threadController.save_piece(piece_msg, peer_id);
		data_size += piece_msg.getPieceInfo().get_len();
		set_last_received_msg(true);
		try
		{
			piece_thread.msg_queue.put(piece_msg);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void p2p_msg_control(MsgInfo p2p_msg)
	{
		try
		{
			piece_thread.msg_queue.put(p2p_msg);

			if(ack_received_state == true && msgsent_state == true && chunk_started_state == false)
			{
				new Thread(piece_thread).start();
				start_time = System.currentTimeMillis();
				data_size = 0;
				setchunk_started(true) ;
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void detect_hand_msg(ShakeMsg hand_msg)
	{
		peer_id = hand_msg.getPeerID();
		send_bid();

		if(msgsent_state == false)
		{
			logs.info("Peer "+threadController.getPeerID()+" is connected to Peer "+peer_id+".");
			send_hand_msg();
		}

		ack_received_state = true;

		if(ack_received_state == true && msgsent_state == true && chunk_started_state == false)
		{
			new Thread(piece_thread).start();
			start_time = System.currentTimeMillis();
			data_size = 0;
			setchunk_started(true);
		}
	}

	private void receive_interested_msg(MsgInfo interested_msg)
	{
		logs.info("Peer ["+threadController.getPeerID()+"] received the 'interested' message from ["+peer_id+"]");
	}

	private void msg_request_control(MsgInfo request_msg)
	{
		if(choked_state == false)
		{
			MsgInfo pieceMessage = threadController.get_pieceMsg(request_msg.getPieceIndex());

			if(pieceMessage != null)
			{
				try
				{
					Thread.sleep(2000);
					msg_sender.send_msg(pieceMessage);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void receive_not_interested_msg(MsgInfo msg)
	{
		logs.info("Peer ["+threadController.getPeerID()+"] received the 'not interested' message from ["+peer_id+"]");
	}
	private PeerThread()
	{

	}
}
