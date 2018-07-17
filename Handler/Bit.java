package Handler;

import java.io.Serializable;

public class Bit implements Serializable
{
	private int length;

	private boolean bit_array[];

	synchronized public void setBitOn(int index, boolean state)
	{
		bit_array[index] = state;
	}
	public boolean getBitOn(int num)
	{
		return bit_array[num];
	}
	public int getLength()
	{
		return length;
	}

	public boolean download_state()
	{
		if(bit_array.length==0)
		{
			return false;
		}
		if(bit_array==null)
		{
			return false;
		}

		for (int i = 0;i<this.getLength();i++)
		{
			if(bit_array[i]!=true)
			{
				return false;
			}
		}
		return true;
	}
	
	public void setBits()
	{
		for(int i=0 ; i<bit_array.length ; i++)
		{
			bit_array[i] = true;
		}
	}
	
	
	public int getBitsLength()
	{
		int len = 0;
		for(int i = 0; i < this.bit_array.length; i++){
			if(this.bit_array[i]==true)
				len++;
		}
		return len;
	}
	
	


	public Bit(int numPieces)
	{
		bit_array = new boolean[numPieces];
		length = numPieces;

		for(int i = 0; i < length; i++)
		{
			bit_array[i] = false;
		}
	}
}