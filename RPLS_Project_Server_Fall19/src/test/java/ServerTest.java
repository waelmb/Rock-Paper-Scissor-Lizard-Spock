import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ServerTest {

	Server server;
	
	@BeforeEach
	void init() {
		server = new Server(data -> {});
	}
	
	@Test
	void initTest() {
		assertEquals("Server", server.getClass().getName(), "didn't initalize proper Server");
	}

}
