package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.dto.InvoiceDTO;
import com.example.hotelmanagement.dto.PaymentDTO;
import com.example.hotelmanagement.exception.BusinessException;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    private static final Double SERVICE_FEE_RATE = 0.05;
    private static final Double TAX_RATE = 0.1;

    private final InvoiceRepository invoiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final HotelServiceRepository hotelServiceRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final GuestRepository guestRepository;

    public BillingService(InvoiceRepository invoiceRepository,
                          ServiceRequestRepository serviceRequestRepository,
                          HotelServiceRepository hotelServiceRepository,
                          ReservationRepository reservationRepository,
                          PaymentRepository paymentRepository,
                          GuestRepository guestRepository) {
        this.invoiceRepository = invoiceRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.hotelServiceRepository = hotelServiceRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.guestRepository = guestRepository;
    }

    // UC-009: Generate Invoice (Tính toán tổng tiền)
    public Invoice generateInvoice(Reservation reservation) {
        if (reservation == null || reservation.getRoom() == null) {
            throw new BusinessException("Thông tin đặt phòng không hợp lệ để tạo hóa đơn!");
        }

        Invoice invoice = reservation.getInvoice();
        if (invoice == null) {
            invoice = new Invoice();
            invoice.setReservation(reservation);
        }

        // 1. Tính tiền phòng
        Double roomTotal = 0.0;
        Double basePrice = reservation.getRoom().getRoomType().getBasePrice();

        // LOGIC MỚI: Nếu Hủy hoặc No-Show -> Phạt 1 đêm tiền phòng
        if ("NO_SHOW".equals(reservation.getStatus())) {
            roomTotal = basePrice; 
        } else if ("CANCELLED".equals(reservation.getStatus())) {
            roomTotal = reservation.getCancellationFee(); // Sử dụng phí phạt đã tính ở ReservationService
        } else {
            // Tính bình thường theo số đêm thực tế
            long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
            if (days < 1) days = 1;
            roomTotal = days * basePrice;
        }

        // 2. Tính tiền dịch vụ (Service Requests)
        List<ServiceRequest> services = serviceRequestRepository.findByReservationId(reservation.getId());
        Double serviceTotal = services.stream()
                .mapToDouble(ServiceRequest::getTotalCost)
                .sum();

        // 3. Tổng hợp
        Double subtotal = roomTotal + serviceTotal;
        
        // BR-302: Service charge 5% applied to ROOM CHARGES ONLY
        Double serviceFee = roomTotal * SERVICE_FEE_RATE; 
        Double tax = (subtotal + serviceFee) * TAX_RATE; // Thuế 10% (Tính trên cả phí dịch vụ)
        
        // BR-103: Trừ tiền giảm giá (nếu có)
        Double discount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : 0.0;
        Double total = subtotal + serviceFee + tax - discount;
        if (total < 0) total = 0.0;

        invoice.setSubtotal(subtotal);
        invoice.setServiceFee(serviceFee);
        invoice.setTaxAmount(tax);
        invoice.setDiscountAmount(discount);
        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }

    // UC-008: Request Service (Gọi món/dịch vụ)
    @Transactional
    public ServiceRequest addServiceRequest(Long reservationId, Long serviceId, Integer quantity) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt phòng với ID: " + reservationId));
        
        HotelService service = hotelServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ không tồn tại với ID: " + serviceId));

        ServiceRequest request = new ServiceRequest();
        request.setReservation(res);
        request.setHotelService(service);
        request.setQuantity(quantity);
        request.setTotalCost(service.getPrice() * quantity);
        request.setRequestDate(LocalDateTime.now());
        // request.setStatus("PENDING"); // Đã được set mặc định trong Entity, không cần gọi ở đây nữa

        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        // Cập nhật lại hóa đơn ngay lập tức
        generateInvoice(res);

        return savedRequest;
    }

    // UC-010: Process Payment (Thanh toán)
    @Transactional
    public Payment processPayment(Long invoiceId, Payment paymentDetails) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + invoiceId));

        Payment payment = new Payment();
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setTransactionId(paymentDetails.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setInvoice(invoice);

        return paymentRepository.save(payment);
    }

    // UC-010: Process Refund (Hoàn tiền)
    @Transactional
    public Payment processRefund(Long invoiceId, Double amount, String reason) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + invoiceId));

        Payment refund = new Payment();
        refund.setAmount(-amount); // Số tiền âm để thể hiện hoàn tiền
        refund.setPaymentMethod("REFUND");
        refund.setTransactionId(reason); // Lưu lý do hoặc mã giao dịch hoàn tiền
        refund.setPaymentDate(LocalDateTime.now());
        refund.setInvoice(invoice);

        return paymentRepository.save(refund);
    }

    // BR-103: Đổi điểm thưởng lấy giảm giá (100 điểm = $1)
    @Transactional
    public Invoice redeemLoyaltyPoints(Long invoiceId, Integer pointsToRedeem) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + invoiceId));
        
        Reservation res = invoice.getReservation();
        Guest guest = res.getGuest();
        
        if (guest.getLoyaltyPoints() < pointsToRedeem) {
            throw new BusinessException("Điểm tích lũy không đủ!");
        }
        
        // Quy đổi: 100 điểm = 1 đơn vị tiền tệ
        Double discountValue = pointsToRedeem / 100.0;
        
        // Cập nhật điểm và hóa đơn
        guest.setLoyaltyPoints(guest.getLoyaltyPoints() - pointsToRedeem);
        guestRepository.save(guest);

        invoice.setDiscountAmount((invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : 0.0) + discountValue);
        return generateInvoice(res); // Tính toán lại tổng tiền
    }

    // --- MAPPING METHODS ---
    public InvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setServiceFee(invoice.getServiceFee());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscountAmount(invoice.getDiscountAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        if (invoice.getReservation() != null) {
            dto.setReservationId(invoice.getReservation().getId());
        }
        return dto;
    }

    public PaymentDTO convertToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setTransactionId(payment.getTransactionId());
        if (payment.getInvoice() != null) {
            dto.setInvoiceId(payment.getInvoice().getId());
        }
        return dto;
    }
}