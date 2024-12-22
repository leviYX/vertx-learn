package com.levi.udemy.vertx_starter.eventbus.customCodec;

public class Pong {
  private Integer id;

  public Pong() {
  }

  public Pong(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  @Override
  public String toString() {
    return "Pong{" +
      "id=" + id +
      '}';
  }
}
