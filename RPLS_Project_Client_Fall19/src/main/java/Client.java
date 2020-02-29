/* Project 4: RPSLS with multithreading
 * Wael Mobeirek - wmobei2@uic.edu
 * Enoc Carranza - ecarra6@uic.edu
 * Taha khomusi - tkhomu2@uic.edu
 * Hasan Sehwail - hsehwa2@uic.edu
 * Rock Paper Scissors Lizard Spock networked server and client programs that support multiple clients at once by multithreading.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	Socket socketClient;
	GameInfo info;
	ObjectOutputStream out;
	ObjectInputStream in;
	String ipAddress;
	int port;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
		info = new GameInfo();
		callback = call;
	}
	
	public void run() {
		
		try {
	    socketClient= new Socket(ipAddress, port);	
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {
			callback.accept("Failed to connect to server");
		}
		
		while(true) {
			 
			try {
				info = (GameInfo) in.readObject();
				callback.accept("recieved game information from server");
				
				//send waiting for 2 players message
				if(!info.have2players) {
					//callback.accept("waiting for other player");
				}
				//System.out.println(info.playsAgainst);
				//send what other player has played and who won
				if(info.myID == 1 && !info.p2Plays.equalsIgnoreCase("")) {
					callback.accept("Enemy played " + info.p2Plays);
				}
				else if(info.myID == 2 && !info.p1Plays.equalsIgnoreCase("")) {
					callback.accept("Enemy played " + info.p1Plays);
				}
				
				//send who won the round
				if(!info.gameWon && !info.winner.equalsIgnoreCase("None")) {
					if(info.myID == 1 && info.winner.equalsIgnoreCase("Player 1")) {
						callback.accept("You won the round!");
					}
					else if(info.myID == 2 && info.winner.equalsIgnoreCase("Player 2")) {
						callback.accept("You won the round!");
					}
					else {
						callback.accept("You lost the round!");
					}
				}
				System.out.println("** Client (recieve)" );
				info.printElems();
			}
			catch(Exception e) {
				//callback.accept("couldn't recieve game info from server");
			}
			
		}
	
    }
	
	public void send(GameInfo information) {
		try {
			information.playedRound = true;
			System.out.println("** Client (send)" );
			information.printElems();
			out.reset();
			out.writeObject(information);
			out.flush();
			callback.accept("sent game information to server");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
