package edu.uob;

    import com.alexmerz.graphviz.ParseException;
    import com.alexmerz.graphviz.Parser;
    import com.alexmerz.graphviz.objects.Edge;
    import com.alexmerz.graphviz.objects.Graph;
    import com.alexmerz.graphviz.objects.Node;
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

/** This class implements the STAG server. */
public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private List<Player> playerList = new ArrayList<>();
    Location startingLocation;
    private Player currentPlayer;
    GameState gameState = new GameState();
    private TreeMap<String, Location> locationMap;
    private TreeMap<String, HashSet<GameAction>> actionMap;
    private CommandHandler commandHandler = new CommandHandler();
    private Helper helper = new Helper();



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

        this.locationMap = loadEntites();
        this.actionMap = loadActions();
        helper.mapItemsWithShapes(locationMap,actionMap);






        gameState.setCurrentLocation(startingLocation);

      handleCommand("David: look");
      handleCommand("David: get axe");
      handleCommand("David: get coin");
      handleCommand("David: get potion");
      handleCommand("David: goto forest");
      handleCommand("David: get key");
//      handleCommand("David: goto cabin");
      handleCommand("David: inv");
      handleCommand("David: unlock key ");



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

        ArrayList<String> splitbyColon = new ArrayList<>(Arrays.asList(command.split(":")));

        String playerName = splitbyColon.get(0);
        System.out.println("playername = " +playerName);
        playerList.forEach(player -> System.out.println("currentplayer: " +player.getName()));

        boolean isPlayerExist = playerList.stream().filter(player -> player.getName().equals(playerName)).collect(Collectors.toList()).size() > 0;

        System.out.println("isPlayerExist="+isPlayerExist);

        if(!isPlayerExist){
            currentPlayer = new Player(playerName,"");
            this.playerList.add(currentPlayer);
        }else{
            currentPlayer = playerList.stream().filter(p -> p.getName().equals(playerName)).collect(Collectors.toList()).get(0);
        }


        System.out.println(playerList);


        List<String> commands = new ArrayList<>(Arrays.asList(splitbyColon.get(1).trim().split(" ")));

        System.out.println(commands + " <commands");
