package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ExtendedCommandTest {

    String err = "[ERROR]";
    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    // Test to spawn a new server and send a simple "look" command
    @Test
    void clearingRun() {
        server.handleCommand("player 1: look");
         server.handleCommand("player 1: get axe");
        server.handleCommand("player 1: get coin");


      server.handleCommand("player 1: goto forest");
        server.handleCommand("player 1: get key");
       server.handleCommand("player 1: chop tree");

        String lookForest  = server.handleCommand("player 1: look");
        assertTrue(lookForest.contains("cabin"));
        assertTrue(lookForest.contains("riverbank"));

        server.handleCommand("player 1: goto cabin");
        server.handleCommand("player 1: unlock with key");
        String lookCabin  = server.handleCommand("player 1: look");

        assertTrue(lookCabin.contains("cellar"));
        server.handleCommand("player 1: goto cellar");
        server.handleCommand("player 1: pay elf");

        String invShovel =  server.handleCommand("player 1: inv");
        assertTrue(invShovel.contains("shovel"));
        assertTrue(!invShovel.contains("coin"));


         server.handleCommand("player 1: goto riverbank");
         server.handleCommand("player 1: bridge river with log");

        String inv2 = server.handleCommand("player 1: inv");
        assertTrue(!inv2.contains("log"));

        String look2 = server.handleCommand("player 1: look");
        assertTrue(look2.contains("clearing"));

        String gotoClearing = server.handleCommand("player 1: goto clearing");
        assertTrue(gotoClearing.contains("riverbank"));

        server.handleCommand("player 1: look");
        assertTrue(gotoClearing.contains("soil"));

        String digWithShovel = server.handleCommand("player 1: dig with shovel");

        String invDig = server.handleCommand("player 1: inv");

        String lookAfterDig = server.handleCommand("player 1: look");

        assertTrue(!lookAfterDig.contains("soil"));
        String digWithShovelAgainResult = server.handleCommand("player 1: dig with shovel");
        assertTrue(digWithShovelAgainResult.contains("You do not have the require item"));

         server.handleCommand("player 1: drop shovel");

        String lookAfterDrop = server.handleCommand("player 1: look");
    }
    @Test
    void testMultiplayer() {
       server.handleCommand("player 1: get axe");

         server.handleCommand("player 2: get potion");

        server.handleCommand("player 1: inv");

        server.handleCommand("player 2: inv");

        server.handleCommand("player 1: look");

    }




}
