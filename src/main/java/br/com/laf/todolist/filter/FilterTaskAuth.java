package br.com.laf.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.laf.todolist.properties.Variables;
import br.com.laf.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;
  @Autowired
  private Variables variables;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var serveletPath = request.getServletPath().split("/")[1];

    if (this.variables.getUrlsAllowAnonymous().contains(serveletPath)) {
      filterChain.doFilter(request, response);
      return;
    }

    var authorization = request.getHeader("authorization");
    var basicAuthDecode = Base64.getDecoder().decode(authorization.substring("Basic".length()).trim());

    var basicAuth = new String(basicAuthDecode);
    var username = basicAuth.split(":")[0];
    var password = basicAuth.split(":")[1];

    var user = this.userRepository.findByUsername(username);

    if (user == null) {
      response.sendError(HttpStatus.UNAUTHORIZED.value());
    } else {
      var passwordCorrect = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
      if (passwordCorrect) {
        request.setAttribute("idUser", user.getId());
        filterChain.doFilter(request, response);
        return;
      }
      response.sendError(HttpStatus.BAD_REQUEST.value(), "Password invalid.");
    }
  }
}
