IPv4 to IPv6 Converter (ipv42v6)

ipv42v6 是一个基于 Spring Boot 构建的 IPv4 转 IPv6 服务，可通过 HTTP 接口提供查询，并支持 Docker 部署。

🚀 功能特点

根据局域网内的 IPv4 地址查询对应的 IPv6 地址。

提供简单的 HTTP 接口，支持 RESTful 访问。

适用于 Windows 和 Linux 平台。

支持 Docker 一键部署。

📌 运行方式

1️⃣ 本地运行（无需 Docker）

环境要求

JDK 11+

Maven 3.6+

启动项目

访问接口

启动后，可使用以下 URL 进行查询：

```
http://localhost:8899/ipv42v6?ipv4=192.168.1.30
```

返回示例：

```
2408:820c:8f59:fb0::14f
```

2️⃣ 使用 Docker 部署

安装 Docker（如未安装）

请参考 Docker 官方文档 进行安装。

构建并运行容器

```
docker run --name ipv42v6 -d -p 8899:8899 youth996/ipv42v6
```

访问服务

🔧 配置说明

默认端口：8899

如果需要修改端口号，请编辑 application.yml 或使用环境变量：

或者运行时指定：

📜 开源协议

本项目遵循 MIT License，欢迎贡献代码或提出 Issue！

GitHub 仓库： [youth996/ipv42v6](https://github.com/youth996/ipv42v6 )  