package com.devmam.taraacademyapi.service;

import java.util.List;

/**
 * Service interface for exporting data to Excel
 */
public interface ExcelExportService {
    
    /**
     * Export Contact list to Excel
     * @param contacts List of contacts to export
     * @return Excel file as byte array
     */
    byte[] exportContactsToExcel(List<com.devmam.taraacademyapi.models.entities.Contact> contacts);
    
    /**
     * Export Transactions to Excel
     * @param transactions List of transactions to export
     * @return Excel file as byte array
     */
    byte[] exportTransactionsToExcel(List<com.devmam.taraacademyapi.models.entities.Tran> transactions);
}

