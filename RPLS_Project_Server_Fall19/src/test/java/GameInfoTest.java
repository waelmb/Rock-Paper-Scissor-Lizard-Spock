import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameInfoTest {
	GameInfo info;
	
	@BeforeEach
	void init() {
		info = new GameInfo();
	}
	
	@Test
	void initTest() {
		assertEquals("GameInfo", info.getClass().getName(), "didn't initalize proper GameInfo");
	}
	
	@Test
	void playTest() {
		info.myID = 1;
		info.play("Paper");
		assertEquals("Paper", info.p1Plays);
		assertEquals("", info.p2Plays);
		assertTrue(info.playedRound);
		info.p1Plays = "";
		info.myID = 2;
		info.playedRound = false;
		info.play("Paper");
		assertEquals("Paper", info.p2Plays);
		assertEquals("", info.p1Plays);
		assertTrue(info.playedRound);
		
	}
	
	@Test
	void whoWonTest() {
		info.gameWon = true;
		info.myID = 1;
		info.p1Won = true;
		assertEquals(1, info.whoWon());
		info.p1Won = false;
		info.p2Won = true;
		assertEquals(2, info.whoWon());
		info.p2Won = false;
		info.gameWon = false;
		assertEquals(0, info.whoWon());
	}
}
