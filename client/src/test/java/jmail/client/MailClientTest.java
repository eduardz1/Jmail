package jmail.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MailClientTest {
  @Test
  void appHasAGreeting() {
    Main classUnderTest = new Main();
    assertNotNull(classUnderTest.getGreeting(), "Client should have a greeting");
  }
}
