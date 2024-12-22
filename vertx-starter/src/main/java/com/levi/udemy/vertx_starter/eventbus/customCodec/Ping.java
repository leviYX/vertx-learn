package com.levi.udemy.vertx_starter.eventbus.customCodec;

public class Ping {
  private String message;
  private Integer version;
  private Boolean success;

  public Ping() {}

  public Ping(String message, Integer version, Boolean success) {
    this.message = message;
    this.version = version;
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public Integer getVersion() {
    return version;
  }

  public Boolean getSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "Ping{" +
      "message='" + message + '\'' +
      ", version=" + version +
      ", success=" + success +
      '}';
  }
}
