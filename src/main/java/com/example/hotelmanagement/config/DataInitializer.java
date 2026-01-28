package com.example.hotelmanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Phương thức này hiện tại để trống để quản lý dữ liệu mẫu qua script SQL bên ngoài.
        // Điều này giúp tách biệt logic nghiệp vụ và dữ liệu thử nghiệm.
        System.out.println(">>>>> Hệ thống đã sẵn sàng (Dữ liệu được quản lý thủ công) <<<<<");
    }
}