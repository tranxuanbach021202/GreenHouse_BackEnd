# 🌱 GreenHouse Backend

**Greenhouse Backend** là thành phần backend của hệ thống quản lý và theo dõi thí nghiệm trong nhà màng (greenhouse) — phục vụ nghiên cứu nông nghiệp theo các bố trí thí nghiệm chuẩn như **RCBD** và **CRD**.  
Hệ thống hỗ trợ các nhà nghiên cứu thiết kế, theo dõi và thu thập dữ liệu các thí nghiệm trồng trọt một cách trực quan, dễ dùng và hiệu quả.

---

## 🌟 Ứng dụng của hệ thống

- Quản lý các **dự án thí nghiệm nông nghiệp** với đầy đủ thông tin: người tham gia, yếu tố thí nghiệm, mức độ, tiêu chí đánh giá
- Hỗ trợ các kiểu bố trí **RCBD** (Randomized Complete Block Design) và **CRD** (Completely Randomized Design)
- Tự động sinh **bố trí thí nghiệm** và **mã QR** cho từng ô đo
- Cho phép thành viên nhập dữ liệu đo đạc trực tiếp theo từng **đợt nhập liệu**
- Hệ thống phân quyền người dùng theo vai trò: **Owner**, **Member**, **Guest**

---

## 🚀 Các chức năng chính

- ✅ Đăng ký, đăng nhập, quên mật khẩu và xác thực bằng JWT
- ✅ Tạo và quản lý dự án thí nghiệm
- ✅ Quản lý thành viên dự án với phân quyền rõ ràng
- ✅ Tạo và cập nhật yếu tố (factor) và các mức độ (level)
- ✅ Tạo và cập nhật tiêu chí đánh giá (criteria)
- ✅ Tự động sinh treatment dựa trên factor + level
- ✅ Sinh bố trí thí nghiệm RCBD/CRD tự động
- ✅ Sinh và lưu mã QR code cho từng ô thí nghiệm
- ✅ Nhập và quản lý dữ liệu đo lường theo đợt
- ✅ Xuất file Excel tổng hợp kết quả
- ✅ Lưu lại lịch sử thao tác người dùng

---

## 🧩 Thách thức khi phát triển

- Hiểu rõ các mô hình bố trí thí nghiệm trong nông nghiệp như **RCBD** và **CRD**
- Tự học và phân tích các kiến thức chuyên ngành nông nghiệp để đảm bảo logic sinh layout đúng chuẩn khoa học
- Thiết kế kiến trúc hệ thống backend đủ linh hoạt để mở rộng cho nhiều loại mô hình thí nghiệm trong tương lai

---

## 🎯 Tính năng mong muốn triển khai trong tương lai

- ✨ Hỗ trợ đa yếu tố (multi-factor)
- 📊 Phân tích thống kê cơ bản (ANOVA, trung bình tổ hợp,...)
- 📥 Hỗ trợ import dữ liệu từ file Excel
- 📈 Tích hợp biểu đồ trực quan hóa dữ liệu
- 🌍 Đa ngôn ngữ (Vietnamese – English)

---

## 📦 Công nghệ sử dụng

- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- MongoDB (Spring Data Mongo)
- Lombok
- Maven
- Cloudflare R2 (lưu trữ)
- ZXing (QR code)
- SendGrid API (gửi email)

---

## ⚙️ Cài đặt dự án

### ✅ Yêu cầu hệ thống

| Thành phần     | Ghi chú                        |
|----------------|-------------------------------|
| Java           | 17 trở lên                    |
| Maven          | Đã cài đặt|
| MongoDB        | Chạy tại `localhost:27017`    |
| Tài khoản      | Cloudflare R2, Gmail, SendGrid |

---

### 🛠 Cài đặt

Sau khi đã clone dự án về:

- Cài đặt MongoDB
- Setup các biến môi trường trong application.properties
- Tạo file .env
- Cài đặt Maven
- Build dự án (mvn spring-boot:run)


## File cấu hình `.env` mẫu

Dưới đây là file `.env` mẫu dùng để cấu hình các biến môi trường cho backend:

```env
# 🔧 Server Config
PORT=8088

# 🔐 JWT Secrets
JWT_SECRET=your_jwt_secret_here
JWT_ACCESS_SECRET=your_jwt_access_secret_here
JWT_REFRESH_SECRET=your_jwt_refresh_secret_here
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPRIRATION_MS=86400000

# 📬 SendGrid API
API_KEY_SENDGRID=your_sendgrid_api_key_here

# ☁️ Cloudflare R2 Storage
R2_ACCESS_KEY_ID=your_r2_access_key
R2_SECRET_ACCESS_KEY=your_r2_secret_key
R2_ENDPOINT=your_r2_endpoint
R2_BUCKET_NAME=greenhouse
R2_DEV_SUBDOMAIN=https://your-public-r2-subdomain.r2.dev

# 📧 SMTP Gmail
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password_here


