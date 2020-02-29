import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
	
	Client client;
	
	@BeforeEach
	void init() {
		client = new Client(e->{});
	}

	@Test
	void initTest() {
		assertEquals("Client", client.getClass().getName(), "didn't initalize proper Client");
	}

}
