package com.devmam.taraacademyapi.service.impl;

import com.devmam.taraacademyapi.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of VNPAY payment service
 */
@Service
public class VnPayServiceImpl implements VnPayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String vnpayUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.ipnUrl}")
    private String ipnUrl;

    @Value("${vnpay.timeoutMinutes:60}")
    private int timeoutMinutes;

    @Override
    public String createPaymentUrl(BigDecimal amount, String orderInfo, String orderType,
                                   String bankCode, String language, String ipAddress, Integer transactionId) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = String.valueOf(transactionId);
            
            // Convert IPv6 localhost to IPv4 (VNPay may not support IPv6)
            String vnp_IpAddr = ipAddress;
            if (vnp_IpAddr != null && (vnp_IpAddr.equals("0:0:0:0:0:0:0:1") || vnp_IpAddr.equals("::1"))) {
                vnp_IpAddr = "127.0.0.1";
            }
            
            String vnp_Amount = String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue());
            
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", tmnCode);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_CurrCode", "VND");
            
            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            // VNPay requires orderInfo to be ASCII only, no Vietnamese accents or special characters
            String finalOrderInfo = "Thanh toan don hang";
            if (orderInfo != null && !orderInfo.isEmpty()) {
                // Remove Vietnamese accents and special characters, keep only ASCII
                finalOrderInfo = orderInfo.replaceAll("[^\\x00-\\x7F]", "")
                    .replaceAll("[^a-zA-Z0-9\\s]", "")
                    .trim();
                if (finalOrderInfo.isEmpty()) {
                    finalOrderInfo = "Thanh toan don hang";
                }
            }
            vnp_Params.put("vnp_OrderInfo", finalOrderInfo);
            vnp_Params.put("vnp_OrderType", orderType != null && !orderType.isEmpty() ? orderType : "other");
            
            if (language != null && !language.isEmpty()) {
                vnp_Params.put("vnp_Locale", language);
            } else {
                vnp_Params.put("vnp_Locale", "vn");
            }
            
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            
            // Use Vietnam timezone (GMT+7) - Asia/Ho_Chi_Minh is the standard timezone ID
            TimeZone vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            Calendar cld = Calendar.getInstance(vietnamTimeZone);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(vietnamTimeZone); // Set timezone for formatter
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            
            // Set expiration time (configurable, default 60 minutes)
            cld.add(Calendar.MINUTE, timeoutMinutes);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            // Build hash data and query string - only include fields with non-empty values
            List<String> validFields = new ArrayList<>();
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    validFields.add(fieldName);
                }
            }
            
            for (int i = 0; i < validFields.size(); i++) {
                String fieldName = validFields.get(i);
                String fieldValue = vnp_Params.get(fieldName);
                
                // Build hash data - field name is NOT encoded, only value is encoded
                // VNPay requires URL encoding for hash data values
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(encodedValue);
                
                // Build query - both field name and value are encoded
                // For query string, keep + for spaces (standard URL encoding)
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                
                // Add & separator if not the last field
                if (i < validFields.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
            
            String queryUrl = query.toString();
            String hashDataString = hashData.toString();
            String vnp_SecureHash = hmacSHA512(hashSecret, hashDataString);
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnpayUrl + "?" + queryUrl;
            
            // Debug logging (remove in production)
            System.out.println("VNPay Hash Data: " + hashDataString);
            System.out.println("VNPay Secure Hash: " + vnp_SecureHash);
            
            return paymentUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verifyChecksum(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            return false;
        }
        
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        
        // Build hash data - only include fields with non-empty values
        List<String> validFields = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                validFields.add(fieldName);
            }
        }
        
        for (int i = 0; i < validFields.size(); i++) {
            String fieldName = validFields.get(i);
            String fieldValue = params.get(fieldName);
            
            hashData.append(fieldName);
            hashData.append('=');
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
            
            // Add & separator if not the last field
            if (i < validFields.size() - 1) {
                hashData.append('&');
            }
        }
        
        String checkSum = hmacSHA512(hashSecret, hashData.toString());
        return checkSum.equals(vnp_SecureHash);
    }

    @Override
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Convert IPv6 localhost to IPv4 (VNPay may not support IPv6)
        if (ipAddress != null && (ipAddress.equals("0:0:0:0:0:0:0:1") || ipAddress.equals("::1"))) {
            ipAddress = "127.0.0.1";
        }
        
        return ipAddress;
    }

    /**
     * Generate HMAC SHA512 hash
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

