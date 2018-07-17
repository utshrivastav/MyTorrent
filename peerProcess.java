
import Main.*;
import java.util.Scanner;

public class peerProcess {

    public static void main(String[] args) {
        String peerID ="";
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter Peer ID");
        peerID = input.next();
        InitController controller = InitController.set_peer(peerID);
        controller.startThread();
    }
}