//        commands.forEach(s-> System.out.println(s));
        String basicResponse = checkBasicCommand(commands, currentPlayer,locationMap,actionMap);
        System.out.println("basicResponse==="+basicResponse);

        currentPlayer.getInventory().forEach(d-> System.out.println(d.getName() + " <<<< got"));

        if(basicResponse!=null){
            System.out.println("will return:\n");
            System.out.println(basicResponse);
            System.out.println("====================================================================================");
            return basicResponse;
        }

        List<String> triggerResult = commandHandler.checkTrigger(commands, currentPlayer, locationMap, actionMap, gameState);

        if(triggerResult.size()>0){
            System.out.println("will return:");
            System.out.println(triggerResult.toString());
            return triggerResult.toString();
        }




        System.out.println("will return: Thank you for your message, nothing\n");
        return "Thanks for your message: " + command;
    }



    public String checkBasicCommand(List<String> commands, Player player, TreeMap<String, Location> locationMap, TreeMap<String, HashSet<GameAction>> actionMap){
        System.out.println("checkBasicCommand");
        String firstWord = commands.get(0);
        StringBuilder stringBuilder;
        Location currentLocation;
        String name;
        String description;
        String locationDescription;


        switch (firstWord){
            case "inv":
            case "inventory":
                System.out.println("inventory...");
                System.out.println("ans "+ player.getInventory().toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("Your inventory has the following items:\n");
                player.getInventory().forEach(i->stringBuilder.append(i.getDescription()+"\n"));


                return stringBuilder.toString();

            case "get":
                System.out.println("get...");
                String target = commands.get(1);
                System.out.println("now in ====> " + gameState.getCurrentLocation().getName());
                List<Artefact> targetArtefect = locationMap.get(gameState.getCurrentLocation().getName()).getArefacts().stream().filter(artefact -> artefact.getName().equalsIgnoreCase(target)).collect(Collectors.toList());
                if(targetArtefect.size()>0){
                    System.out.println("found");
                    player.addToInventory(targetArtefect.get(0));
                }else{
                    System.out.println("no such");
                }

                return "You picked up a " + targetArtefect.get(0).getName();
            case "drop":
                System.out.println("drop...");
                break;
            case "goto":
                System.out.println("goto...");
                stringBuilder = new StringBuilder();
                 target = commands.get(1);
                System.out.println("target goto="+target);
                gameState.setCurrentLocation(locationMap.get(target));
                 currentLocation = locationMap.get(gameState.getCurrentLocation().getName());
                 name = currentLocation.getName();
                 description = currentLocation.getDescription();
                 locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);
                currentLocation.getArefacts().forEach(d-> stringBuilder.append(d.getDescription()+"\n")    );
                currentLocation.getFurnitures().forEach(d-> stringBuilder.append(d.getDescription()+"\n")    );


                stringBuilder.append("You can access from here:\n");
                currentLocation.getPaths().forEach(p-> stringBuilder.append(p.getName()+"\n"));




                return stringBuilder.toString();

            case "look":
                System.out.println("look...");
                stringBuilder = new StringBuilder();
                 currentLocation = locationMap.get(startingLocation.getName());
                 name = currentLocation.getName();
                 description = currentLocation.getDescription();
                 locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);
                currentLocation.getArefacts().forEach(d-> stringBuilder.append(d.getDescription()+"\n")    );
                stringBuilder.append("You can access from here:\n");
                currentLocation.getPaths().forEach(p-> stringBuilder.append(p.getName()+"\n"));




                return stringBuilder.toString();

            default:
                System.out.println("nothing match...");
                return null;
        }
    return null;

    }

    private TreeMap<String, HashSet<GameAction>> loadActions() {
        DocumentBuilder builder = null;
        TreeMap<String, HashSet<GameAction>> actionsTree = new TreeMap<String, HashSet<GameAction>>();

        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "extended-actions.xml");
            Element root = document.getDocumentElement();

            NodeList actions = root.getChildNodes();
            System.out.println("actions length = " + actions.getLength());
            //for each action
            for(int i = 1; i<=actions.getLength()-2;i+=2){
                HashSet<GameAction> gameActionHashSet = new HashSet<>();
                GameAction newGameAction = new GameAction();
                Element allActions = (Element)actions.item(i);

                Element triggers = (Element)allActions.getElementsByTagName("triggers").item(0);
                Element subjects = (Element)allActions.getElementsByTagName("subjects").item(0);

                Element consumed = (Element)allActions.getElementsByTagName("consumed").item(0);
                Element produced = (Element)allActions.getElementsByTagName("produced").item(0);
                Element narration = (Element)allActions.getElementsByTagName("narration").item(0);

                newGameAction.retriveTriggers(triggers);
                newGameAction.retriveSubjects(subjects);
                newGameAction.retriveConsumed(consumed);
                newGameAction.retriveProduced(produced);
                newGameAction.retriveNarration(narration);

               // System.out.println(newGameAction.g);


                //System.out.println(newGameAction.getTriggers() + "<<<<");
                gameActionHashSet.add(newGameAction);

                newGameAction.getTriggers().forEach(trigger->{

                    actionsTree.put(trigger,gameActionHashSet);
                });
               // System.out.println("************end loop*********");

         }


//            System.out.println("------> " + actionsTree);





        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        HashSet<GameAction> gameActionHashSet1 = actionsTree.get("open");

        gameActionHashSet1.forEach(s-> System.out.println(s.getProduced()+ " <") );

        return actionsTree;

    }

    private  TreeMap<String, Location> loadEntites() {

        Parser parser = new Parser();

        try {
            FileReader reader = new FileReader("config" + File.separator + "extended-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();

            ArrayList<Edge> paths = sections.get(1).getEdges();
//            paths.forEach(p-> System.out.println(p.getSource().getNode().getId()));

            TreeMap<String, Location> locationMap= new TreeMap<>();

            System.out.println("starrting = " + locations.get(0).getNodes(false).get(0));

            Node node = locations.get(0).getNodes(false).get(0);

            this.startingLocation= new Location(locations.get(0).getNodes(false).get(0).getId().getId(),locations.get(0).getNodes(false).get(0).getAttribute("description"));



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

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void addPlayer(Player player) {
        this.playerList.add(player);
    }
}
