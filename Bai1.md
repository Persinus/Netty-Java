# Bài 1: Giới Thiệu Giao Thức TCP và WebSocket Trong Server Game

Khi phát triển game online (đặc biệt là game casual và RPG), việc lựa chọn **giao thức giao tiếp giữa client và server** rất quan trọng. Trong bài này, ta sẽ tìm hiểu 2 giao thức phổ biến:

- ✅ **TCP (Transmission Control Protocol)**
- ✅ **WebSocket**

---

## 1. Giao thức TCP

### ✅ Tổng quan
TCP là một giao thức kết nối có trạng thái (*connection-oriented*), đảm bảo:
- Dữ liệu **không bị mất**
- **Đến đúng thứ tự**
- **Không bị trùng lặp**

### 🔧 Cách hoạt động
1. Server mở socket và lắng nghe trên một `port`
2. Client kết nối đến IP + port
3. Dữ liệu được gửi qua luồng byte (`byte stream`)
4. Cần **định nghĩa định dạng gói tin** để phân biệt từng gói

### 📦 Ví dụ định dạng gói tin
[Length][MessageID][Payload]
- `Length` (4 byte): tổng chiều dài gói
- `MessageID` (2 byte): loại message (login, move...)
- `Payload`: dữ liệu chính (có thể là JSON hoặc binary)

### 🧠 Lý do chọn TCP
- Dữ liệu **ổn định, tin cậy**
- Phù hợp cho game casual, RPG, turn-based
- Netty hỗ trợ mạnh mẽ: `ByteBuf`, `ChannelHandler`, `Decoder`

---

## 2. Giao thức WebSocket

### ✅ Tổng quan
WebSocket là giao thức hai chiều (**full-duplex**) chạy trên TCP, rất phù hợp cho ứng dụng **web real-time**.

### 🔧 Cách hoạt động
1. Bắt đầu bằng HTTP request (handshake)
2. Nếu thành công, kết nối được nâng cấp lên WebSocket
3. Sau đó client-server có thể gửi/nhận dữ liệu liên tục

### 📦 Ưu điểm
- Tích hợp dễ dàng trong trình duyệt
- Không bị chặn bởi firewall (chạy qua port 80/443)
- Không cần xử lý phân gói thủ công

### 🧠 Lý do chọn WebSocket
- Game web-based (HTML5, WebGL)
- Muốn phát triển nhanh, ít setup networking
- Không cần quá tối ưu performance

---

## So sánh TCP và WebSocket

| Tiêu chí              | TCP                         | WebSocket                    |
|------------------------|------------------------------|------------------------------|
| Giao tiếp hai chiều    | ✅ Có                        | ✅ Có                         |
| Tin cậy, thứ tự        | ✅ Có                        | ✅ Có                         |
| Cần phân gói thủ công  | ✅ Có                        | ❌ Không                      |
| Hỗ trợ trình duyệt     | ❌ Không                     | ✅ Có                         |
| Dễ tích hợp frontend   | 🔸 Trung bình                | ✅ Dễ                         |
| Tùy biến protocol      | ✅ Rất cao                   | 🔸 Giới hạn (theo frame)      |
| Phù hợp cho game       | RPG, turn-based, native app | Casual web, web-based RPG    |

---

## ✅ Kết luận

- **TCP** là lựa chọn tốt cho game native, server chuyên dụng (Netty).
- **WebSocket** phù hợp với game web, dễ tích hợp, ít cấu hình.
- Cả hai đều chạy trên TCP, nhưng cách triển khai và tính phù hợp khác nhau.

---

## 🛠 Gợi ý cho người dùng Netty

- Dùng `LengthFieldBasedFrameDecoder` để tách gói TCP
- Dùng `ByteBuf` để đọc/ghi dữ liệu
- Dùng `ChannelInboundHandlerAdapter` để xử lý logic
- Nếu muốn thêm WebSocket, dùng `WebSocketServerProtocolHandler` trong Netty

---
