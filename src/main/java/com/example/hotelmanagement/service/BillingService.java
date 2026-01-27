package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private HotelServiceRepository hotelServiceRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    // UC-009: Generate Invoice (Tính toán tổng tiền)
    public Invoice generateInvoice(Reservation reservation) {
        Invoice invoice = reservation.getInvoice();
        if (invoice == null) {
            invoice = new Invoice();
            invoice.setReservation(reservation);
        }

        // 1. Tính tiền phòng
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (days < 1) days = 1; // Tối thiểu 1 ngày
        
        Double roomPrice = reservation.getRoom().getRoomType().getBasePrice();
        Double roomTotal = days * roomPrice;

        // 2. Tính tiền dịch vụ (Service Requests)
        List<ServiceRequest> services = serviceRequestRepository.findByReservationId(reservation.getId());
        Double serviceTotal = services.stream()
                .mapToDouble(ServiceRequest::getTotalCost)
                .sum();

        // 3. Tổng hợp
        Double subtotal = roomTotal + serviceTotal;
        Double tax = subtotal * 0.1; // Thuế 10%
        Double total = subtotal + tax;

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }

    // UC-008: Request Service (Gọi món/dịch vụ)
    public ServiceRequest addServiceRequest(Long reservationId, Long serviceId, Integer quantity) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng"));
        
        HotelService service = hotelServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        ServiceRequest request = new ServiceRequest();
        request.setReservation(res);
        request.setHotelService(service);
        request.setQuantity(quantity);
        request.setTotalCost(service.getPrice() * quantity);
        request.setRequestDate(LocalDateTime.now());

        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        // Cập nhật lại hóa đơn ngay lập tức
        generateInvoice(res);

        return savedRequest;
    }

    // UC-010: Process Payment (Thanh toán)
    public Payment processPayment(Long invoiceId, Payment paymentDetails) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        Payment payment = new Payment();
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setTransactionId(paymentDetails.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setInvoice(invoice);

        return paymentRepository.save(payment);
    }

    // UC-010: Process Refund (Hoàn tiền)
    public Payment processRefund(Long invoiceId, Double amount, String reason) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        Payment refund = new Payment();
        refund.setAmount(-amount); // Số tiền âm để thể hiện hoàn tiền
        refund.setPaymentMethod("REFUND");
        refund.setTransactionId(reason); // Lưu lý do hoặc mã giao dịch hoàn tiền
        refund.setPaymentDate(LocalDateTime.now());
        refund.setInvoice(invoice);

        return paymentRepository.save(refund);
    }
}