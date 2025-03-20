FROM java:8
LABEL maintainer="YouTH996<youth996@163.com>"
ENV TZ 'Asia/Shanghai'
ENV LANG C.UTF-8
ENV LANGUAGE C.UTF-8
ENV LC_ALL C.UTF-8

# 挂载目录
VOLUME /home/youth996/ipv42v6
# 创建目录
RUN mkdir -p /home/youth996/ipv42v6
# 指定路径
WORKDIR /home/youth996/ipv42v6
# 复制jar文件到路径
COPY target/ipv42v6.jar /home/youth996/ipv42v6/ipv42v6.jar
COPY src/main/resources/application.yml /home/youth996/ipv42v6/application.yml


EXPOSE 10401
ENTRYPOINT ["java","-jar","ipv42v6","--spring.config.location=application.yml"]
