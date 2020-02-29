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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{

	int count = 1;
	int gameCount = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<Game> games = new ArrayList<Game>();
	TheServer server;
	private Consumer<Serializable> callback;
	int port;
	ServerSocket mySocket;
	
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	public class TheServer extends Thread{
		
		public void run() {
			
			try(ServerSocket mysocket = new ServerSocket(port);){
				mySocket = mysocket;
			    while(true) {
			
					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();
					System.out.println("A new client #" + count + " connected to the server");
					count++;
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
		
		class Game {
			int gameNum;
			GameInfo gameInfo;
			int client1;
			int client2;
			boolean has2players;
			
			Game(int gameNum, int client1, int client2) {
				this.gameNum = gameNum;
				gameInfo = new GameInfo();
				this.client1 = client1;
				this.client2 = client2;
				has2players = has2playersCheck();
			}
			
			//makes sure that both clients are still there, and update has two players
			public boolean has2playersCheck() {
				boolean p1Exists = false;
				boolean p2Exists = false;
				
				for(int i = 0; i < clients.size(); i++) {
					if( clients.get(i).count == client1) {
						p1Exists = true;
					}
					
					if( clients.get(i).count == client2) {
						p2Exists = true;
					}
				}
				
				if(p1Exists && p2Exists) {
					return true;
				} 
				else {
					return false;
				}
			}
		}

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			boolean isAvailable;
			ArrayList<Integer> list;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
				isAvailable = true;
				list = new ArrayList<Integer>();
			}
				
			//TODO: update to support multiple clients
			public void run(){
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
	                updateClientsInfo();
	                while(true) {
	                	//Receive attempt
					    try {
					    	//read stream
					    	GameInfo g = (GameInfo) in.readObject();
					    	System.out.println("** Server read stream: GameInfo g" );
							g.printElems();

					    	//if this is a challenge attempt or not
							if (g.challenge == true) {
								//Check if both clients are available
								//figure out other client's index
								int challengedClientIndex = -1;
								for(int i = 0; i < clients.size(); i++) {
									if(clients.get(i).count == g.playsAgainst) {
										challengedClientIndex = i;
										break;
									}
								}

								if(this.isAvailable && clients.get(challengedClientIndex).isAvailable) {
									//if yes, create a game and set challenge attempt to false
									createGame(this.count, g.playsAgainst);
									g.challenge = false;
									
									//set clients to not available
									this.isAvailable = false;
									clients.get(challengedClientIndex).isAvailable = false;
									
									//update other clients that these two clients aren't available anymore
									updateClientsInfo();
								}
								else {
									callback.accept("Client " + this.count + " is challenging an unavailable client " + g.playsAgainst);
									//TODO: send something to client?
								}
								
								//update available clients
								updateClientsInfo();
							}
							//handle playing again
							else if(g.playingAgain) {
								//go to client clients arrayList
								//set isAvailable to true
								this.isAvailable = true;
								
								//update other clients that this client is available again
								updateClientsInfo();
							}
							//otherwise, this is a gameInfo update, so update the respective game
							else {
								int gameIndex = -1;
								
								//figure out what game to update
								for (int i = 0; i < games.size(); i++){
									//TODO: check if this right
									if (games.get(i).gameInfo.gameNum == g.gameNum){
										gameIndex = i;
									}
								}

								//figure out what client is this
								int client = g.myID;
								//update gameInfo in Game
								if (client == 1){
									games.get(gameIndex).gameInfo.p1Plays = g.p1Plays;
									games.get(gameIndex).gameInfo.p1Points = g.p1Points;
								}
								else {
									games.get(gameIndex).gameInfo.p2Plays = g.p2Plays;
									games.get(gameIndex).gameInfo.p2Points = g.p2Points;
								}
								
								//evaluate game if both players played
								if(!games.get(gameIndex).gameInfo.p1Plays.equalsIgnoreCase("") && !games.get(gameIndex).gameInfo.p2Plays.equalsIgnoreCase("")) {
									//evaluate game
									games.get(gameIndex).gameInfo.winner = evalRoundWinner(gameIndex);
									evalGameWinner(gameIndex);
									
									//update the clients in the game
									updateGamesInfo(gameIndex);
												
									//delete the game from the arrayList when the game is over
									games.remove(gameIndex);
								}
							}// end else
					    	
					    } // end try
					    catch(Exception e) {
					    	
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	//updateClients("Client #"+count+" has left the server!");type name = new type();
					    	//updateClientsInfo(gameInfo);
					    	try {
								this.connection.close();
							} catch (IOException e1) {

								callback.accept("Couldn't close socket");
							}
					    	
					    	clients.remove(this);
					    	//update available clients that a client left
					    	updateClientsInfo();
					    	break;
					    }
					}
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
					
				 
			}//end of run
				
			//used to update clients of a single game
			public void updateGamesInfo(int gameIndex) {
				//System.out.println("Game Index: " + gameIndex);
				int client1 = games.get(gameIndex).client1;
				int client2 = games.get(gameIndex).client2;
				GameInfo data = games.get(gameIndex).gameInfo;
				data.gameNum = games.get(gameIndex).gameNum;
				
				//update myCount
				data.myCount = this.count;
				
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					
					//update myCount
					data.myCount = t.count;
					
					//if this is one of the clients in the game
					if(client1 == t.count || client2 == t.count) {
						try {
							if(client1 == t.count) {
								data.myID = 1;
								data.playsAgainst = client2;
							}
							else if (client2 == t.count){
								data.myID = 2;
								data.playsAgainst = client1;
								System.out.println("client2 playsAgainst: " + data.playsAgainst);
							}
							t.updateClientsArrayList();
							System.out.println("** Server Game output for game index: " + gameIndex + " gameNum: " + data.gameNum + " for client: " + t.count);
							data.printElems();
							t.out.reset();
							t.out.writeObject(data);
							t.out.flush();
							callback.accept("sent game information to " + "client #" + t.count);
						}
						catch(Exception e) {callback.accept("this broke bro fix ur shit");}
					}
				}
			}
			
			//used to update clients that are available (not in-game)
			public void updateClientsInfo() {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					if(t.isAvailable) {
						try {
							//create a new game info 
							GameInfo data = new GameInfo();
							
							//update available clients ArrayList
							t.updateClientsArrayList();
							data.clients = t.list;
							
							//update myCount
							data.myCount = t.count;
									
							System.out.println("** Server Clients output for clients: client " + t.count);
							data.printElems();
							//write stream
							t.out.reset();
							t.out.writeObject(data);
							t.out.flush();
							callback.accept("sent client information to " + "client #" + t.count);
						}
						catch(Exception e) {}
					}
				}
			}
			
			//creates a new game for clients that are ready to play
			public void createGame(int client1, int client2) {
				//determine the lower number client
				if(client2 > client1) {
					int swap = client1;
					client1 = client2;
					client1 = swap;
				}
								
				//create a new game
				Game newGame = new Game(gameCount, client1, client2);
				System.out.println("New game has been created with gameNum:" + gameCount + " and curr-index:" + (games.size()));
				
				//increment gameCount
				gameCount++;
				
				//if has2players, add the game to games ArrayList
				if(newGame.has2players) {
					games.add(newGame);

					System.out.println(2.9);
					//update both clients
					updateGamesInfo(games.size()-1);
				}
				
				//send a message to the server
				callback.accept("client "+ client1 +" is playing client " + client2);

			}
			
			//creates an arrayList of Integer of other clients on the server
			public void updateClientsArrayList() {
				list.clear();
				//loop through clients, and store count for each client in list
				for(int i = 0; i < clients.size(); i++) {
					if(clients.get(i).isAvailable) {
						list.add(clients.get(i).count);
					}
				}

			}
			
		}//end of client thread
		
		
		/*
		 * Game Logic methods
		 */
		
		//Evaluate game's winner
		public void evalGameWinner(int gameIndex) {
			if(games.get(gameIndex).gameInfo.p1Points >= 1) {
				games.get(gameIndex).gameInfo.gameWon = true; 
				games.get(gameIndex).gameInfo.p1Won = true;
				callback.accept("client "+ games.get(gameIndex).client1 +" won against client " + games.get(gameIndex).client2);
			}
			
			else if(games.get(gameIndex).gameInfo.p2Points >= 1) {
				games.get(gameIndex).gameInfo.gameWon = true;
				games.get(gameIndex).gameInfo.p2Won = true;
				callback.accept("client "+ games.get(gameIndex).client2 +" won against client " + games.get(gameIndex).client1);
			}
			
			else{
				games.get(gameIndex).gameInfo.gameWon = true;
				callback.accept("draw between client "+ games.get(gameIndex).client1 +" and client" + games.get(gameIndex).client2);
			}
		}
		
		//TODO: update to support multiple clients
		//Evaluate the round's winner
		public String evalRoundWinner(int gameIndex) {
			String p1Choice = games.get(gameIndex).gameInfo.p1Plays.toString();
			String p2Choice = games.get(gameIndex).gameInfo.p2Plays.toString();
			
			if(p1Choice.equalsIgnoreCase("Scissors") && p2Choice.equalsIgnoreCase("Paper")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Paper") && p2Choice.equalsIgnoreCase( "Scissors")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Scissors") && p2Choice.equalsIgnoreCase("Lizard")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Lizard") && p2Choice.equalsIgnoreCase("Scissors")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Paper") && p2Choice.equalsIgnoreCase("Spock")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Spock") && p2Choice.equalsIgnoreCase("Paper")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Paper") && p2Choice.equalsIgnoreCase("Rock")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Rock") && p2Choice.equalsIgnoreCase("Paper")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Rock") && p2Choice.equalsIgnoreCase("Scissors")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Scissors") && p2Choice.equalsIgnoreCase("Rock")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Rock") && p2Choice.equalsIgnoreCase("Lizard")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Lizard") && p2Choice.equalsIgnoreCase("Rock")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Lizard") && p2Choice.equalsIgnoreCase("Paper")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Paper") && p2Choice.equalsIgnoreCase("Lizard")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Lizard") && p2Choice.equalsIgnoreCase("Spock")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Spock") && p2Choice.equalsIgnoreCase("Lizard")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Spock") && p2Choice.equalsIgnoreCase("Scissors")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Scissors") && p2Choice.equalsIgnoreCase("Spock")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else if(p1Choice.equalsIgnoreCase("Spock") && p2Choice.equalsIgnoreCase("Rock")) {
				games.get(gameIndex).gameInfo.p1Points++;
				return "Player 1";
			}
			else if(p1Choice.equalsIgnoreCase("Rock") && p2Choice.equalsIgnoreCase("Spock")) {
				games.get(gameIndex).gameInfo.p2Points++;
				return "Player 2";
			}
			else {
				return "None";
			}
		}
}


	
	

	
