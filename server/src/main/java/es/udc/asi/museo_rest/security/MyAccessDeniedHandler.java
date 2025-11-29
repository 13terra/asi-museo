package es.udc.asi.notebook_rest.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
  private final Logger logger = LoggerFactory.getLogger(MyAccessDeniedHandler.class);

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    logger.debug("Access denied");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
  }
}
