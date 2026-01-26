package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import để ngắt vòng lặp JSON
import java.util.List;

@Entity
@Data
@Table(name = "room_types")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ví dụ: Deluxe Ocean View, Standard Room

    // --- BỔ SUNG MỚI THEO YÊU CẦU 6.1 ---
    private String description; // Mô tả chi tiết (VD: View biển, có bồn tắm...)
    
    // Lưu ý: Dùng Double để đồng bộ với code tính tiền ở BillingService
    // Nếu dùng BigDecimal bạn sẽ phải sửa lại toàn bộ code Service, rất mất công.
    private Double basePrice; 

    private int maxOccupancy; // Số người tối đa

    // --- BỔ SUNG MỚI THEO YÊU CẦU 6.1 ---
    private String bedType; // Ví dụ: King Size, Queen Size, Twin Bed

    // Quan hệ 1-N: Một loại phòng có nhiều phòng vật lý
    @OneToMany(mappedBy = "roomType")
    @JsonIgnore // <-- QUAN TRỌNG: Thêm dòng này để khi lấy RoomType không bị lôi theo cả list Room
    private List<Room> rooms;
    
    // Quan hệ N-N với Tiện nghi (Amenity)
    @ManyToMany
    @JoinTable(
        name = "room_type_amenities",
        joinColumns = @JoinColumn(name = "room_type_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;
}