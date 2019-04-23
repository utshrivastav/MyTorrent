package Threads;

import Detail.*;
import Handler.*;
import Msg.*;
import Main.*;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PieceThread implements Runnable
{
	int [] piece_array = new int[1000];
	public BlockingQueue<MsgInfo> msg_queue;
	private static String pre_log = PieceThread.class.getSimpleName();
	private Bit bit_controller = null;
	private PeerThread peer_controller;
	private boolean shutdown_state = false;
	private InitController threadController;

	public void run()
	{
		while(shutdown_state == false)
		{
			try
			{
				//set interest message
				MsgInfo interest_msg = MsgInfo.createInstance();
				interest_msg.setMsgType(2);

				//set request message
				MsgInfo request_msg = MsgInfo.createInstance();
				request_msg.setMsgType(6);

				MsgInfo msg = msg_queue.take();
				System.out.println(": Received Message: "+Defines.getMessageFromIndex(msg.getMsgType()));

				//incase of unchoke message
				if(msg.getMsgType() == 1)
				{
					int numMissingPiece = piece_num();

					peer_controller.set_last_received_msg(false);

					if(numMissingPiece != -1)
					{
						request_msg.setPieceIndex(numMissingPiece);
						peer_controller.send_request_msg(request_msg);
						interest_msg.setPieceIndex(numMissingPiece);
						peer_controller.send_interested_msg(interest_msg);
					}
				}

				//in case of having message
				if(msg.getMsgType() == 4)
				{
					int pieceNum = msg.getPieceIndex();

					try
					{
						bit_controller.setBitOn(pieceNum, true);
					}
					catch (Exception e)
					{
						System.out.println(pre_log+"["+peer_controller.peer_id+"]: Error : NULL POINTER for piece Index"+pieceNum +" ... "+bit_controller);
						e.printStackTrace();
					}

					int missingPieceIndex = piece_num();

					if(missingPieceIndex != -1)
					{
						if(peer_controller.lastPieceMsgReceivedState() == true)
						{
							peer_controller.set_last_received_msg(false);
							request_msg.setPieceIndex(missingPieceIndex);
							peer_controller.send_request_msg(request_msg);
							interest_msg.setPieceIndex(missingPieceIndex);
							peer_controller.send_interested_msg(interest_msg);
						}
					}
					else
					{
						//set not interested message
						MsgInfo not_interest_msg = MsgInfo.createInstance();
						not_interest_msg.setMsgType(3);
						peer_controller.send_not_interested_msg(not_interest_msg);
					}
				}

				//in case of bitfield message
				if(msg.getMsgType() == 5)
				{
					bit_controller = msg.getBitHandler();

					int missingPieces = piece_num();

					if(missingPieces != -1)
					{
						request_msg.setPieceIndex(missingPieces);
						peer_controller.send_request_msg(request_msg);
						interest_msg.setPieceIndex(missingPieces);
						peer_controller.send_interested_msg(interest_msg);
					}
					else
					{
						//set not interested message
						MsgInfo not_interested_msg = MsgInfo.createInstance();
						not_interested_msg.setMsgType(3);
						peer_controller.send_not_interested_msg(not_interested_msg);
					}
				}

				//in case of piece message
				if(msg.getMsgType() == 7)
				{
					int numMissingPiece = piece_num();
					if(numMissingPiece != -1)
					{
						if(peer_controller.lastPieceMsgReceivedState() == true)
						{
							peer_controller.set_last_received_msg(false);
							request_msg.setPieceIndex(numMissingPiece);
							peer_controller.send_request_msg(request_msg);
							interest_msg.setPieceIndex(numMissingPiece);
							peer_controller.send_interested_msg(interest_msg);
						}
					}
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				break;
			}
		}
	}
	public int piece_num()
	{

		int index = 0;
		Bit peer_bit_controller = threadController.get_bit_message().getBitHandler();

		for(int i=0 ; i<bit_controller.getLength() && index<piece_array.length ; i++)
		{
			if(peer_bit_controller.getBitOn(i) == false && bit_controller.getBitOn(i) == true)
			{
				piece_array[index] = i;
				index++;
			}
		}

		if(index != 0)
		{
			Random random = new Random();
			return piece_array[random.nextInt(index)];
		}
		else
		{
			return -1;
		}
	}
	public static PieceThread createInstance(InitController controller, PeerThread peerHandler)
	{		
		if(controller == null || peerHandler == null)
		{
			return null;
		}
		int piece_size = Integer.parseInt(Tokens.returnProperty("PieceSize"));
		int piece_num = (int) Math.ceil(Integer.parseInt(Tokens.returnProperty("FileSize")) / (piece_size*1.0));

		PieceThread sender_request = new PieceThread();
		sender_request.threadController = controller;
		sender_request.peer_controller = peerHandler;
		sender_request.msg_queue = new ArrayBlockingQueue<MsgInfo>(100);
		sender_request.bit_controller = new Bit(piece_num);
		return sender_request;
	}
	private PieceThread()
	{
	}
}