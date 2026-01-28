package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.GuestDTO;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.service.GuestService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/guests")
@Tag(name = "Guest", description = "Quản lý thông tin khách hàng")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping
    @Operation(summary = "Tạo mới khách hàng", description = "Đăng ký một khách hàng mới vào hệ thống.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Khách hàng đã được tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    public ResponseEntity<GuestDTO> registerGuest(@Valid @RequestBody Guest guest) {
        Guest savedGuest = guestService.registerGuest(guest);
        GuestDTO dto = guestService.convertToDTO(savedGuest);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách khách hàng", description = "Trả về danh sách khách hàng có hỗ trợ phân trang.")
    public ResponseEntity<Page<GuestDTO>> getAllGuests(Pageable pageable) {
        Page<Guest> guests = guestService.getAllGuests(pageable);
        return ResponseEntity.ok(guests.map(guestService::convertToDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin khách hàng theo ID")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id) {
        Guest guest = guestService.getGuestById(id);
        return ResponseEntity.ok(guestService.convertToDTO(guest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách hàng")
    public ResponseEntity<GuestDTO> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        Guest updatedGuest = guestService.updateGuestProfile(id, guestDetails);
        return ResponseEntity.ok(guestService.convertToDTO(updatedGuest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khách hàng (Soft Delete)", description = "Đánh dấu khách hàng là đã xóa thay vì xóa vĩnh viễn khỏi DB.")
    @ApiResponse(responseCode = "204", description = "Xóa thành công")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuestDTO>> searchGuests(@RequestParam String keyword) {
        List<Guest> guests = guestService.searchGuests(keyword);
        return ResponseEntity.ok(guests.stream().map(guestService::convertToDTO).collect(Collectors.toList()));
    }
}