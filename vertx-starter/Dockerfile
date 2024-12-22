# 使用docker 构建fatjar
# docker build -t example/vertx-starter .
# docker run -t -i -p 8888:8888 example/vertx-starter

FROM openjdk:17

# 定义变量
ENV FAT_JAR vertx-starter-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME /usr/app

EXPOSE 8888

COPY target/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh","-c"]
CMD ["exec java -jar $FAT_JAR"]
