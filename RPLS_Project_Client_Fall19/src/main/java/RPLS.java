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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseEvent;

/*
 * Client program
 */
public class RPLS extends Application {

    TextField portNumber,ipAddr;
    Text header;
    Label ip,port,title, points, playerPoints, oppLastMove, messageFromServer,yourMoves, winner, playAgain, oppLastPLay;
    Button clientChoice,b1, quitButton,challengeButton;
    Button playAgainButtonY;
    HashMap<String, Scene> sceneMap;
    HBox buttonBox, portBox, ipBox, PointsBox, oppBox, gamePieces,topofScreen, playAgainBox,challengeButtonBox;
    VBox clientBox,finalScreenBox, gameOverBox,challengeClientBox;
    Scene startScene;
    BorderPane startPane, pane;
    Client clientConnection;
    ListView<String> listItems, listItems2;
    MenuBar menu;
    MenuItem Exit;
    Image rock, paper, scissors, lizard, spock, p2ChoiceImg;
    ImageView rockView, paperView, scissorsView, lizardView, spockView;
    ImageView oppLastMoveView = new ImageView();


    int myID;
    ListView<String> messagesFromServer,clientList;
    String ipAddress;
    Integer myPort = new Integer(0);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    //feel free to remove the starter code from this method
    @Override
    public void start(Stage primaryStage) throws Exception {

        this.clientChoice = new Button("Play!");
        this.clientChoice.setStyle("-fx-pref-width: 300px");

        port = new Label("Enter Port Number: ");
        ip = new Label("Enter IP address: ");
        title = new Label("ROCK PAPER SCISSORS \nLIZARD SPOCK");
        title.setStyle("-fx-font: 18 arial;");
        portNumber = new TextField("1738");
        ipAddr = new TextField("127.0.0.1");
        portNumber.setMinWidth(80);
        quitButton = new Button("Quit");
        buttonBox = new HBox(clientChoice);
        portBox = new HBox(30,port, portNumber);
        ipBox = new HBox(30,ip,ipAddr);
        VBox startBoxes = new VBox(20,title,portBox,ipBox,buttonBox);
        startPane = new BorderPane();
        startPane.setPadding(new Insets(70));
        startPane.setCenter(startBoxes);

        startScene = new Scene(startPane, 450,350);

        listItems = new ListView<String>();

        b1 = new Button("Send");

        sceneMap = new HashMap<String, Scene>();

        sceneMap.put("client",  createClientGui());
        sceneMap.put("Finished",  createClientFinish());

        //new scene

        sceneMap.put("challenge", createClientChallenge());

        challengeButton.setOnAction(e -> {
	            //primaryStage.setScene(sceneMap.get("client"));
        		System.out.println("challenge button was pressed");
	            clientConnection.send(clientConnection.info);
            }
        );

        quitButton.setOnAction(e->{System.exit(0);});

        playAgainButtonY.setOnAction(e-> {
                //reset client info
                clientConnection.info.resetRequest = true;
                clientConnection.info.playingAgain = true;
                clientConnection.info.playsAgainst = -1;

                System.out.println("Play Again was pressed");
                //reset challenge
                //sceneMap.put("challenge", createClientChallenge());

                
                //update clientList
                updateListview();

                //display challenge
                //sceneMap.put("challenge",createClientChallenge());
                //updateListview();
                listItems2 = listItems;
                primaryStage.setScene(sceneMap.get("challenge"));
                primaryStage.setTitle("RPSLS Client");
        });

        clientChoice.setOnAction(e-> {
            primaryStage.setScene(sceneMap.get("challenge"));
            primaryStage.setTitle("RPSLS Client");
            clientConnection = new Client(data->{
                Platform.runLater(()->{

                    listItems2.getItems().add(0, data.toString());
                    listItems = listItems2;

                    this.myID = clientConnection.info.myID;
                    if(clientConnection.info.playsAgainst != -1){
                        sceneMap.put("client",  createClientGui());
                        primaryStage.setScene(sceneMap.get("client"));
                        primaryStage.setTitle("RPSLS Client");
                    }

                    if(clientConnection.info.gameWon == true){

                        //System.out.println("My ID is: " + myID + "!clientConnection.info.p2Plays.equalsIgnoreCase(): " + !clientConnection.info.p2Plays.equalsIgnoreCase(""));
                        //System.out.println("My ID is: " + myID + "!clientConnection.info.p2Plays.equalsIgnoreCase(): " + !clientConnection.info.p1Plays.equalsIgnoreCase(""));

                        if(myID == 1 && !clientConnection.info.p2Plays.equalsIgnoreCase("")) {
                            //System.out.println("inside first if");
                            if(clientConnection.info.p2Plays.equalsIgnoreCase("Rock")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("rock.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p2Plays.equalsIgnoreCase("Paper")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("paper.png");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p2Plays.equalsIgnoreCase("Scissors")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("scissors.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p2Plays.equalsIgnoreCase("Lizard")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("lizard.png");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p2Plays.equalsIgnoreCase("Spock")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("spock.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }

                        }
                        else if(myID == 2 && !clientConnection.info.p1Plays.equalsIgnoreCase("")) {
                            //System.out.println("inside first if");
                            if(clientConnection.info.p1Plays.equalsIgnoreCase("Rock")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("rock.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p1Plays.equalsIgnoreCase("Paper")) {
                               // System.out.println("inside second if");
                                p2ChoiceImg = new Image("paper.png");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p1Plays.equalsIgnoreCase("Scissors")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("scissors.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p1Plays.equalsIgnoreCase("Lizard")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("lizard.png");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                            else if(clientConnection.info.p1Plays.equalsIgnoreCase("Spock")) {
                                //System.out.println("inside second if");
                                p2ChoiceImg = new Image("spock.jpg");
                                oppLastMoveView.setImage(p2ChoiceImg);
                            }
                        }

                        setImages(oppLastMoveView);

                        if(clientConnection.info.whoWon() == 1){
                            winner.setText("YOU WIN!");
                        }
                        else if(clientConnection.info.whoWon() == 2){
                            winner.setText("YOU LOSE :(");
                        }
                        else if(clientConnection.info.whoWon() == 0){
                            winner.setText("Draw!");
                        }

                        primaryStage.setScene(sceneMap.get("Finished"));
                        primaryStage.setTitle("RPSLS Client");
                        clientConnection.info = new GameInfo();
                        clientConnection.info.playingAgain = true;
                        clientConnection.send(clientConnection.info);
                
                    }
                    //update clientList
                    updateListview();

                    clientList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                           if(newValue != null) {
                        	   System.out.println("ListView selection changed from oldValue = " + oldValue + " to newValue = " + newValue);

                               clientConnection.info.playsAgainst = Integer.parseInt(newValue);
                               clientConnection.info.challenge = true;
                           }
                            
                        }
                    });

                }); //end runLater
            });

            //get ip and port
            try {
                myPort = Integer.valueOf(portNumber.getText());
                ipAddress = ipAddr.getText();
                clientConnection.ipAddress = ipAddress;
                clientConnection.port = myPort;
                clientConnection.start();
            }
            catch(Exception c) {
                messagesFromServer.getItems().add(0, "Inproper port/IP. Connection failed");
            };
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                try {
					clientConnection.socketClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Couldn't close client socket properly");
				}
                //System.exit(0);
            }
        });

        primaryStage.setScene(startScene);
        primaryStage.setTitle("RPSLS Client");
        primaryStage.show();
    }

    public Scene createClientGui() {
        messageFromServer = new Label("Messages From Server : ");
        yourMoves = new Label("Please Select a move :");
        listItems2 = new ListView<String>();
        rock = new Image("Rock.jpg");
        paper = new Image("Paper.png");
        scissors = new Image("Scissors.jpg");
        lizard = new Image("Lizard.png");
        spock = new Image("Spock.jpg");



        rockView = new ImageView(rock);
        paperView = new ImageView(paper);
        scissorsView = new ImageView(scissors);
        lizardView = new ImageView(lizard);
        spockView = new ImageView(spock);

        setImages(rockView);
        setImages(paperView);
        setImages(scissorsView);
        setImages(lizardView);
        setImages(spockView);

        gamePieces = new HBox(70,rockView, paperView, scissorsView, lizardView,spockView);
        gamePieces.setPadding(new Insets(0,0,0,20));


        menu = new MenuBar();
        Menu mOne = new Menu("Exit");
        Exit = new MenuItem("Exit");
        mOne.getItems().add(Exit);
        menu.getMenus().addAll(mOne);



        HBox buttonBox2 = new HBox(b1);
        buttonBox2.setPadding(new Insets(0,0,0,770));

        //listItems2 = listItems;
        clientBox = new VBox(10,messageFromServer,listItems2,yourMoves,gamePieces,buttonBox2);
        clientBox.setPadding(new Insets(30));

        finalScreenBox = new VBox(10,menu,clientBox);

        finalScreenBox.setStyle("-fx-background-color: #dfcaae");

        makeHandlers();

        return new Scene(finalScreenBox, 950, 650);
    }

    public void makeHandlers(){

        int depth = 70; //Setting the uniform variable for the glow width and height
        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLUEVIOLET);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        rockView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Rock pressed ");
            disableEffect();
            rockView.setEffect(borderGlow);
            clientConnection.info.play("Rock");
        });
        paperView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Paper pressed ");
            disableEffect();
            paperView.setEffect(borderGlow);
            clientConnection.info.play("Paper");
        });
        scissorsView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Scissors pressed ");
            disableEffect();
            scissorsView.setEffect(borderGlow);
            clientConnection.info.play("Scissors");
        });
        lizardView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Lizard pressed ");
            disableEffect();
            lizardView.setEffect(borderGlow);
            clientConnection.info.play("Lizard");
        });
        spockView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Spock pressed ");
            disableEffect();
            spockView.setEffect(borderGlow);
            clientConnection.info.play("Spock");
        });


        Exit.setOnAction(x -> {
            Platform.exit();
            System.exit(0);
        });

        b1.setOnAction(e->{
            disableEffect();
            clientConnection.send(clientConnection.info);
        });
    }

    public void disableEffect(){
        rockView.setEffect(null);
        paperView.setEffect(null);
        scissorsView.setEffect(null);
        lizardView.setEffect(null);
        spockView.setEffect(null);
        //oppLastMoveView.setEffect(null);
    }


    public Scene createClientFinish() {

        winner = new Label("YOU SHOULDN'T BE SEEING THIS WIN/LOSE CONDITION");
        oppLastPLay = new Label("Opponent's Last Move:");
        playAgain = new Label("Do You Want To Play Again?");
        playAgainButtonY  = new Button("Play Again");


        winner.setDisable(false);

        playAgainBox = new HBox(30,playAgainButtonY);
        gameOverBox = new VBox(30,winner,oppLastPLay,oppLastMoveView,playAgain,playAgainBox);

        gameOverBox.setStyle("-fx-background-color: #dfcaae");
        gameOverBox.setPadding(new Insets(30));

        return new Scene(gameOverBox, 300, 400);
    }

    public void setImages(ImageView myView){

        myView.setFitWidth(100);
        myView.setPreserveRatio(true);
        myView.setSmooth(true);
        myView.setCache(true);
    }

    public Scene createClientChallenge() {
    header = new Text("RPSLS Client - Challenge a player!");
    challengeButton = new Button("Challenge");
    listItems2 = new ListView<String>();
    pane = new BorderPane();
    clientList = new ListView<>();
    challengeButtonBox = new HBox(50, challengeButton, quitButton);
    challengeClientBox = new VBox(50, header, clientList, listItems2,challengeButtonBox);
    pane.setCenter(challengeClientBox);
    return new Scene(pane, 800, 800);
    }

    public void updateListview(){
        clientList.getItems().clear();
        if(clientConnection.info.clients != null) {
            for (int i = 0; i < clientConnection.info.clients.size(); i++) {
                if(clientConnection.info.clients.get(i) != clientConnection.info.myCount) {
                    clientList.getItems().add(clientConnection.info.clients.get(i).toString());
                }
            }
        }
    }

}