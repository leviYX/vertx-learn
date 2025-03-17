import com.levi.ws.MainVerticle;
import com.levi.ws.handler.WebSocketHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void can_connect_wen_socket_server(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var httpClient = vertx.createHttpClient();
    httpClient.webSocket(8080, "localhost", WebSocketHandler.WEBSOCKET_PATH)
            .onFailure(testContext::failNow)
            .onComplete(testContext.succeeding(ws -> {
                 ws.frameHandler(buffer -> {
                   String msg = buffer.textData();
                    assert "connected".equals(msg);
                    httpClient.close();
                     testContext.completeNow();
                 });
            }));
  }
}
