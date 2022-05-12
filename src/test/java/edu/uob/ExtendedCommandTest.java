package edu.uob;

import com.sun.source.tree.AssertTree;
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
     assertTrue(server.handleCommand("player 1: look").contains("log"));

       String lookForest  = server.handleCommand("player 1: look");
         assertTrue(lookForest.contains("cabin"));
        assertTrue(lookForest.contains("riverbank"));

        assertTrue(server.handleCommand("player 1: get log").contains("You picked up a log"));

        server.handleCommand("player 1: goto cabin");
        server.handleCommand("player 1: unlock with key");
        String lookCabin  = server.handleCommand("player 1: look");

        assertTrue(lookCabin.contains("cellar"));
        server.handleCommand("player 1: goto cellar");

        assertTrue(server.handleCommand("player 1: inv").contains("coin"));
        server.handleCommand("player 1: pay elf");
        assertTrue(!server.handleCommand("player 1: inv").contains("coin"));

//        assertTrue(server.handleCommand("player 1: look").contains("shovel"));
//        assertTrue(server.handleCommand("player 1: get shovel").contains("shovel"));
//        String invShovel =  server.handleCommand("player 1: inv");
//        assertTrue(invShovel.contains("shovel"));
//        assertTrue(!invShovel.contains("coin"));
//
//
//         server.handleCommand("player 1: goto riverbank");
//         server.handleCommand("player 1: bridge river with log");
//
//        String inv2 = server.handleCommand("player 1: inv");
//        assertTrue(!inv2.contains("log"));
//
//        String look2 = server.handleCommand("player 1: look");
//        assertTrue(look2.contains("clearing"));
//
//        String gotoClearing = server.handleCommand("player 1: goto clearing");
//        assertTrue(gotoClearing.contains("riverbank"));
//
//        server.handleCommand("player 1: look");
//        assertTrue(gotoClearing.contains("soil"));
//
//        String digWithShovel = server.handleCommand("player 1: dig with shovel");
//
//       assertTrue(!server.handleCommand("player 1: inv").contains("gold"));
//
//      String lookAfterDig = server.handleCommand("player 1: look");
//
//         assertTrue(!lookAfterDig.contains("soil"));
//        assertTrue(lookAfterDig.contains("gold"));
//
//        assertTrue(server.handleCommand("player 1: get gold").contains("gold"));
//        assertTrue(server.handleCommand("player 1: inv").contains("gold"));
//
//
//        String digWithShovelAgainResult = server.handleCommand("player 1: dig with shovel");
//        assertTrue(digWithShovelAgainResult.contains("You do not have the require item"));
//
//         server.handleCommand("player 1: drop shovel");
//
//        String lookAfterDrop = server.handleCommand("player 1: look");
    }
    @Test
    void testMultiplayer() {
       server.handleCommand("player 1: get axe");

         server.handleCommand("player 2: get potion");

         assertTrue( server.handleCommand("player 1: inv").contains("axe"));
        assertTrue( !server.handleCommand("player 1: inv").contains("potion"));



        assertTrue( !server.handleCommand("player 2: inv").contains("axe"));
        assertTrue( server.handleCommand("player 2: inv").contains("potion"));


        server.handleCommand("player 1: look");



    }

    @Test
    void testMultiplayer2() {
        server.handleCommand("player 1: get axe");
        server.handleCommand("player 1: goto forest");
        server.handleCommand("player 2: goto forest");
        server.handleCommand("player 2: goto riverbank");

        server.handleCommand("player 3: get potion");

        server.handleCommand("player 2: look");

        server.handleCommand("player 3: look");



    }

    @Test
    void getAxe() {
        assertTrue(server.handleCommand("player 1: get axe").contains("You picked up"));
        assertTrue(server.handleCommand("player 1: drop axe").contains("dropped"));
        assertTrue(!server.handleCommand("player 1: inv").contains("axe"));

    }

    @Test
    void gotoForest() {
        assertTrue(server.handleCommand("player 1: look").contains("log cabin"));
       assertTrue(server.handleCommand("player 1: goto forest").contains("in A deep dark forest"));


    }

    @Test
    void testHealth() {
        server.handleCommand("player 1: get potion");
        server.handleCommand("player 1: get axe");
        server.handleCommand("player 1: get coin");
        assertTrue(server.handleCommand("player 1: health").contains("3"));
        assertTrue(server.handleCommand("player 1: health").contains("You"));

        server.handleCommand("player 1: goto forest");
        server.handleCommand("player 1: get key");
        server.handleCommand("player 1: goto cabin");
        server.handleCommand("player 1: unlock key");
        server.handleCommand("player 1: goto cellar");

        server.handleCommand("player 1: attack elf");

        assertTrue(server.handleCommand("player 1: health").contains("2"));

        assertTrue(server.handleCommand("player 1: inv").contains("potion"));
        server.handleCommand("player 1: drink potion");
        assertTrue(!server.handleCommand("player 1: inv").contains("potion"));

        assertTrue(server.handleCommand("player 1: health").contains("3"));

        server.handleCommand("player 1: attack elf");
        server.handleCommand("player 1: attack elf");
        assertTrue( server.handleCommand("player 1: inv").contains("axe"));
        assertTrue( server.handleCommand("player 1: inv").contains("coin"));

        assertTrue( !server.handleCommand("player 1: look").contains("axe"));
        assertTrue( !server.handleCommand("player 1: look").contains("coin"));

       server.handleCommand("player 1: attack elf");
        assertTrue( !server.handleCommand("player 1: inv").contains("axe"));
        assertTrue( !server.handleCommand("player 1: inv").contains("coin"));

        assertTrue( !server.handleCommand("player 1: look").contains("axe"));
        assertTrue( !server.handleCommand("player 1: look").contains("coin"));


        server.handleCommand("player 1: goto cellar");
        assertTrue( server.handleCommand("player 1: look").contains("axe"));
        assertTrue( server.handleCommand("player 1: look").contains("coin"));

    }

    @Test
    void testZeroHealth() {
        server.handleCommand("player 1: get potion");
        server.handleCommand("player 1: get axe");
        server.handleCommand("player 1: get coin");
        assertTrue(server.handleCommand("player 1: health").contains("3"));
        assertTrue(server.handleCommand("player 1: health").contains("You"));

        server.handleCommand("player 1: goto forest");
        server.handleCommand("player 1: get key");
        server.handleCommand("player 1: goto cabin");
        server.handleCommand("player 1: unlock key");
        server.handleCommand("player 1: goto cellar");

        server.handleCommand("player 1: attack elf");

        assertTrue(server.handleCommand("player 1: health").contains("2"));

        assertTrue(server.handleCommand("player 1: inv").contains("potion"));
        server.handleCommand("player 1: drink potion");
        assertTrue(!server.handleCommand("player 1: inv").contains("potion"));

        assertTrue(server.handleCommand("player 1: health").contains("3"));

        server.handleCommand("player 1: attack elf");
        server.handleCommand("player 1: attack elf");
        assertTrue( server.handleCommand("player 1: health").contains("1"));
        assertTrue( server.handleCommand("player 1: inv").contains("axe"));
        assertTrue( server.handleCommand("player 1: inv").contains("coin"));

        assertTrue( !server.handleCommand("player 1: look").contains("axe"));
        assertTrue( !server.handleCommand("player 1: look").contains("coin"));

        server.handleCommand("player 1: attack elf");
        assertTrue( !server.handleCommand("player 1: inv").contains("axe"));
        assertTrue( !server.handleCommand("player 1: inv").contains("coin"));

        assertTrue( !server.handleCommand("player 1: look").contains("axe"));
        assertTrue( !server.handleCommand("player 1: look").contains("coin"));


        server.handleCommand("player 1: goto cellar");
        assertTrue( server.handleCommand("player 1: look").contains("axe"));
        assertTrue( server.handleCommand("player 1: look").contains("coin"));

    }

    @Test
    void openBox() {
        assertTrue(server.handleCommand("player 1: open box").contains("opened"));

    }
    @Test
    void notenoughInfo() {
        assertTrue(server.handleCommand("player 1: open ").contains("Not enough info"));

    }

    @Test
    void invalid1() {
        assertTrue(server.handleCommand("player 1: a ").contains("ERROR"));

    }

    @Test
    void blowhorn() {
        server.handleCommand("player 1: goto forest ");
        server.handleCommand("player 1: goto riverbank ");
        server.handleCommand("player 1: get horn ");
        server.handleCommand("player 1: inv ");
        assertTrue(!server.handleCommand("player 1: look ").contains("cutter"));
        server.handleCommand("player 1: blow horn ");
        assertTrue(server.handleCommand("player 1: look ").contains("cutter"));
        server.handleCommand("player 1: attack lumberjack ");
        server.handleCommand("player 1: look ");
    }

    @Test
    void producePotion() {
        server.handleCommand("player 1: get axe ");
        server.handleCommand("player 1: get potion ");
        server.handleCommand("player 1: drink potion ");
        server.handleCommand("player 1: inv");
        server.handleCommand("player 1: goto forest ");
        server.handleCommand("player 1: chop tree ");
        server.handleCommand("player 1: look ");

    }

    @Test
    void accessCellarWithoutKey() {
        assertTrue(server.handleCommand("player 1: goto cellar ").contains("cannot"));

    }

    @Test
    void getGold() {
        assertTrue(server.handleCommand("player 1: get gold ").contains("ERROR"));
    }





}
