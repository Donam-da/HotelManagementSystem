package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.ServiceRequest;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    // --- HÀM TÍNH HÓA ĐƠN (Cần cái này để hết lỗi generateInvoice) ---
    public Invoice generateInvoice(Reservation reservation) {
        // 1. Tính số đêm ở (Ít nhất 1 đêm)
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (nights < 1) nights = 1;

        // 2. Tiền phòng = Giá gốc * Số đêm
        Double roomPrice = reservation.getRoom().getRoomType().getBasePrice();
        Double roomCharge = roomPrice * nights;

        // 3. Tiền dịch vụ (Ăn uống, giặt ủi...)
        List<ServiceRequest> requests = serviceRequestRepository.findByReservationId(reservation.getId());
        // Nếu danh sách rỗng thì tiền dịch vụ = 0
        Double serviceCharge = 0.0;
        if (requests != null) {
            serviceCharge = requests.stream().mapToDouble(ServiceRequest::getTotalCost).sum();
        }

        // 4. Tính tổng và Thuế (10%)
        Double subTotal = roomCharge + serviceCharge;
        Double tax = subTotal * 0.10; // 10% VAT
        Double total = subTotal + tax;

        // 5. Lưu hoặc Cập nhật Hóa đơn
        Invoice invoice = reservation.getInvoice();
        if (invoice == null) {
            invoice = new Invoice();
            invoice.setReservation(reservation);
        }

        // Gán các giá trị vào Invoice (Phải khớp với Entity Invoice bạn đã sửa)
        invoice.setSubtotal(subTotal);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }
}