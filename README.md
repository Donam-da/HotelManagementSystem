# Hotel Management System

Hệ thống quản lý khách sạn xây dựng trên nền tảng Spring Boot.

## 1. Yêu cầu hệ thống
* Java 17+
* MySQL 8.0+
* Maven 3.6+

## 2. Cấu hình cơ sở dữ liệu
1. Tạo database trong MySQL:
   ```sql
   CREATE DATABASE hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Cập nhật username/password trong file `src/main/resources/application.properties`.

## 3. Chạy ứng dụng
Sử dụng Maven Wrapper:
```bash
./mvnw spring-boot:run
```

## 4. Tài liệu API
Sau khi chạy ứng dụng, truy cập các đường dẫn sau:
* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI Spec:** `http://localhost:8080/v3/api-docs`

## 5. Các chức năng chính
* **Quản lý khách hàng:** Đăng ký, tích điểm loyalty.
* **Đặt phòng:** Kiểm tra phòng trống, tính phí hủy phòng.
* **Dịch vụ:** Gọi món, giặt ủi tích hợp vào hóa đơn.
* **Bảo trì:** Quản lý trạng thái phòng và chi phí sửa chữa.
* **Báo cáo:** Thống kê doanh thu và tỷ lệ lấp đầy phòng.

## 6. Tài liệu thiết kế (Documentation)

### 6.1. Sơ đồ Use Case (Module Đặt phòng)
Hệ thống cho phép Khách hàng tìm kiếm và đặt phòng, Lễ tân quản lý vòng đời đơn đặt (Check-in/out) và Admin quản lý cấu hình phòng.

### 6.2. Sơ đồ thực thể (ERD)
Các thực thể chính bao gồm `Guest`, `Reservation`, `Room`, `Invoice` và `Payment`. Mối quan hệ được thiết kế để đảm bảo tính toàn vẹn dữ liệu và hỗ trợ truy vấn báo cáo nhanh.

### 6.3. Quy trình nghiệp vụ phức tạp
1. **Đặt phòng:** Tự động kiểm tra xung đột lịch đặt phòng (Overlapping) và tính hóa đơn tạm tính.
2. **Thanh toán:** Hỗ trợ nhiều phương thức, tính thuế 10% và phí dịch vụ 5% trên tiền phòng.
3. **Loyalty Program:** Tích lũy 10 điểm cho mỗi $1 chi tiêu và cho phép đổi điểm lấy giảm giá trực tiếp trên hóa đơn.

## 7. Hướng dẫn cài đặt (Setup Instructions)
1. **Cơ sở dữ liệu:**
   - Cài đặt MySQL 8.0.
   - Chạy lệnh: `CREATE DATABASE hotel_db;`.
2. **Cấu hình:**
   - Mở `src/main/resources/application.properties`.
   - Cập nhật `spring.datasource.username` và `password`.
3. **Chạy ứng dụng:**
   - Mở terminal tại thư mục gốc.
   - Chạy: `./mvnw spring-boot:run`.
   - Truy cập Swagger UI tại: `http://localhost:8080/swagger-ui/index.html`.

## 6. Cấu trúc gói
* `controller`: Tiếp nhận các request API.
* `service`: Xử lý logic nghiệp vụ.
* `repository`: Tương tác với cơ sở dữ liệu.
* `entity`: Định nghĩa các bảng dữ liệu.
* `dto`: Đối tượng chuyển đổi dữ liệu API.
* `exception`: Xử lý lỗi tập trung.