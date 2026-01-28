package com.example.hotelmanagement.config;

import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;

    public DataInitializer(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            System.out.println("[INFO] Database hiện đang trống.");
            System.out.println("[HINT] Vui lòng thực thi tệp 'init_data.sql' để nạp 20+ bản ghi mẫu cho mỗi thực thể.");
        } else {
            System.out.println("[INFO] Hệ thống đã sẵn sàng với " + roomRepository.count() + " phòng trong cơ sở dữ liệu.");
        }
        System.out.println("------------------------------------------------------------");
        System.out.println(">>>>> HOTEL MANAGEMENT SYSTEM IS READY ON PORT 8080 <<<<<");
        System.out.println("------------------------------------------------------------");
    }
}