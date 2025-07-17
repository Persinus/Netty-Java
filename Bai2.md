# Bài 2: Thiết lập Dự Án Netty Server với Maven và Kết Nối Unity Client

Bài này hướng dẫn cách tạo **server game bằng Netty** trong Eclipse, sử dụng **Maven** để quản lý thư viện, và **Unity** làm client gửi nhận dữ liệu qua TCP.

---

## ✅ Phần 1: Tạo Maven Project trong Eclipse

### Cách tạo
1. Vào menu: `File > New > Project > Maven Project`
2. Tick "Create a simple project (skip archetype selection)" → Next
3. Điền:
   - `Group Id`: `com.example.netty`
   - `Artifact Id`: `NettyServer`
   - `Version`: giữ nguyên (0.0.1-SNAPSHOT)
   - `Packaging`: `jar`
4. Finish.

### Cấu trúc sau khi tạo
NettyServer/
├── pom.xml
└── src/
└── main/
└── java/
└── com/example/netty/
## ✅ Phần 2: Cấu hình Maven (pom.xml)

Mở file `pom.xml`, thay nội dung bằng:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example.netty</groupId>
  <artifactId>NettyServer</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  <name>NettyServer</name>

  <dependencies>
    <dependency>
      <groupId>io.netty</groupId>
      <artifactId>netty-all</artifactId>
      <version>4.1.86.Final</version>
    </dependency>
  </dependencies>
</project>
```
👉 Sau khi lưu:

Chuột phải vào project → Maven > Update Project

Maven sẽ tự tải thư viện Netty về máy tại thư mục .m2

✅ Phần 3: Tạo Class Java
Tạo 2 class trong package com.example.netty:

📄 Server.java
```
package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8080;

        // Nhóm xử lý kết nối (accept) và nhóm xử lý dữ liệu (read/write)
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // Nhận kết nối
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Xử lý dữ liệu

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) {
                             // Thêm handler xử lý logic game
                             ch.pipeline().addLast(new ServerHandler());
                         }
                     });

            System.out.println("✅ Netty server đang chạy tại cổng " + port);

            // Gắn cổng và chạy
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } finally {
            // Tắt các thread khi kết thúc
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```
📄 ServerHandler.java
```
package com.example.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        String received = new String(bytes);
        System.out.println("📩 Nhận từ client: " + received);

        String response = "Hello from Netty!";
        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(response.getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```
✅ Phần 4: Chạy thử Server
Chuột phải vào Server.java → Run As > Java Application

Nếu thành công: console sẽ in ✅ Netty server đang chạy tại cổng 8080

⚠️ Thông báo WARNING: sun.misc.Unsafe... là bình thường, do Netty dùng tối ưu hiệu năng với Unsafe. Không ảnh hưởng đến quá trình chạy.

✅ Phần 5: Unity Client (C#)
Tạo file NettyClient.cs trong Unity, gán vào 1 GameObject:
```
using System.Net.Sockets;
using System.Text;
using UnityEngine;

public class NettyClient : MonoBehaviour
{
    TcpClient client;
    NetworkStream stream;

    void Start()
    {
        try
        {
            client = new TcpClient("127.0.0.1", 8080);
            stream = client.GetStream();

            byte[] data = Encoding.UTF8.GetBytes("Hello from Unity");
            stream.Write(data, 0, data.Length);
            Debug.Log("📤 Đã gửi tới Netty");

            byte[] buffer = new byte[256];
            int len = stream.Read(buffer, 0, buffer.Length);
            string response = Encoding.UTF8.GetString(buffer, 0, len);
            Debug.Log("📥 Nhận từ server: " + response);
        }
        catch (SocketException e)
        {
            Debug.LogError("❌ Không kết nối được: " + e.Message);
        }
    }

    void OnApplicationQuit()
    {
        stream?.Close();
        client?.Close();
    }
}

```

✅ Kết quả
🖥 Eclipse:
```
📩 Nhận từ client: Hello from Unity
```
🎮 Unity Console:
```
📤 Đã gửi tới Netty
📥 Nhận từ server: Hello from Netty!
```
