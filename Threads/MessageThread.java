package Threads;

import Detail.*;
import Msg.*;

import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageThread implements Runnable {

	private ObjectOutputStream obj_output_stream = null;
	private BlockingQueue<Msg> queue_msg;
	private boolean exit_state = false;
	private static final String pre_log = MessageThread.class.getSimpleName();

	public void run() {

		if(queue_msg == null){
			throw new IllegalStateException(pre_log+": Not initialized . This can be the result of calling the deinit () function.");
		}

		while(exit_state == false){
			try {
				Msg msg = queue_msg.take();
				obj_output_stream.writeUnshared(msg);
				obj_output_stream.flush();
				messageDetailsShow(msg);

				msg = null;
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("System Existed..!!");

				break;
			}
		}
	}

	private boolean init(){
		queue_msg = new ArrayBlockingQueue<Msg>(100);
		return true;
	}
	public void deinitialize(){
		if(queue_msg !=null && queue_msg.size()!=0){
			queue_msg.clear();
		}
		queue_msg = null;
	}

	public void send_msg(Msg msg) throws InterruptedException{
		if(queue_msg == null){
			throw new IllegalStateException("");
		}else{
			queue_msg.put(msg);
		}
	}

	public static MessageThread instanceCreate(ObjectOutputStream outputStream, PeerThread handler){

		MessageThread msgSender = new MessageThread();
		boolean isInitialized = msgSender.init();		
		if(isInitialized == false){
			msgSender.deinitialize();
			msgSender = null;
			return null;
		}
		
		msgSender.obj_output_stream = outputStream;
		return msgSender;
	}

	public void messageDetailsShow(Msg msg){

	}
	private MessageThread(){

	}
}
