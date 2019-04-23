package Msg;

public class MsgId
{
	private static MsgId mag_id;

	public static MsgId createIdentfier()
	{
		if(mag_id == null)
		{
			mag_id = new MsgId();
		}
		return mag_id;
	}
	private MsgId(){

	}


}