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
        server.handleCommand("playerOne: look");
         server.handleCommand("playerOne: get axe");
        server.handleCommand("playerOne: get coin");


      server.handleCommand("playerOne: goto forest");
        server.handleCommand("playerOne: get key");
       server.handleCommand("playerOne: chop tree");
     assertTrue(server.handleCommand("playerOne: look").contains("log"));

       String lookForest  = server.handleCommand("playerOne: look");
         assertTrue(lookForest.contains("cabin"));
        assertTrue(lookForest.contains("riverbank"));

        assertTrue(server.handleCommand("playerOne: get log").contains("You picked up a log"));

        server.handleCommand("playerOne: goto cabin");
        server.handleCommand("playerOne: unlock with key");
        String lookCabin  = server.handleCommand("playerOne: look");

        assertTrue(lookCabin.contains("cellar"));
        server.handleCommand("playerOne: goto cellar");

        assertTrue(server.handleCommand("playerOne: inv").contains("coin"));
        server.handleCommand("playerOne: pay elf");
        assertTrue(!server.handleCommand("playerOne: inv").contains("coin"));

//        assertTrue(server.handleCommand("playerOne: look").contains("shovel"));
//        assertTrue(server.handleCommand("playerOne: get shovel").contains("shovel"));
//        String invShovel =  server.handleCommand("playerOne: inv");
//        assertTrue(invShovel.contains("shovel"));
//        assertTrue(!invShovel.contains("coin"));
//
//
//         server.handleCommand("playerOne: goto riverbank");
//         server.handleCommand("playerOne: bridge river with log");
//
//        String inv2 = server.handleCommand("playerOne: inv");
//        assertTrue(!inv2.contains("log"));
//
//        String look2 = server.handleCommand("playerOne: look");
//        assertTrue(look2.contains("clearing"));
//
//        String gotoClearing = server.handleCommand("playerOne: goto clearing");
//        assertTrue(gotoClearing.contains("riverbank"));
//
//        server.handleCommand("playerOne: look");
//        assertTrue(gotoClearing.contains("soil"));
//
//        String digWithShovel = server.handleCommand("playerOne: dig with shovel");
//
//       assertTrue(!server.handleCommand("playerOne: inv").contains("gold"));
//
//      String lookAfterDig = server.handleCommand("playerOne: look");
//
//         assertTrue(!lookAfterDig.contains("soil"));
//        assertTrue(lookAfterDig.contains("gold"));
//
//        assertTrue(server.handleCommand("playerOne: get gold").contains("gold"));
//        assertTrue(server.handleCommand("playerOne: inv").contains("gold"));
//
//
//        String digWithShovelAgainResult = server.handleCommand("playerOne: dig with shovel");
//        assertTrue(digWithShovelAgainResult.contains("You do not have the require item"));
//
//         server.handleCommand("playerOne: drop shovel");
//
//        String lookAfterDrop = server.handleCommand("playerOne: look");
    }
    @Test
    void testMultiplayer() {
       server.handleCommand("playerOne: get axe");

         server.handleCommand("playerTwo: get potion");

         assertTrue( server.handleCommand("playerOne: inv").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: inv").contains("potion"));



        assertTrue( !server.handleCommand("playerTwo: inv").contains("axe"));
        assertTrue( server.handleCommand("playerTwo: inv").contains("potion"));


        server.handleCommand("playerOne: look");



    }

    @Test
    void testMultiplayer2() {
        server.handleCommand("playerOne: get axe");
        server.handleCommand("playerOne: goto forest");
        server.handleCommand("playerTwo: goto forest");
        server.handleCommand("playerTwo: goto riverbank");

        server.handleCommand("playerThree: get potion");

        server.handleCommand("playerTwo: look");

        server.handleCommand("playerThree: look");



    }

    @Test
    void getAxe() {
        assertTrue(server.handleCommand("playerOne: get axe").contains("You picked up"));
        assertTrue(server.handleCommand("playerOne: drop axe").contains("dropped"));
        assertTrue(!server.handleCommand("playerOne: inv").contains("axe"));

    }

    @Test
    void gotoForest() {
        assertTrue(server.handleCommand("playerOne: look").contains("log cabin"));
       assertTrue(server.handleCommand("playerOne: goto forest").contains("in A deep dark forest"));


    }

    @Test
    void testHealth() {
        server.handleCommand("playerOne: get potion");
        server.handleCommand("playerOne: get axe");
        server.handleCommand("playerOne: get coin");
        assertTrue(server.handleCommand("playerOne: health").contains("3"));
        assertTrue(server.handleCommand("playerOne: health").contains("You"));

        server.handleCommand("playerOne: goto forest");
        server.handleCommand("playerOne: get key");
        server.handleCommand("playerOne: goto cabin");
        server.handleCommand("playerOne: unlock key");
        server.handleCommand("playerOne: goto cellar");

        server.handleCommand("playerOne: attack elf");

        assertTrue(server.handleCommand("playerOne: health").contains("2"));

        assertTrue(server.handleCommand("playerOne: inv").contains("potion"));
        server.handleCommand("playerOne: drink potion");
        assertTrue(!server.handleCommand("playerOne: inv").contains("potion"));

        assertTrue(server.handleCommand("playerOne: health").contains("3"));

        server.handleCommand("playerOne: attack elf");
        server.handleCommand("playerOne: attack elf");
        assertTrue( server.handleCommand("playerOne: inv").contains("axe"));
        assertTrue( server.handleCommand("playerOne: inv").contains("coin"));

        assertTrue( !server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: look").contains("coin"));

       server.handleCommand("playerOne: attack elf");
        assertTrue( !server.handleCommand("playerOne: inv").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: inv").contains("coin"));

        assertTrue( !server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: look").contains("coin"));


        server.handleCommand("playerOne: goto cellar");
        assertTrue( server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( server.handleCommand("playerOne: look").contains("coin"));

    }

    @Test
    void testZeroHealth() {
        server.handleCommand("playerOne: get potion");
        server.handleCommand("playerOne: get axe");
        server.handleCommand("playerOne: get coin");
        assertTrue(server.handleCommand("playerOne: health").contains("3"));
        assertTrue(server.handleCommand("playerOne: health").contains("You"));

        server.handleCommand("playerOne: goto forest");
        server.handleCommand("playerOne: get key");
        server.handleCommand("playerOne: goto cabin");
        server.handleCommand("playerOne: unlock key");
        server.handleCommand("playerOne: goto cellar");

        server.handleCommand("playerOne: attack elf");

        assertTrue(server.handleCommand("playerOne: health").contains("2"));

        assertTrue(server.handleCommand("playerOne: inv").contains("potion"));
        server.handleCommand("playerOne: drink potion");
        assertTrue(!server.handleCommand("playerOne: inv").contains("potion"));

        assertTrue(server.handleCommand("playerOne: health").contains("3"));

        server.handleCommand("playerOne: attack elf");
        server.handleCommand("playerOne: attack elf");
        assertTrue( server.handleCommand("playerOne: health").contains("1"));
        assertTrue( server.handleCommand("playerOne: inv").contains("axe"));
        assertTrue( server.handleCommand("playerOne: inv").contains("coin"));

        assertTrue( !server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: look").contains("coin"));

        server.handleCommand("playerOne: attack elf");
        assertTrue( server.handleCommand("playerOne: health").contains("3"));
        assertTrue( !server.handleCommand("playerOne: inv").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: inv").contains("coin"));

        assertTrue( !server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( !server.handleCommand("playerOne: look").contains("coin"));


        server.handleCommand("playerOne: goto cellar");
        assertTrue( server.handleCommand("playerOne: look").contains("axe"));
        assertTrue( server.handleCommand("playerOne: look").contains("coin"));

    }

    @Test
    void openBox() {
        assertTrue(server.handleCommand("playerOne: open box").contains("opened"));

    }
    @Test
    void notenoughInfo() {
        assertTrue(server.handleCommand("playerOne: open ").contains("Not enough info"));

    }

    @Test
    void invalid1() {
        assertTrue(server.handleCommand("playerOne: a ").contains("ERROR"));

    }

    @Test
    void blowhorn() {
        server.handleCommand("playerOne: goto forest ");
        server.handleCommand("playerOne: goto riverbank ");
        server.handleCommand("playerOne: get horn ");
        server.handleCommand("playerOne: inv ");
        assertTrue(!server.handleCommand("playerOne: look ").contains("cutter"));
        server.handleCommand("playerOne: blow horn ");
        assertTrue(server.handleCommand("playerOne: look ").contains("cutter"));
        server.handleCommand("playerOne: attack lumberjack ");
        server.handleCommand("playerOne: look ");
    }

    @Test
    void producePotion() {
        assertTrue(server.handleCommand("playerOne: get axe ").contains("picked up"));
        server.handleCommand("playerOne: get potion ");
        assertTrue(server.handleCommand("playerOne: drink potion ").contains("You drink the potion and your health improves"));
        server.handleCommand("playerOne: inv");
        server.handleCommand("playerOne: goto forest ");
        server.handleCommand("playerOne: chop tree ");
        server.handleCommand("playerOne: look ");

    }

    @Test
    void accessCellarWithoutKey() {
        assertTrue(server.handleCommand("playerOne: goto cellar ").contains("cannot"));

    }

    @Test
    void getGold() {
        assertTrue(server.handleCommand("playerOne: get gold ").contains("ERROR"));
    }

    @Test
    void chopTree1() {

        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: drop axe").contains("dropped a axe"));
        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: goto forest").contains("forest"));
        assertTrue(server.handleCommand("playerOne: cutdown tree with axe").contains("You cut down the tree with the axe"));

    }

    @Test
    void chopTree2() {

        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: drop axe").contains("dropped a axe"));
        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: goto forest").contains("forest"));
        assertTrue(server.handleCommand("playerOne: chop tree with axe").contains("You cut down the tree with the axe"));

    }
    @Test
    void chopTree3() {

        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: drop axe").contains("dropped a axe"));
        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: goto forest").contains("forest"));
        assertTrue(server.handleCommand("playerOne: cut tree with axe").contains("You cut down the tree with the axe"));

    }

    @Test
    void chopTree4() {

        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: drop axe").contains("dropped a axe"));
        assertTrue(server.handleCommand("playerOne: get axe").contains("picked"));
        assertTrue(server.handleCommand("playerOne: goto forest").contains("forest"));
        assertTrue(server.handleCommand("playerOne: cutting tree with axe").contains("ERROR"));

    }



}
