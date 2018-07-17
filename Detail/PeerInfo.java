package Detail;

public class PeerInfo
{
	private boolean exit_state;
	private int portNum;
	private String adr;
	private String id;
	public void setportNum(int portNum)
	{
		this.portNum = portNum;
	}
	public void setadr(String adr)
	{
		this.adr = adr;
	}
	public void setexit_state(boolean exit_state)
	{
		this.exit_state = exit_state;
	}
	public int getportNum()
	{
		return portNum;
	}
	public void setid(String id)
	{
		this.id = id;
	}
	public boolean file_exit_state()
	{
		return exit_state;
	}
	public String getid()
	{
		return id;
	}
	public String getadr()
	{
		return adr;
	}
}