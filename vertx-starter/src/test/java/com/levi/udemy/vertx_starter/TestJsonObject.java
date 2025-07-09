package com.levi.udemy.vertx_starter;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

public class TestJsonObject {

  @Test
  public void testJsonObject() {
    var person = new Person(1, "Levi",false);
    var obj = JsonObject.mapFrom(person);
    System.out.println(obj.getBoolean("lovesVertx"));
  }

  @Test
  public void canMapJavaObject() {
    var person = new Person(1, "Levi",false);
    var obj = JsonObject.mapFrom(person);
    System.out.println(obj.getBoolean("lovesVertx"));
    // 反序列化需要默认构造
    var person2 = obj.mapTo(Person.class);
    System.out.println(person2.getName());
  }
}
