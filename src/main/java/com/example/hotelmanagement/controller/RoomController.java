package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.dto.RoomTypeDTO;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room", description = "Quản lý phòng và tìm kiếm phòng trống")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 1. Tạo loại phòng (Admin dùng)
    // POST /api/v1/rooms/types
    @PostMapping("/types")
    public RoomTypeDTO createType(@RequestBody RoomType type) {
        return roomService.convertToRoomTypeDTO(roomService.createRoomType(type));
    }

    // 1.1 Lấy danh sách loại phòng
    @GetMapping("/types")
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomService.getAllRoomTypes().stream()
                .map(roomService::convertToRoomTypeDTO)
                .collect(Collectors.toList());
    }

    // 2. Tạo phòng mới (Admin dùng)
    // POST /api/v1/rooms?typeId=1
    @PostMapping
    public RoomDTO createRoom(@Valid @RequestBody Room room, @RequestParam Long typeId) {
        Room savedRoom = roomService.createRoom(room, typeId);
        return roomService.convertToDTO(savedRoom);
    }

    // 3. Lấy danh sách phòng CÓ PHÂN TRANG (Chuẩn NFR 5.2)
    // GET /api/v1/rooms?page=0&size=5
    @GetMapping
    public Page<RoomDTO> getAllRooms(
            @RequestParam(defaultValue = "0") int page, // Trang số mấy (bắt đầu từ 0)
            @RequestParam(defaultValue = "10") int size // Lấy bao nhiêu dòng
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return roomService.getAllRooms(pageable).map(roomService::convertToDTO);
    }

    // 4. Tìm phòng trống (UC-002)
    // GET /api/v1/rooms/available?checkIn=2026-05-01&checkOut=2026-05-05
    @GetMapping("/available")
    @Operation(summary = "Tìm kiếm phòng trống", description = "Tìm danh sách các phòng còn trống trong khoảng thời gian được chọn.")
    public List<RoomDTO> findAvailableRooms(
            @Parameter(description = "Ngày nhận phòng (yyyy-MM-dd)") @RequestParam LocalDate checkIn,
            @Parameter(description = "Ngày trả phòng (yyyy-MM-dd)") @RequestParam LocalDate checkOut) {
        return roomService.getAvailableRooms(checkIn, checkOut).stream().map(roomService::convertToDTO).collect(Collectors.toList());
    }

    // API phụ: Gán tiện nghi vào loại phòng (Ví dụ: Thêm Wifi vào phòng Deluxe)
    // POST /api/v1/rooms/types/{typeId}/amenities/{amenityId}
    @PostMapping("/types/{typeId}/amenities/{amenityId}")
    public RoomTypeDTO addAmenityToType(@PathVariable Long typeId, @PathVariable Long amenityId) {
        return roomService.convertToRoomTypeDTO(roomService.addAmenityToType(typeId, amenityId));
    }

    // 5. Lấy dữ liệu cho Sơ đồ phòng (Visual Map)
    // GET /api/v1/rooms/map
    @GetMapping("/map")
    public List<Map<String, Object>> getRoomMap() {
        return roomService.getRoomMapData();
    }
}