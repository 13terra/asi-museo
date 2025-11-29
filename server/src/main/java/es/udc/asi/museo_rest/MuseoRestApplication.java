package es.udc.asi.museo_rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

import es.udc.asi.museo_rest.config.DatabaseLoader;
import es.udc.asi.museo_rest.exception.UserLoginExistsException;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class MuseoRestApplication {
  private final Logger logger = LoggerFactory.getLogger(MuseoRestApplication.class);

  @Autowired
  @Lazy
  private DatabaseLoader databaseLoader;

  public static void main(String[] args) {
    SpringApplication.run(MuseoRestApplication.class, args);
  }

  @PostConstruct
  public void init() throws InterruptedException {
    try {
      databaseLoader.loadData();
    } catch (UserLoginExistsException e) {
      logger.error(e.getMessage(), e);
    }
  }
}
