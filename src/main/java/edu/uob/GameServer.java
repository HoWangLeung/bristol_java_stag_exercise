package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.Exception.GameException;
import edu.uob.Helper.Helper;
import edu.uob.actions.CommandHandler;
import edu.uob.subEntities.*;
import edu.uob.subEntities.Character;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the STAG server.
 */
public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;


    private Player currentPlayer;
    GameState gameState = new GameState();

    private CommandHandler commandHandler = new CommandHandler();
    private Helper helper = new Helper();


    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
     * your submission correctly.
     *
     * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
     *
     * @param entitiesFile The game configuration file containing all game entities to use in your game
     * @param actionsFile  The game configuration file containing all game actions to use in your game
     */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here


        TreeMap<String, Location> stringLocationTreeMap = gameState.loadEntites(entitiesFile);
        gameState.loadActions(stringLocationTreeMap, actionsFile);



    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
     * able to mark your submission correctly.
     *
     * <p>This method handles all incoming game commands and carries out the corresponding actions.
     */
    public String handleCommand(String command) {
        try {
            System.out.println("current command = " + command);
            // TODO implement your server logic here

            ArrayList<String> splitbyColon = new ArrayList<>(Arrays.asList(command.split(":")));

            String playerName = splitbyColon.get(0);
            System.out.println("playername = " + playerName);
            gameState.getPlayerList().forEach(player -> System.out.println("currentplayer: " + player.getName()));

            boolean isPlayerExist = gameState.getPlayerList().stream().filter(player -> player.getName().equals(playerName)).collect(Collectors.toList()).size() > 0;

            System.out.println("isPlayerExist=" + isPlayerExist);

            if (!isPlayerExist) {
                currentPlayer = new Player(playerName, "");
                this.gameState.getPlayerList().add(currentPlayer);

                gameState.setCurrentPlayer(currentPlayer);
                gameState.getCurrentPlayer().setCurrentLocation(gameState.getStartingLocation());
            } else {
                currentPlayer = gameState.getPlayerList().stream().filter(p -> p.getName().equals(playerName)).collect(Collectors.toList()).get(0);
                gameState.setCurrentPlayer(currentPlayer);
                gameState.getCurrentPlayer().setCurrentLocation(gameState.getStartingLocation());
            }



            List<String> commands = new ArrayList<>(Arrays.asList(splitbyColon.get(1).trim().split(" ")));

            System.out.println(commands + " <commands");
//        commands.forEach(s-> System.out.println(s));
            String basicResponse = commandHandler.checkBasicCommand(commands, currentPlayer, gameState);


            currentPlayer.getInventory().forEach(d -> System.out.println(d.getName() + " <<<< got"));

            if (basicResponse != null) {
                System.out.println("will return:");
                System.out.println(basicResponse);
                System.out.println("====================================================================================");
                return basicResponse;
            }

            List<String> triggerResult = commandHandler.checkTrigger(commands, currentPlayer, gameState);

            if (triggerResult.size() > 0) {
                System.out.println("will return:");
                System.out.println(triggerResult.toString());
                return triggerResult.toString();
            }


            System.out.println("will return: Thank you for your message, nothing\n");
            return "Thanks for your message: " + command;
        } catch (GameException e) {
            return e.getLocalizedMessage();
        }


    }


    //  === Methods below are there to facilitate server related operations. ===

    /**
     * Starts a *blocking* socket server listening for new connections. This method blocks until the
     * current thread is interrupted.
     *
     * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
     * you want to.
     *
     * @param portNumber The port to listen on.
     * @throws IOException If any IO related operation fails.
     */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
     * Handles an incoming connection from the socket server.
     *
     * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
     * * you want to.
     *
     * @param serverSocket The client socket to read/write from.
     * @throws IOException If any IO related operation fails.
     */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if (incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }


}
