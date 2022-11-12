package jmail.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClientTest {
  @Test
  void appHasAGreeting() {
    Main classUnderTest = new Main();
    assertNotNull(classUnderTest.getGreeting(), "Client should have a greeting");
  }
}
