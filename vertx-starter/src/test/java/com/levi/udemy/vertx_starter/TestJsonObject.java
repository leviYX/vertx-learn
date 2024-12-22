package com.levi.udemy.vertx_starter;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

public class TestJsonObject {

  @Test
  public void testJsonObject() {
    var person = new Person(1, "Levi",true);
    var obj = JsonObject.mapFrom(person);
    System.out.println(obj.getBoolean("lovesVertx"));
  }
}
