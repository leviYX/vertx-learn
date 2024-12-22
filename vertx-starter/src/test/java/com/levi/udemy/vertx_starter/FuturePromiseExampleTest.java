package com.levi.udemy.vertx_starter;


import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ExtendWith(VertxExtension.class)
public class FuturePromiseExampleTest {

  private static final Logger log = LoggerFactory.getLogger(FuturePromiseExampleTest.class);

  @Test
  public void promise_success(Vertx vertx, VertxTestContext testContext) {
    Promise<String> promise = Promise.promise();
    log.info("start");
    vertx.setTimer(1000,id -> {
      promise.complete("success");
      log.info("success");
      testContext.completeNow();
    });
    log.info("end");
  }

  @Test
  public void promise_fail(Vertx vertx, VertxTestContext testContext) {
    Promise<String> promise = Promise.promise();
    log.info("start");
    vertx.setTimer(1000,id -> {
      promise.fail(new RuntimeException("fail"));
      log.info("failure");
      testContext.completeNow();
    });
    log.info("end");
  }

  @Test
  public void future_success(Vertx vertx, VertxTestContext testContext) {
    Promise<String> promise = Promise.promise();
    log.info("start");
    vertx.setTimer(1000,id -> {
      // promise在完成之后会触发future的onSuccess回调
      promise.complete("success");
      log.info("Timer done");
      // 这里不需要上下文的completeNow，因为future的onSuccess回调会触发上下文的完成,
      // 不需要手动触发上下文的完成，因为此时可能future的onSuccess还没有执行，你就完成了测试上下文，
      // 这里我们交给promise.complete处理，不需要手动触发上下文的完成
      // testContext.completeNow();
    });
    // 创建future，并绑定promise
    Future<String> future = promise.future();
    // future的onSuccess和onFailure回调会在promise完成之后被触发
    future
      .onSuccess(result -> {
        log.info(result);
        // 此时回调完成，结束上下文销毁上下文
        testContext.completeNow();
      })
      .onFailure(testContext::failNow);
  }

  @Test
  public void future_fail(Vertx vertx, VertxTestContext testContext) {
    Promise<String> promise = Promise.promise();
    log.info("start");
    vertx.setTimer(1000,id -> {
      // promise在完成之后会触发future的onSuccess回调
      promise.fail(new RuntimeException("Timer execute error"));
      log.info("Timer done");

    });
    // 创建future，并绑定promise
    Future<String> future = promise.future();
    // future的onSuccess和onFailure回调会在promise完成之后被触发
    future
      .onSuccess(result -> {
        log.info(result);
        testContext.completeNow();
      })
      .onFailure(err ->{
        log.error(err.getMessage());
        testContext.completeNow();
      });
  }

  @Test
  public void future_map(Vertx vertx, VertxTestContext testContext) {
    Promise<String> promise = Promise.promise();
    log.info("start");
    vertx.setTimer(1000,id -> {
      promise.complete("success");
      log.info("Timer done");
    });
    Future<String> future = promise.future();
    future
      .map(resultStr ->{
        log.info("map string to jsonObject");
        return new JsonObject().put("key",resultStr);
      })
      .map(jsonObj -> {
        log.info("map jsonObject to jsonArray");
        return new JsonArray().add(jsonObj);
      })
      .onSuccess(result -> {
        log.info("res is {} and res type is {}",result.toString(),result.getClass().getSimpleName());
        // 此时回调完成，结束上下文销毁上下文
        testContext.completeNow();
      })
      .onFailure(testContext::failNow);
  }

  @Test
  public void future_coordination(Vertx vertx, VertxTestContext testContext) {
    log.info("start");

    vertx
      .createHttpServer()
      .requestHandler(request ->{
        log.info("{}",request);
      })
      .listen(8080)
      .compose(server ->{
        log.info("compose 1");
        return Future.succeededFuture(server);
      })
      .compose(server -> {
        log.info("compose 1");
        return Future.succeededFuture(server);
      })
      .onSuccess(result -> {
        log.info("server started on port {}",result.actualPort());
        testContext.completeNow();
      })
      .onFailure(testContext::failNow);

    log.info("end");
  }

//  @Test
//  public void future_composition(Vertx vertx, VertxTestContext context) {
//    log.info("start");
//
//    var promise1 = Promise.promise();
//    var promise2 = Promise.promise();
//    var promise3 = Promise.promise();
//
//    var future1 = promise1.future();
//    var future2 = promise2.future();
//    var future3 = promise3.future();
//
//    CompositeFuture.all(future1,future2,future3)
//      .onSuccess(res -> {
//        log.info("all future success");
//        context.completeNow();
//      })
//      .onFailure(err ->{
//        log.error(err.getMessage());
//        context.failNow(err);
//      });
//
//    vertx.setTimer(1000,timerId ->{
//      promise1.complete("future 1");
//      promise2.complete("future 2");
//      promise3.fail("Timer execute error");
//    });
//
//    log.info("end");
//  }

}
