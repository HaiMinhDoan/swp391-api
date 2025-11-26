package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.VnPayPaymentRequest;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.VnPayPaymentResponse;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.service.VnPayService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.TranService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for VNPAY payment processing
 */
@RestController
@RequestMapping("/api/v1/payment/vnpay")
@PreAuthorize("permitAll()")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private TranService tranService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserCourseService userCourseService;

    /**
     * Create payment URL and insert transaction
     */
    @PostMapping("/create")
    public ResponseEntity<ResponseData<VnPayPaymentResponse>> createPayment(
            @Valid @RequestBody VnPayPaymentRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Validate user exists
            Optional<User> userOpt = userService.getOne(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<VnPayPaymentResponse>builder()
                                .status(400)
                                .message("User not found")
                                .error("User with id " + request.getUserId() + " not found")
                                .data(null)
                                .build());
            }

            // Validate course exists
            Optional<Course> courseOpt = courseService.getOne(request.getCourseId());
            if (courseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<VnPayPaymentResponse>builder()
                                .status(400)
                                .message("Course not found")
                                .error("Course with id " + request.getCourseId() + " not found")
                                .data(null)
                                .build());
            }

            // Create transaction record
            Tran transaction = new Tran();
            transaction.setUser(userOpt.get());
            transaction.setAmount(request.getAmount());
            transaction.setMethod("VNPAY");
            transaction.setStatus(0); // 0: Pending, 1: Success, 2: Failed
            transaction.setIsDeleted(0);
            transaction.setCreatedAt(Instant.now());
            transaction.setUpdatedAt(Instant.now());
            
            // Store additional info in detail JSON
            Map<String, Object> detail = new HashMap<>();
            detail.put("orderInfo", request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan don hang");
            detail.put("orderType", request.getOrderType() != null ? request.getOrderType() : "other");
            detail.put("bankCode", request.getBankCode());
            detail.put("language", request.getLanguage() != null ? request.getLanguage() : "vn");
            detail.put("courseId", request.getCourseId());
            transaction.setDetail(detail);

            // Save transaction to get ID
            Tran savedTransaction = tranService.create(transaction);

            // Get IP address
            String ipAddress = vnPayService.getIpAddress(httpRequest);

            // Create payment URL
            String paymentUrl = vnPayService.createPaymentUrl(
                    request.getAmount(),
                    request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan don hang",
                    request.getOrderType() != null ? request.getOrderType() : "other",
                    request.getBankCode(),
                    request.getLanguage() != null ? request.getLanguage() : "vn",
                    ipAddress,
                    savedTransaction.getId()
            );

            if (paymentUrl == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ResponseData.<VnPayPaymentResponse>builder()
                                .status(500)
                                .message("Failed to create payment URL")
                                .error("Error generating payment URL")
                                .data(null)
                                .build());
            }

            VnPayPaymentResponse response = VnPayPaymentResponse.builder()
                    .paymentUrl(paymentUrl)
                    .transactionId(savedTransaction.getId())
                    .message("Payment URL created successfully")
                    .build();

            return ResponseEntity.ok(ResponseData.<VnPayPaymentResponse>builder()
                    .status(200)
                    .message("Payment URL created successfully")
                    .error(null)
                    .data(response)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<VnPayPaymentResponse>builder()
                            .status(500)
                            .message("Failed to create payment")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Handle return URL from VNPAY (user redirects back)
     */
    @GetMapping("/return")
    public ResponseEntity<ResponseData<Map<String, Object>>> paymentReturn(
            @RequestParam Map<String, String> params) {
        try {
            // Verify checksum
            if (!vnPayService.verifyChecksum(new HashMap<>(params))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<Map<String, Object>>builder()
                                .status(400)
                                .message("Invalid checksum")
                                .error("Checksum verification failed")
                                .data(null)
                                .build());
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_BankCode = params.get("vnp_BankCode");
            String vnp_PayDate = params.get("vnp_PayDate");

            // Get transaction
            Integer transactionId = Integer.parseInt(vnp_TxnRef);
            Optional<Tran> transactionOpt = tranService.findById(transactionId);
            
            if (transactionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<Map<String, Object>>builder()
                                .status(404)
                                .message("Transaction not found")
                                .error("Transaction with id " + transactionId + " not found")
                                .data(null)
                                .build());
            }

            Tran transaction = transactionOpt.get();
            
            // Update transaction detail with VNPAY response
            Map<String, Object> detail = transaction.getDetail() != null ? 
                    new HashMap<>(transaction.getDetail()) : new HashMap<>();
            detail.put("vnp_ResponseCode", vnp_ResponseCode);
            detail.put("vnp_TransactionNo", vnp_TransactionNo);
            detail.put("vnp_BankCode", vnp_BankCode);
            detail.put("vnp_PayDate", vnp_PayDate);
            detail.put("vnp_Amount", vnp_Amount);
            transaction.setDetail(detail);

            // Update transaction status
            if ("00".equals(vnp_ResponseCode)) {
                transaction.setStatus(1); // Success
                transaction.setResponseCode(0);
                
                // Create UserCourse when payment is successful
                createUserCourse(transaction);
            } else {
                transaction.setStatus(2); // Failed
                transaction.setResponseCode(Integer.parseInt(vnp_ResponseCode));
            }
            transaction.setUpdatedAt(Instant.now());
            tranService.update(transactionId, transaction);

            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("transactionId", transactionId);
            responseData.put("status", transaction.getStatus());
            responseData.put("responseCode", vnp_ResponseCode);
            responseData.put("message", "00".equals(vnp_ResponseCode) ? "Payment successful" : "Payment failed");
            responseData.put("vnp_TransactionNo", vnp_TransactionNo);

            return ResponseEntity.ok(ResponseData.<Map<String, Object>>builder()
                    .status(200)
                    .message("Payment processed")
                    .error(null)
                    .data(responseData)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Map<String, Object>>builder()
                            .status(500)
                            .message("Failed to process payment return")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Handle IPN (Instant Payment Notification) from VNPAY (server-to-server)
     */
    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> paymentIpn(
            @RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify checksum
            if (!vnPayService.verifyChecksum(new HashMap<>(params))) {
                response.put("RspCode", "97");
                response.put("Message", "Checksum failed");
                return ResponseEntity.ok(response);
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_BankCode = params.get("vnp_BankCode");
            String vnp_PayDate = params.get("vnp_PayDate");

            // Get transaction
            Integer transactionId = Integer.parseInt(vnp_TxnRef);
            Optional<Tran> transactionOpt = tranService.findById(transactionId);
            
            if (transactionOpt.isEmpty()) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return ResponseEntity.ok(response);
            }

            Tran transaction = transactionOpt.get();
            
            // Verify amount
            BigDecimal expectedAmount = transaction.getAmount().multiply(BigDecimal.valueOf(100));
            BigDecimal receivedAmount = new BigDecimal(vnp_Amount);
            if (!expectedAmount.equals(receivedAmount)) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return ResponseEntity.ok(response);
            }

            // Check if transaction already processed
            if (transaction.getStatus() == 1) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return ResponseEntity.ok(response);
            }

            // Update transaction detail with VNPAY response
            Map<String, Object> detail = transaction.getDetail() != null ? 
                    new HashMap<>(transaction.getDetail()) : new HashMap<>();
            detail.put("vnp_ResponseCode", vnp_ResponseCode);
            detail.put("vnp_TransactionNo", vnp_TransactionNo);
            detail.put("vnp_BankCode", vnp_BankCode);
            detail.put("vnp_PayDate", vnp_PayDate);
            detail.put("vnp_Amount", vnp_Amount);
            transaction.setDetail(detail);

            // Update transaction status
            if ("00".equals(vnp_ResponseCode)) {
                transaction.setStatus(1); // Success
                transaction.setResponseCode(0);
                
                // Create UserCourse when payment is successful
                createUserCourse(transaction);
                
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            } else {
                transaction.setStatus(2); // Failed
                transaction.setResponseCode(Integer.parseInt(vnp_ResponseCode));
                response.put("RspCode", "00");
                response.put("Message", "Order failed");
            }
            transaction.setUpdatedAt(Instant.now());
            tranService.update(transactionId, transaction);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Create UserCourse when payment is successful
     */
    private void createUserCourse(Tran transaction) {
        try {
            Map<String, Object> detail = transaction.getDetail();
            if (detail == null || !detail.containsKey("courseId")) {
                return;
            }

            Integer courseId = (Integer) detail.get("courseId");
            Optional<Course> courseOpt = courseService.getOne(courseId);
            if (courseOpt.isEmpty()) {
                return;
            }

            // Check if UserCourse already exists
            Optional<UserCourse> existingUserCourse = userCourseService.findByUserIdAndCourseId(
                    transaction.getUser().getId(), courseId);

            if (existingUserCourse.isPresent()) {
                // Update existing UserCourse
                UserCourse userCourse = existingUserCourse.get();
                userCourse.setTran(transaction);
                userCourse.setStatus(1);
                userCourse.setExpiredAt(Instant.now().plusSeconds(6L * 30 * 24 * 60 * 60)); // 6 months
                userCourse.setUpdatedAt(Instant.now());
                userCourseService.update(userCourse.getId(), userCourse);
            } else {
                // Create new UserCourse
                UserCourse userCourse = new UserCourse();
                userCourse.setUser(transaction.getUser());
                userCourse.setCourse(courseOpt.get());
                userCourse.setTran(transaction);
                userCourse.setEnrolledAt(Instant.now());
                userCourse.setExpiredAt(Instant.now().plusSeconds(6L * 30 * 24 * 60 * 60)); // 6 months
                userCourse.setStatus(1);
                userCourse.setIsDeleted(0);
                userCourse.setCreatedAt(Instant.now());
                userCourse.setUpdatedAt(Instant.now());
                userCourseService.create(userCourse);
            }
        } catch (Exception e) {
            // Log error but don't fail the transaction
            e.printStackTrace();
        }
    }
}

