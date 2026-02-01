package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Amenity;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.entity.RoomStatus;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.dto.RoomTypeDTO;
import com.example.hotelmanagement.dto.AmenityDTO;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.AmenityRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final AmenityRepository amenityRepository;

    public RoomService(RoomRepository roomRepository, 
                       RoomTypeRepository roomTypeRepository, 
                       AmenityRepository amenityRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
    }

    // UC-002: Search Available Rooms (Nâng cao)
    public List<Room> searchRooms(LocalDate checkIn, LocalDate checkOut, Long typeId, Integer capacity) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Ngày check-in và check-out là bắt buộc");
        }
        return roomRepository.searchAvailableRooms(checkIn, checkOut, typeId, capacity);
    }

    // UC-011: Manage Room Status (Cập nhật trạng thái thủ công)
    public Room updateRoomStatus(Long roomId, String status) {
        Room room = roomRepository.findById(Objects.requireNonNull(roomId))
                .orElseThrow(() -> new ResourceNotFoundException("Phòng không tồn tại với ID: " + roomId));
        
        // Các trạng thái hợp lệ: AVAILABLE, OCCUPIED, MAINTENANCE, DIRTY
        room.setStatus(RoomStatus.valueOf(status.toUpperCase()));
        return roomRepository.save(room);
    }
    
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Hỗ trợ phân trang cho Controller
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(Objects.requireNonNull(pageable));
    }

    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(Objects.requireNonNull(roomType));
    }

    public Room createRoom(Room room, Long roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(Objects.requireNonNull(roomTypeId))
                .orElseThrow(() -> new ResourceNotFoundException("Loại phòng không tồn tại với ID: " + roomTypeId));
        room.setRoomType(roomType);
        return roomRepository.save(room);
    }

    // Tìm phòng trống cơ bản (chỉ theo ngày)
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }

    // --- QUẢN LÝ TIỆN NGHI (AMENITY) ---
    public List<Amenity> getAllAmenities() { return amenityRepository.findAll(); }
    public Amenity createAmenity(Amenity amenity) { return amenityRepository.save(Objects.requireNonNull(amenity)); }
    public void deleteAmenity(Long id) { amenityRepository.deleteById(Objects.requireNonNull(id)); }
    
    public Amenity updateAmenity(Long id, Amenity details) {
        Amenity amenity = amenityRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Tiện nghi không tồn tại với ID: " + id));
        amenity.setName(details.getName());
        amenity.setDescription(details.getDescription());
        amenity.setIcon(details.getIcon());
        return amenityRepository.save(amenity);
    }

    public RoomType addAmenityToType(Long typeId, Long amenityId) {
        RoomType type = roomTypeRepository.findById(Objects.requireNonNull(typeId))
                .orElseThrow(() -> new ResourceNotFoundException("Loại phòng không tồn tại với ID: " + typeId));
        Amenity amenity = amenityRepository.findById(Objects.requireNonNull(amenityId))
                .orElseThrow(() -> new ResourceNotFoundException("Tiện nghi không tồn tại với ID: " + amenityId));

        if (!type.getAmenities().contains(amenity)) {
            type.getAmenities().add(amenity);
            return roomTypeRepository.save(type);
        }
        return type;
    }

    // --- MAPPING ENTITY TO DTO ---
    public RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setStatus(room.getStatus() != null ? room.getStatus().name() : null);
        if (room.getRoomType() != null) {
            dto.setRoomTypeName(room.getRoomType().getName());
            dto.setPrice(room.getRoomType().getBasePrice());
            dto.setCapacity(room.getRoomType().getMaxOccupancy());
        }
        return dto;
    }

    public RoomTypeDTO convertToRoomTypeDTO(RoomType type) {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(type.getId());
        dto.setName(type.getName());
        dto.setDescription(type.getDescription());
        dto.setBasePrice(type.getBasePrice());
        dto.setMaxOccupancy(type.getMaxOccupancy());
        dto.setBedType(type.getBedType());
        if (type.getAmenities() != null) {
            dto.setAmenities(type.getAmenities().stream().map(this::convertToAmenityDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    public AmenityDTO convertToAmenityDTO(Amenity amenity) {
        AmenityDTO dto = new AmenityDTO();
        dto.setId(amenity.getId());
        dto.setName(amenity.getName());
        dto.setDescription(amenity.getDescription());
        dto.setIcon(amenity.getIcon());
        return dto;
    }
}