package Detail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Handler.*;
import Main.*;
import Msg.*;
import Threads.*;


public class PeerConfig {
	
public Map<String,PeerInfo> map = null;
	
private static PeerConfig  peerConfig = null;

	public boolean init(){
		try {
			//----------------get server list from config file-----------------
			map = new HashMap<String,PeerInfo>();
			BufferedReader bfReader =  new BufferedReader(new InputStreamReader(new FileInputStream("PeerInfo.cfg")));
			String line = bfReader.readLine();

			PeerInfo peerInfo = null;
			while(line != null){
				String token_array[] = line.trim().split(" ");
				peerInfo = new PeerInfo();
				if(token_array[3].equals("1")){
					peerInfo.setexit_state(true);
				}else{
					peerInfo.setexit_state(false);
				}

				peerInfo.setid(token_array[0]);
				peerInfo.setadr(token_array[1]);
				peerInfo.setportNum(Integer.parseInt(token_array[2]));

				map.put(token_array[0],peerInfo);
				line = bfReader.readLine();
			}
			//--------------------------------------------------------------------

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return true;
	}
	
	public Map<String, PeerInfo> getPeerInfoMap() {
		return map;
	}

	public static PeerConfig createInstance(){
		if( peerConfig == null){
			peerConfig = new  PeerConfig();
			peerConfig.init();
		}
		return  peerConfig;
	}

}
