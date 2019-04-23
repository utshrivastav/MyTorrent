package Detail;

import java.io.Serializable;


public class PieceInfo implements Serializable
{
	int length;
	private byte[] piece_data;

	public PieceInfo(int length)
	{
		this.length = length;
	}

	public int get_len()
	{
		if(piece_data == null)
		{
			return -1;
		}
		else
		{
			return piece_data.length;
		}
	}

	public void set_piece_data(byte[] piece_data)
	{
		this.piece_data = piece_data;
	}

	public byte[] get_piece_data()
	{
		return piece_data;
	}

}