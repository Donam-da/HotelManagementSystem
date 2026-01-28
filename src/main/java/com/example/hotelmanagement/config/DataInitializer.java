package com.example.hotelmanagement.config;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final AmenityRepository amenityRepository;
    private final HotelServiceRepository hotelServiceRepository;

    public DataInitializer(RoomTypeRepository roomTypeRepository, 
                           RoomRepository roomRepository,
                           GuestRepository guestRepository,
                           AmenityRepository amenityRepository,
                           HotelServiceRepository hotelServiceRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.amenityRepository = amenityRepository;
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roomTypeRepository.count() > 0) {
            return; // Đã có dữ liệu, không chèn thêm
        }

        // 1. Seed Amenities
        Amenity wifi = new Amenity(); wifi.setName("High-speed Wi-Fi"); wifi.setIcon("fa-wifi");
        Amenity tv = new Amenity(); tv.setName("Smart TV"); tv.setIcon("fa-tv");
        amenityRepository.saveAll(Arrays.asList(wifi, tv));

        // 2. Seed Room Types
        RoomType standard = new RoomType();
        standard.setName("Standard Room");
        standard.setBasePrice(500000.0);
        standard.setMaxOccupancy(2);
        standard.setAmenities(Arrays.asList(wifi, tv));
        roomTypeRepository.save(standard);

        // 3. Seed Rooms
        Room room101 = new Room();
        room101.setRoomNumber("101");
        room101.setFloor(1);
        room101.setStatus(RoomStatus.AVAILABLE);
        room101.setRoomType(standard);
        roomRepository.save(room101);

        // 4. Seed Guests
        Guest guest = new Guest();
        guest.setFirstName("Nguyễn");
        guest.setLastName("Văn An");
        guest.setEmail("an.nguyen@example.com");
        guest.setPhone("0901234567");
        guestRepository.save(guest);

        // 5. Seed Services
        HotelService laundry = new HotelService();
        laundry.setName("Laundry");
        laundry.setPrice(50000.0);
        laundry.setCategory("LAUNDRY");
        laundry.setIsActive(true);
        hotelServiceRepository.save(laundry);

        System.out.println(">>>>> Dữ liệu mẫu đã được khởi tạo thành công! <<<<<");
    }
}