package com.example.netty;

// Các import cần thiết từ Netty
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8080; // ✅ Cổng mà server sẽ lắng nghe (có thể đổi)

        // ✅ Tạo hai nhóm luồng:
        // boss: chấp nhận kết nối mới
        // worker: xử lý dữ liệu sau khi kết nối đã được thiết lập
        EventLoopGroup boss = new NioEventLoopGroup();    
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // ✅ Bootstrap khởi tạo server
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker)  // Gán boss và worker cho server
                     .channel(NioServerSocketChannel.class)  // ✅ Chọn kiểu kênh (sử dụng NIO - Non-blocking I/O)
                     .childHandler(new ChannelInitializer<SocketChannel>() { // ✅ Xử lý khi có client mới kết nối
                         @Override
                         protected void initChannel(SocketChannel ch) {
                             // ✅ Thêm handler xử lý dữ liệu vào pipeline
                             ch.pipeline().addLast(new ServerHandler()); 
                         }
                     });

            System.out.println("✅ Netty server đang chạy tại cổng " + port);

            // ✅ Gán server vào cổng, chờ kết nối đến (bind)
            ChannelFuture future = bootstrap.bind(port).sync(); 

            // ✅ Giữ server chạy cho đến khi kênh bị đóng (client ngắt)
            future.channel().closeFuture().sync(); 
        } finally {
            // ✅ Đóng gracefully: ngắt kết nối và dọn dẹp tài nguyên
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}