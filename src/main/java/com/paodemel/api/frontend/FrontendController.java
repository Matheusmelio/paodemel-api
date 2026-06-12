package com.paodemel.api.frontend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrontendController {

  @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
  public String index() throws IOException {
    return Files.readString(Path.of("..", "index.html"));
  }

  @GetMapping(value = "/styles.css", produces = "text/css")
  public String styles() throws IOException {
    return Files.readString(Path.of("..", "styles.css"));
  }

  @GetMapping(value = "/script.js", produces = "application/javascript")
  public String script() throws IOException {
    return Files.readString(Path.of("..", "script.js"));
  }
}
