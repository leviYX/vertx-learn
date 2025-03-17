package com.levi.ws.factory;

import com.github.javafaker.Faker;

public class InstanceFactory {

  private static final Faker FAKER = Faker.instance();

  public static Faker faker() {
    return FAKER;
  }
}
