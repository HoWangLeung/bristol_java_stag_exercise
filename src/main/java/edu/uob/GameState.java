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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class GameState {

    private Location startingLocation;
    private TreeMap<String, Location> locationMap;
    private TreeMap<String, HashSet<GameAction>> actionMap;
    private Storeroom storeroom = new Storeroom();
    private List<Player> playerList = new ArrayList<>();
    private Player currentPlayer;
    private String response;



    public GameState() {

    }

//    private Location currentLocation;
//
//    public Location getCurrentLocation() {
//        return currentLocation;
//    }
//
//    public void setCurrentLocation(Location currentLocation) {
//        this.currentLocation = currentLocation;
//    }

    public void removeItemFromLocation(Artefact artefact){
        getLocationMap().get(currentPlayer.getCurrentLocation().getName()).getArefacts().remove(artefact);
        getLocationMap().get("storeroom").getArefacts().add(artefact);
    }


    public TreeMap<String, Location>  loadEntites(File entitiesFile) {

        Parser parser = new Parser();

        try {
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            ArrayList<Edge> paths = sections.get(1).getEdges();
            TreeMap<String, Location> locationMap= new TreeMap<>();

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
                            artefact.setShape("diamond");
                            newLocation.addArefacts(artefact);
                            this.storeroom.addArtefact(artefact);
                        });


                    }

                    if(type.equalsIgnoreCase("furniture")){
                        ArrayList<Node> allFurnitures = subgraphs.get(j).getNodes(false);
                        allFurnitures.forEach(f->{
                            Furniture furniture = new Furniture(f.getId().getId(),f.getAttribute("description"));
                            furniture.setShape("hexagon");
                            newLocation.addFurnitures(furniture);
                            this.storeroom.addFurniture(furniture);
                        });



                    }

                    if(type.equalsIgnoreCase("characters")){
                        ArrayList<Node> allCharacters = subgraphs.get(j).getNodes(false);
                        allCharacters.forEach(c->{
                            Character character = new Character(c.getId().getId(),c.getAttribute("description"));
                            character.setShape("ellipse");
                            newLocation.addCharacter(character);
                            this.storeroom.addCharacter(character);
                        });
                    }

                }
                locationMap.put(newLocation.getName(),newLocation);

            }



            setLocationMap(locationMap);
            return locationMap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void loadActions(TreeMap<String, Location> locationMap, File actionsFile) {

        DocumentBuilder builder = null;
        TreeMap<String, HashSet<GameAction>> actionsTree = new TreeMap<String, HashSet<GameAction>>();

        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();

            NodeList actions = root.getChildNodes();
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

             //   System.out.println("triggers==" + triggers.getTextContent());

                newGameAction.retriveTriggers(triggers);
                newGameAction.retriveSubjects(subjects,locationMap);
                newGameAction.retriveConsumed(consumed,locationMap);
                newGameAction.retriveProduced(produced,locationMap,this);
                newGameAction.retriveNarration(narration);

                // System.out.println(newGameAction.g);


                //System.out.println(newGameAction.getTriggers() + "<<<<");
                gameActionHashSet.add(newGameAction);



                newGameAction.getTriggers().forEach(trigger->{
                    if(actionsTree.get(trigger)!=null){
                        HashSet<GameAction> setTobeUpdated = actionsTree.get(trigger);
                        setTobeUpdated.add(newGameAction);
                        actionsTree.put(trigger,setTobeUpdated);

                    }else{
                        actionsTree.put(trigger,gameActionHashSet);


                    }


                });


            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        HashSet<GameAction> gameActionHashSet1 = actionsTree.get("open");


          this.setStartingLocation(startingLocation);


        setActionMap(actionsTree);

    }

    public Location getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(Location startingLocation) {
        this.startingLocation = startingLocation;
    }

    public TreeMap<String, Location> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(TreeMap<String, Location> locationMap) {
        this.locationMap = locationMap;
    }

    public TreeMap<String, HashSet<GameAction>> getActionMap() {
        return actionMap;
    }

    public void setActionMap(TreeMap<String, HashSet<GameAction>> actionMap) {
        this.actionMap = actionMap;
    }

    public Storeroom getStoreroom() {
        return storeroom;
    }

    public void setStoreroom(Storeroom storeroom) {
        this.storeroom = storeroom;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void addPlayer(Player player) {
        this.playerList.add(player);
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        if(response!=null)
            response=response.trim();
        this.response = response;
    }

}
