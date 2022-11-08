package jmail.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClientTest {
  @Test
  void appHasAGreeting() {
    Client classUnderTest = new Client();
    assertNotNull(classUnderTest.getGreeting(), "Client should have a greeting");
  }
}
