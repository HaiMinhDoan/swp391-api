package com.devmam.taraacademyapi.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for VNPAY payment processing
 */
public interface VnPayService {
    
    /**
     * Create payment URL for VNPAY
     * @param amount Payment amount
     * @param orderInfo Order information
     * @param orderType Order type
     * @param bankCode Bank code (optional)
     * @param language Language (vi/en)
     * @param ipAddress Client IP address
     * @param transactionId Transaction ID from database
     * @return Payment URL
     */
    String createPaymentUrl(BigDecimal amount, String orderInfo, String orderType, 
                           String bankCode, String language, String ipAddress, Integer transactionId);
    
    /**
     * Verify checksum from VNPAY callback
     * @param params Parameters from VNPAY
     * @return true if checksum is valid
     */
    boolean verifyChecksum(Map<String, String> params);
    
    /**
     * Get IP address from request
     * @param request HTTP request
     * @return IP address
     */
    String getIpAddress(HttpServletRequest request);
}

