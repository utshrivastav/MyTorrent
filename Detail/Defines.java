package Detail;

public class Defines {
	
	public static String getMessageFromIndex(int index){
		switch (index) {

		case 0:
			//choke message
			return "Choke Message";
		case 1:
			//unchoke message
			return "Unchoke message";
		case 2:
			// interested message
			return "Interested message";
		case 3:
			// not interested message
			return "Not interested message";
		case 4:
			//have message
			return "Have message";
		case 5:
			//bit field message
			return "Bitfield Message";
		case 6:
			//request message
			return "Request Messages";
		case 7:
			//piece message
			return "Piece message";
		case 9:
			//handshake message
			return "Handshake Message";
		case 100:
		// shutdown message
		return "Shutdown message";

		}
		return null;
	}

	
}
