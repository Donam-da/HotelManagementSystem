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

## 6. Cấu trúc gói
* `controller`: Tiếp nhận các request API.
* `service`: Xử lý logic nghiệp vụ.
* `repository`: Tương tác với cơ sở dữ liệu.
* `entity`: Định nghĩa các bảng dữ liệu.
* `dto`: Đối tượng chuyển đổi dữ liệu API.
* `exception`: Xử lý lỗi tập trung.