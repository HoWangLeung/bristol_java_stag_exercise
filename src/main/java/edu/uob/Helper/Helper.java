package edu.uob.Helper;

import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.subEntities.Location;
import edu.uob.subEntities.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Helper {

    private Player currentPlayer;

   public String getShapeByName(String name){


       return "a shape";
   }

    public void registerPlayer(GameState gameState,String command) {
        ArrayList<String> splitbyColon = new ArrayList<>(Arrays.asList(command.split(":")));
        String playerName = splitbyColon.get(0);
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
}
