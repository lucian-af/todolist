package br.com.laf.todolist.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties("properties.variables")
@Data
@Component
public class Variables {
  private List<String> urlsAllowAnonymous = Arrays.asList("h2-console", "users");
}
