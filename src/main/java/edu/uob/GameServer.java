package edu.uob;

    import com.alexmerz.graphviz.ParseException;
    import com.alexmerz.graphviz.Parser;
    import com.alexmerz.graphviz.objects.Edge;
    import com.alexmerz.graphviz.objects.Graph;
    import com.alexmerz.graphviz.objects.Node;
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

/** This class implements the STAG server. */
public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here

        HashMap<String, Location> locationMap = loadEntites();
        System.out.println("---> " + locationMap.get("forest").getName());


        handleCommand("chop tree with axe");
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        System.out.println("current command = " + command);
        // TODO implement your server logic here
        HashMap<String, Location> locationMap = loadEntites();
        
        
        loadActions();
        
        
        
        
        



        return "Thanks for your message: " + command;
    }

    private void loadActions() {
        DocumentBuilder builder = null;
        TreeMap<String, HashSet<GameAction>> actionsTree = new TreeMap<String, HashSet<GameAction>>();

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();

//            Element firstAction = (Element)actions.item(1);
//            Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
//            String firstTriggerPhrase = triggers.getElementsByTagName("keyword").item(1).getTextContent();
//            System.out.println("firstAction=" + firstTriggerPhrase);
            System.out.println("action = " + (Element)actions);






        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }

    private  HashMap<String, Location> loadEntites() {

        Parser parser = new Parser();

        try {
            FileReader reader = new FileReader("config" + File.separator + "extended-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();

            ArrayList<Edge> paths = sections.get(1).getEdges();
//            paths.forEach(p-> System.out.println(p.getSource().getNode().getId()));

            HashMap<String, Location> locationMap= new HashMap<>();

            for(int i =0;i<locations.size();i++){
                Graph location = locations.get(i);

                Node locationDetail = location.getNodes(false).get(0);
                Location newLocation = new Location(locationDetail.getId().getId(),locationDetail.getAttribute("description"));
                ArrayList<Graph> subgraphs = location.getSubgraphs();

                //System.out.println("location ====> " + newLocation.getName());

                paths.forEach(p-> {
                    String fromLocation = p.getSource().getNode().getId().getId();
                    String toLocation = p.getTarget().getNode().getId().getId();
                    if(newLocation.getName().equalsIgnoreCase(fromLocation)){
                        Path pathToAdd = new Path(toLocation,p.getTarget().getNode().getAttribute("description"));
                        newLocation.addPath(pathToAdd);
                    }
                });




                for(int j = 0;j<subgraphs.size();j++){
                    String type = subgraphs.get(j).getId().getId();

                    if(type.equalsIgnoreCase("artefacts")){
                        ArrayList<Node> allArtefacts = subgraphs.get(j).getNodes(false);
                        allArtefacts.forEach(a->{
                            Artefact artefact = new Artefact(a.getId().getId(),a.getAttribute("description"));
                            newLocation.addArefacts(artefact);
                        });


                    }

                    if(type.equalsIgnoreCase("furniture")){
                        ArrayList<Node> allFurnitures = subgraphs.get(j).getNodes(false);
                        allFurnitures.forEach(f->{
                            Furniture furniture = new Furniture(f.getId().getId(),f.getAttribute("description"));
                            newLocation.addFurnitures(furniture);
                        });



                    }

                    if(type.equalsIgnoreCase("characters")){
                        ArrayList<Node> allCharacters = subgraphs.get(j).getNodes(false);
                        allCharacters.forEach(c->{
                            Character character = new Character(c.getId().getId(),c.getAttribute("description"));
                            newLocation.addCharacters(character);
                        });
                    }

                }
                locationMap.put(newLocation.getName(),newLocation);

            }



            return locationMap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }
}
