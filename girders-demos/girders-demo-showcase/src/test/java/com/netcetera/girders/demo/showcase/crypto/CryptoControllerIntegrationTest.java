package com.netcetera.girders.demo.showcase.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(properties = "jasypt.encryptor.password=girders")
class CryptoControllerIntegrationTest {

  @Autowired
  private CryptoController controller;

  @Test
  void shouldProvideIndexResource() {
    Model model = new ExtendedModelMap();

    assertThat(controller.index(model), is("crypto"));
    assertThat(model.asMap().get("textEncrypted"),
        is("This value is shown in clear-text here, but is stored as an encrypted string in the configuration file."));
  }

}