package Handler;

import Detail.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class PieceController {

	int piece_num ;
	int piece_length;
	private static PieceController piece_handler;
	private static Bit bit ;
	FileInputStream input_file;
	RandomAccessFile output_file;

	synchronized public PieceInfo get_piece_data(int num){

		PieceInfo piece_info = new PieceInfo(piece_length);
		if(bit.getBitOn(num))
		{
			try{
				byte[] read_bytes = new byte[piece_length];
				output_file.seek(piece_length*num);
				int size = output_file.read(read_bytes);

				if(size == piece_length){
					piece_info.set_piece_data(read_bytes);
				}else{
					byte[] new_read_bytes = new byte[size];
					for(int i=0 ; i<size ; i++){
						new_read_bytes[i] = read_bytes[i];
					}
					piece_info.set_piece_data(read_bytes);
				}

				return piece_info;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else {

			return null;
		}
	}



	synchronized public boolean check_download_state(){
		return bit.download_state();
	}




	synchronized public boolean write_piece(int num,PieceInfo piece){

		if(bit.getBitOn(num))
		{
			return false;

		}
		else
		{
			try {

				output_file.seek(num*piece_length);
				output_file.write(piece.get_piece_data());

				bit.setBitOn(num, true);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

	}

	synchronized public void exit_stream(){
		try {
			if(input_file != null){
				input_file.close();
			}
			if(output_file!= null){
				output_file.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}
	synchronized public static PieceController create_piece_handler(boolean exist_state, String id){
		if(piece_handler == null){
			piece_handler = new PieceController();
			boolean state = piece_handler.initPieceInfo(exist_state,id);
			if(state == false){
				piece_handler = null;
			}
		}
		return piece_handler;
	}


	public Bit returnBitHandler(){
		return bit;
	}

	public boolean initPieceInfo(boolean exist_state, String id){
		
		if(Tokens.returnProperty("PieceSize")!=null)
			piece_length = Integer.parseInt(Tokens.returnProperty("PieceSize"));
		
			if(Tokens.returnProperty("FileSize")!= null)
				piece_num = (int) Math.ceil(Integer.parseInt(Tokens.returnProperty("FileSize")) / (piece_length*1.0)) ;
		

		try
		{
			bit = new Bit(piece_num);
			
			if(exist_state){
				bit.setBits();
			}
			
			String output_fileName = new String();
			output_fileName = Tokens.returnProperty("FileName");
			
			String dir_name = "peer_"+id;
			File dir = new File(dir_name);
			
			if(!dir.exists()){
				dir.mkdir();
			}
			output_fileName = dir.getAbsolutePath()+"/"+output_fileName;
			File file = new File(output_fileName);
			if(file.exists() == false){
				file.createNewFile();
			}
			output_file = new RandomAccessFile(output_fileName,"rw");
			output_file.setLength(Integer.parseInt(Tokens.returnProperty("FileSize")));
			return true;
			
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		  return false;
		}	
	}



	private PieceController(){

	}

	

}