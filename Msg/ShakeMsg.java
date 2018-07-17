package Msg;

import Detail.*;

/*
 * Author: Prem Ankur
 * */

public class ShakeMsg implements Msg
{
	private String peer_id;
	private static int instance_num;
	private int nsgNumber;

	public int getMsgType()
	{
		//return handshake message type
		return 9;
	}
	public String getPeerID()
	{
		return peer_id;
	}

	public static ShakeMsg createNewInstance()
	{
		instance_num++;
		ShakeMsg shake_msg = new ShakeMsg();
		boolean success = true;
		if(success == false)
		{
			shake_msg = null;
		}else
		{
			shake_msg.update_msgNum();
		}
		return shake_msg;
	}
	
	public void setPeerID(String peer_id)
	{
		this.peer_id = peer_id;
	}
	
	private void update_msgNum()
	{
		nsgNumber=instance_num;
	}

	private ShakeMsg()
	{

	}


}