package es.udc.asi.notebook_rest.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyUnauthorizedEntryPoint implements AuthenticationEntryPoint {
  private final Logger logger = LoggerFactory.getLogger(MyUnauthorizedEntryPoint.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {

    logger.debug("Rejecting access");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Entry");
  }
}
