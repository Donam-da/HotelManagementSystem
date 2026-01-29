# Hotel Management System

## Thông tin đồ án
* **Giảng viên hướng dẫn:** TS. Bùi Hoa
* **Sinh viên thực hiện:**
    * Đỗ Hoài Nam - 22014002
    * Nguyễn Văn Minh - 22010049

Hệ thống quản lý khách sạn (HMS) toàn diện được xây dựng trên nền tảng Spring Boot, hỗ trợ quản lý vòng đời đặt phòng, dịch vụ khách hàng, hóa đơn và chương trình khách hàng thân thiết.

## 1. Công nghệ sử dụng
* **Backend:** Java 24, Spring Boot 3.x, Spring Data JPA.
* **Database:** MySQL 8.0+.
* **Documentation:** Swagger UI (OpenAPI 3.0).
* **Build Tool:** Maven.

## 2. Yêu cầu hệ thống
* Java 17+
* MySQL 8.0+
* Maven 3.6+

## 3. Chức năng chính & Quy tắc nghiệp vụ (Business Rules)

### 3.1. Quản lý Đặt phòng (Reservation)
* **Quy trình:** `PENDING` -> `CONFIRMED` -> `CHECKED_IN` -> `CHECKED_OUT`.
* **Ràng buộc:**
    * **BR-101:** Khách hàng phải từ 18 tuổi trở lên.
    * **BR-002:** Thời gian lưu trú tối đa là 30 đêm liên tiếp.
    * **BR-004:** Chỉ cho phép chỉnh sửa thông tin khi đơn ở trạng thái `PENDING` hoặc `CONFIRMED`.
    * **Kiểm tra xung đột:** Hệ thống tự động chặn đặt phòng nếu lịch trình bị trùng lặp (Overlapping).

### 3.2. Chính sách Hủy phòng (Cancellation Policy)
Phí phạt hủy phòng được tính dựa trên thời gian hủy trước giờ Check-in (mặc định 14:00):
* **BR-005:** Hủy dưới 24h: Phạt 100% giá trị đêm đầu tiên.
* **BR-006:** Hủy từ 24h - 72h: Phạt 50% giá trị đêm đầu tiên.
* **BR-007:** Hủy trên 72h: Miễn phí hủy phòng.

### 3.3. Thanh toán & Hóa đơn (Billing)
* **Tự động hóa:** Hóa đơn tạm tính được tạo ngay khi đặt phòng.
* **Cấu trúc phí:**
    * Thuế VAT: 10%.
    * Phí dịch vụ: 5%.
    * Mã hóa đơn định dạng: `INV-YYYYMMDD-XXXXX` (Random 5 ký tự).
* **Loyalty Program (BR-103):** Tích lũy 10 điểm cho mỗi $1 chi tiêu. Điểm có thể dùng để giảm giá trực tiếp trên hóa đơn.

### 3.4. Quản lý Phòng
* Trạng thái phòng: `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`.
* Quản lý theo Loại phòng (Room Type) với các tiện nghi (Amenities) đi kèm.

## 4. Cấu trúc dự án
```text
src/main/java/com/example/hotelmanagement/
├── controller/    # Tiếp nhận request API & Validation
├── service/       # Xử lý logic nghiệp vụ (Core Logic)
├── repository/    # Tương tác DB (Spring Data JPA)
├── entity/        # Định nghĩa các thực thể (Database Schema)
├── dto/           # Data Transfer Objects cho API
└── exception/     # Xử lý lỗi tập trung (Global Exception Handling)
```

## 5. Hướng dẫn cài đặt

### 5.1. Cơ sở dữ liệu
1. Tạo database:
   ```sql
   CREATE DATABASE hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Cấu hình kết nối trong `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```

### 5.2. Chạy ứng dụng
Mở terminal tại thư mục gốc và chạy:
```bash
./mvnw spring-boot:run
```

## 6. Tài liệu API (Endpoints)
Sau khi ứng dụng khởi chạy, truy cập:
* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

### Các API chính:
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/v1/reservations` | Tạo đơn đặt phòng mới |
| PATCH | `/api/v1/reservations/{id}/check-in` | Thực hiện Check-in |
| PATCH | `/api/v1/reservations/{id}/check-out` | Thực hiện Check-out & Tính điểm loyalty |
| PATCH | `/api/v1/reservations/{id}/cancel` | Hủy phòng & Tính phí phạt |
| GET | `/api/v1/reservations` | Lấy danh sách đơn (Hỗ trợ phân trang) |
| GET | `/api/v1/reservations/{id}/invoice` | Xem chi tiết hóa đơn |

## 7. Sơ đồ hệ thống (Thiết kế)

### 7.1. Sơ đồ thực thể (ERD)(Có trong báo cáo Word)
Hệ thống bao gồm các thực thể chính:
* **Guest:** Thông tin khách hàng và điểm tích lũy.
* **Room & RoomType:** Quản lý phòng vật lý và cấu hình loại phòng (giá, tiện nghi).
* **Reservation:** Trung tâm quản lý trạng thái và thời gian lưu trú.
* **Invoice & Payment:** Quản lý tài chính và lịch sử giao dịch.

### 7.2. Quy trình Check-out & Loyalty
1. Khách hàng yêu cầu Check-out.
2. Hệ thống cập nhật trạng thái phòng về `AVAILABLE`.
3. `BillingService` tổng hợp chi phí (phòng + dịch vụ + thuế).
4. Hệ thống tính toán điểm thưởng: `TotalAmount * 10`.
5. Cập nhật số dư điểm vào hồ sơ `Guest`.

---
*Dự án được phát triển cho mục đích quản lý khách sạn chuyên nghiệp.*
