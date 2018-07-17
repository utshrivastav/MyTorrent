package Threads;

import Detail.*;
import Handler.*;
import Main.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ServerThread implements Runnable{
	public String Id;
	public Log log = null;
	public boolean serve_complete_state = false;

	public InitController init_controller;
	public PeerConfig peer_config = null;
	public ServerSocket server_socket = null;

	public static ServerThread server_thread = null;

	public void run() {
		Map<String,PeerInfo> peer_list = peer_config.getPeerInfoMap();
		PeerInfo serverPeerInfo = peer_list.get(Id);
		int server_portNum = serverPeerInfo.getportNum();

		try {

			server_socket = new ServerSocket(server_portNum);
			int connected_peers_size = init_controller.connected_numbers();

			for(int i=0 ; i<connected_peers_size ; i++){
				Socket near_peer_socket = server_socket.accept();
				PeerThread near_peer_controller = PeerThread.newConnection(near_peer_socket, init_controller);
				init_controller.near_thread.add(near_peer_controller);
				new Thread(near_peer_controller).start();
			}
			serve_complete_state = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean init_prof (InitController controller){
		peer_config = PeerConfig.createInstance();
		log = init_controller.getLogger();
		if(peer_config == null){
			return false;
		}
		return true;
	}

	public static ServerThread init(String id, InitController controller){
		if(server_thread == null){
			server_thread = new ServerThread();
			server_thread.init_controller = controller;
			server_thread.Id = id;
			boolean init_state = server_thread.init_prof(controller);
			if(init_state == false){

				server_thread = null;
			}
		}
		return server_thread;
	}

}
