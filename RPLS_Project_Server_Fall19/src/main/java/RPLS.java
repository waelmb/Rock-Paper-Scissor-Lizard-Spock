/* Project 4: RPSLS with multithreading
 * Wael Mobeirek - wmobei2@uic.edu
 * Enoc Carranza - ecarra6@uic.edu
 * Taha khomusi - tkhomu2@uic.edu
 * Hasan Sehwail - hsehwa2@uic.edu
 * Rock Paper Scissors Lizard Spock networked server and client programs that support multiple clients at once by multithreading.
 */
import java.io.IOException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*
 * Server program
 */
public class RPLS extends Application {

	Text header, pCount,history1, serverMsgs1;
	TextField portField;
	Button serverSwitch, quitButton;
	HBox portHBox;
	HashMap<String, Scene> sceneMap;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	int port;
	ListView<Integer> integerList = new ListView<Integer>();
	ListView<String> clientList = new ListView<String>();

	ListView<String> client1Messages, client2Messages;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("RPSLS Server");

		//UI Objects
		serverSwitch = new Button("Turn on Server");
		serverSwitch.setStyle("-fx-pref-width: 300px");
		header = new Text("RPSLS Server");
		Text portText = new Text("port: ");
		TextField portField = new TextField("1738");
		portHBox = new HBox(portText, portField);
		quitButton = new Button("Quit");



		startPane = new BorderPane();
		startPane.setTop(header);
		startPane.setCenter(portHBox);
		startPane.setBottom(serverSwitch);
		startPane.setPadding(new Insets(20));

		startScene = new Scene(startPane, 400,600);

		client1Messages = new ListView<String>();
		client2Messages = new ListView<String>();



		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());

		quitButton.setOnAction(e->{System.exit(0);});

		serverSwitch.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						integerList.getItems().clear();
						//loop through clients, and store count for each client in list
						for(int i = 0; i < serverConnection.clients.size(); i++) {
							integerList.getItems().add(serverConnection.clients.get(i).count);
						}

						clientList.getItems().clear();
						for(int i = 0; i < serverConnection.clients.size(); i++) {

							clientList.getItems().add(integerList.getItems().get(i).toString());

						}
						client1Messages.getItems().add(0, data.toString());

						//update player count
						pCount.setText("Number of clients: " + (serverConnection.clients.size()));

					});
				});
				//get ip and port

				try {
					port = Integer.valueOf(portField.getText());
					serverConnection.port = port;
				}
				catch(Exception c) {
					client1Messages.getItems().add(0, "Improper port numer. Server setup failed.");
				};

		});



		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                try {
					serverConnection.mySocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Couldn't close server socket properly");
				}
            }
        });



		primaryStage.setScene(startScene);
		primaryStage.show();
	}

	//updated server gui, just the skeleton until we get everything else working
	public Scene createServerGui() {
		header = new Text("RPSLS Server - Game Results");
		history1 = new Text("History: ");
		serverMsgs1 = new Text("Game Results: Who played who & winner/loser ");
		pCount = new Text("Number of clients: 0");
		client1Messages = new ListView<String>();
		clientList = new ListView<String>();
		VBox serverVBox = new VBox(150, header, serverMsgs1);
		VBox clientBox = new VBox(10, pCount, clientList, quitButton);
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: coral");
		pane.setCenter(serverVBox);
		pane.setRight(clientBox);
		pane.setBottom(client1Messages);

		return new Scene(pane, 800, 800);
	}
}
