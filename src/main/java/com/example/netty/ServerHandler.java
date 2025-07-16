package com.example.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

// ✅ Lớp xử lý gói tin đến từ client. Kế thừa từ SimpleChannelInboundHandler với kiểu dữ liệu là ByteBuf (buffer dạng byte)
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // ✅ Đọc toàn bộ dữ liệu từ buffer vào mảng byte
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);

        // ✅ Chuyển dữ liệu nhận được sang dạng chuỗi
        String received = new String(bytes);
        System.out.println("📩 Nhận từ client: " + received);

        // ✅ Tạo chuỗi phản hồi
        String response = "Hello from Netty!";

        // ✅ Gửi lại chuỗi phản hồi cho client dưới dạng ByteBuf
        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(response.getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ✅ In stack trace khi có lỗi xảy ra và đóng kết nối
        cause.printStackTrace();
        ctx.close();
    }
}
