package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class BasicCommandTests {
  String err = "[ERROR]";
  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("playerOne: look").toLowerCase();
    System.out.println(response);

    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  @Test
  void testGetPotion() {
    String response = server.handleCommand("playerOne: get potion");
    assertTrue(response.contains("You picked up a potion"), "should contain potion");
  }

  @Test
  void getTrapdoorFail() {
    String response = server.handleCommand("playerOne: get trapdoor");
    assertTrue(response.startsWith(err));
  }

  @Test
  void getElfFail() {
    String response = server.handleCommand("playerOne: get elf");
    assertTrue(response.startsWith(err));
  }

  @Test
  void getKeyFail() {
    String response = server.handleCommand("playerOne: get key");
    assertTrue(response.startsWith(err));
  }

  @Test
  void getKey() {
    server.handleCommand("playerOne: goto forest");
    String response = server.handleCommand("playerOne: get key");
    assertTrue(response.contains("You picked up a key"));

  }

  @Test
  void getKeyCheckInventory() {
     server.handleCommand("playerOne: goto forest");
  String getKeyResponse =  server.handleCommand("playerOne:get key");

     String response = server.handleCommand("playerOne: inv");
    assertTrue(response.contains("key"));
  }

  @Test
  void look() {
    server.handleCommand("     players 's              One: look    ");

  }

  @Test
  void invalidInput1() {
    assertTrue(server.handleCommand("playerOne: look around").startsWith(err));

    assertTrue(server.handleCommand("playerOne: look").contains("room"));
  }


  @Test
  void invalidInput2() {
    assertTrue(server.handleCommand("playerOne: health check").startsWith(err));
    assertTrue(server.handleCommand("playerOne:              look     ").contains("room"));
  }

  @Test
  void invalidInput3() {
    server.handleCommand("playerOne: goto forest");
    server.handleCommand("playerOne: chop tree");
  }

  @Test
  void invalidGoto() {
    assertTrue(server.handleCommand("playerOne: goto riverbank").contains("cannot"));

  }













  // Add more unit tests or integration tests here.

}
