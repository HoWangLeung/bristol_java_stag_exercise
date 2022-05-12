package edu.uob.Helper;

import edu.uob.Exception.GameException;
import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.subEntities.Location;
import edu.uob.subEntities.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper {

    private Player currentPlayer;

   public String getShapeByName(String name){


       return "a shape";
   }

    public void registerPlayer(GameState gameState,String command) throws GameException {
        ArrayList<String> splitbyColon = new ArrayList<>(Arrays.asList(command.split(":")));
        String playerName = splitbyColon.get(0);
        this.checkNameFormat(playerName);
        boolean isPlayerExist = gameState.getPlayerList().stream().filter(player -> player.getName().equals(playerName)).collect(Collectors.toList()).size() > 0;

        if (!isPlayerExist) {
            currentPlayer = new Player(playerName, "");
            gameState.getPlayerList().add(currentPlayer);

            gameState.setCurrentPlayer(currentPlayer);
            gameState.getCurrentPlayer().setCurrentLocation(gameState.getStartingLocation());
        } else {
            currentPlayer = gameState.getPlayerList().stream().filter(p -> p.getName().equals(playerName)).collect(Collectors.toList()).get(0);
            gameState.setCurrentPlayer(currentPlayer);

        }
    }

    private void checkNameFormat(String playerName) throws GameException {
        System.out.println("playerName="+playerName);
        Pattern pattern = Pattern.compile("[0-9$&+,:;=?@#|<>.^*()%!]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(playerName);
        boolean matchFound = matcher.find();
        if(matchFound) {
            throw new GameException("Valid player names can consist of uppercase and lowercase letters, spaces, apostrophes and hyphens");
        } else {
            System.out.println("OK?");
        }
    }

    public void checklengthMaxOne(List<String> commands) throws GameException {
        if(commands.size()!=1){throw new GameException("expect exactly one words");}
    }
    public void checklengthMaxTwo(List<String> commands) throws GameException {
        if(commands.size()!=2){throw new GameException("expect exactly two words");}
    }

    public <T> List<T> findIntersection(List<T> list1, List<T> list2) throws GameException {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        if (list.size() == 0) {
            throw new GameException("cannot recognize the command");
        }

        return list;
    }

    public List<String> processCommand(String command) {

        ArrayList<String> splitbyColon = new ArrayList<>(Arrays.asList(command.split(":")));
        return new ArrayList<>(Arrays.asList(splitbyColon.get(1).trim().split(" "))).stream().map(w->w.toLowerCase()).collect(Collectors.toList());
    }
}
