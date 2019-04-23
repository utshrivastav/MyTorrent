package Msg;

import Detail.*;
import Handler.*;


public class MsgInfo implements Msg
{
	private static final long serialVersionUID = 1L;
	private int msg_type;
	private int piece_index;
	private PieceInfo piece_info;
	private Bit bit_handler = null;
	public int msg_num = 0;
	private static int msg_count = 0;

	public Bit getBitHandler()
	{
		return bit_handler;
	}
	public void setBitFieldhandler(Bit bit_handler)
	{
		this.bit_handler = bit_handler;
	}
	public int getPieceIndex()
	{
		return piece_index;
	}
	public void setPieceIndex(int piece_index)
	{
		this.piece_index = piece_index;
	}
	public int getMsgType()
	{
		return this.msg_type;
	}
	public void setMsgType(int messgageType) 
	{
		this.msg_type = messgageType;
	}
	public PieceInfo getPieceInfo()
	{
		return piece_info;
	}
	public void setPieceInfo(PieceInfo piece_info)
	{
		this.piece_info = piece_info;
	}

	public static MsgInfo createInstance()
	{
		MsgInfo message = new MsgInfo();
		boolean success = message.init();
		if(success == false){
			message = null;
		}
		return message;
	}
	private MsgInfo()
	{

	}
	private boolean init()
	{
		msg_count++;
		msg_num = msg_count;
		return true;
	}

}
