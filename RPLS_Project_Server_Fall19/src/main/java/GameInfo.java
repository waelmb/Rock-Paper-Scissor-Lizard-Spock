/* Project 4: RPSLS with multithreading
 * Wael Mobeirek - wmobei2@uic.edu
 * Enoc Carranza - ecarra6@uic.edu
 * Taha khomusi - tkhomu2@uic.edu
 * Hasan Sehwail - hsehwa2@uic.edu
 * Rock Paper Scissors Lizard Spock networked server and client programs that support multiple clients at once by multithreading.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable {
	int myCount;
	int playsAgainst;
	int gameNum;
	int p1Points;
	int p2Points;
	String p1Plays;
	String p2Plays;
	boolean have2players; //TODO: remove?
	int myID;
	boolean playedRound; //TODO: remove?
	boolean gameWon; //TODO: remove?
	boolean p1Won;
	boolean p2Won;
	boolean resetRequest; //TODO: remove?
	boolean playingAgain;
	String winner;
	boolean challenge;
	ArrayList<Integer> clients;
	
	private static final long serialVersionUID = 5867735837149809485L;
	
	GameInfo() {
		myCount = -1;
		playsAgainst = -1;
		p1Points = 0;
		p2Points = 0;
		p1Plays = "";
		p2Plays = "";
		have2players = false;
		myID = 0;
		playedRound = false;
		gameWon = false;
		p1Won = false;
		p2Won = false;
		resetRequest = false;
		playingAgain = false;
		winner = "None";
		challenge = false;
	}
	
	//set id
	public void setID(int id) {
		myID = id;
	}
	
	//prints data members
	public void printElems() {
		System.out.println("myCount: " + myCount);
		System.out.println("playsAgainst: " + playsAgainst);
		System.out.println("gameNum: " + gameNum);
		System.out.println("p1Points: " + p1Points);
		System.out.println("p2Points: " + p2Points);
		System.out.println("p1Plays " + p1Plays);
		System.out.println("p2Plays " + p2Plays);
		System.out.println("have2players: " + have2players);
		System.out.println("myID: " + myID);
		System.out.println("playedRound: " + playedRound);
		System.out.println("gameWon: " + gameWon);
		System.out.println("p1Won: " + p1Won);
		System.out.println("p2Won: " + p2Won);
		System.out.println("resetRequest: " + resetRequest);
		System.out.println("playingAgain: " + playingAgain);
		System.out.println("winner: " + winner);
		System.out.println("challenge: " + challenge);
		if(clients != null) {
			System.out.print("clients ArrayList<Integer>: ");
			for(int i = 0; i < clients.size(); i++) {
				System.out.print(clients.get(i) + " ");
			}
			System.out.println("");
		}
	}
	
	//determine who played what
	public void play(String choice) {
		if (myID == 1) {
			p1Plays = choice;
			p2Plays = "";
			playedRound = true;
		}
		else if(myID == 2) {
			p1Plays = "";
			p2Plays = choice;
			playedRound = true;
		}
		else {
			p1Plays = "";
			p2Plays = "";
		}
	}
	
	//determine who is the game winner
	//0 no one won yet
	//1 this client won
	//2 other client won
	public int whoWon() {
		if(gameWon) {
			if(myID == 1 && p1Won) {
				return 1;
			}
			else if(myID == 2 && p2Won) {
				return 1;
			}
			else {
				return 2;
			}
		}
		return 0;
	}
}
