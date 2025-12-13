# Excel Export API Documentation

## Tổng quan

API này cung cấp chức năng export dữ liệu ra file Excel với đầy đủ tính năng filter và sort, tương tự như API `/filter` hiện có.

---

## 1. Export Contact List to Excel

### Endpoint
```
POST /api/v1/contacts/export/excel
```

### Authentication
- **Required**: Yes
- **Role**: ADMIN only
- **Header**: `Authorization: Bearer <JWT_TOKEN>`

### Request Body
Request body là **optional**. Nếu không gửi body hoặc gửi `{}`, API sẽ export tất cả contacts.

```json
{
  "filters": [
    {
      "fieldName": "string",
      "operation": "EQUALS | NOT_EQUALS | GREATER_THAN | LESS_THAN | GREATER_THAN_OR_EQUAL | LESS_THAN_OR_EQUAL | CONTAINS | NOT_CONTAINS | STARTS_WITH | ENDS_WITH | IN | NOT_IN",
      "value": "any",
      "logicType": "AND | OR"
    }
  ],
  "sorts": [
    {
      "fieldName": "string",
      "direction": "ASC | DESC"
    }
  ],
  "page": 0,
  "size": 1000
}
```

### Request Body Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `filters` | Array | No | `[]` | Danh sách điều kiện lọc (giống như `/filter` endpoint) |
| `sorts` | Array | No | `[]` | Danh sách sắp xếp (giống như `/filter` endpoint) |
| `page` | Integer | No | `0` | Số trang bắt đầu (API sẽ tự động lấy tất cả pages) |
| `size` | Integer | No | `1000` | Kích thước mỗi page khi fetch dữ liệu |

### Filter Criteria

Mỗi filter object có cấu trúc:

```json
{
  "fieldName": "email",           // Tên field trong entity Contact
  "operation": "CONTAINS",        // Toán tử so sánh
  "value": "gmail.com",          // Giá trị so sánh
  "logicType": "AND"              // AND hoặc OR (mặc định: AND)
}
```

#### Các field có thể filter:
- `id` (Integer)
- `fullName` (String)
- `email` (String)
- `phone` (String)
- `company` (String)
- `personalRole` (String)
- `subject` (String)
- `message` (String)
- `status` (Integer)
- `createdAt` (Instant/DateTime)
- `updatedAt` (Instant/DateTime)
- `isDeleted` (Integer)
- `services.id` (Integer) - Filter theo service ID

#### Các toán tử filter (FilterOperation):
- `EQUALS` - Bằng chính xác
- `NOT_EQUALS` - Khác
- `GREATER_THAN` - Lớn hơn
- `LESS_THAN` - Nhỏ hơn
- `GREATER_THAN_OR_EQUAL` - Lớn hơn hoặc bằng
- `LESS_THAN_OR_EQUAL` - Nhỏ hơn hoặc bằng
- `CONTAINS` - Chứa (case-insensitive)
- `NOT_CONTAINS` - Không chứa
- `STARTS_WITH` - Bắt đầu bằng
- `ENDS_WITH` - Kết thúc bằng
- `IN` - Trong danh sách
- `NOT_IN` - Không trong danh sách

### Sort Criteria

Mỗi sort object có cấu trúc:

```json
{
  "fieldName": "createdAt",
  "direction": "DESC"
}
```

#### Direction:
- `ASC` - Tăng dần
- `DESC` - Giảm dần

### Response

**Success Response (200 OK)**
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Body**: Excel file (binary)
- **Headers**:
  - `Content-Disposition: attachment; filename="contacts_export.xlsx"`
  - `Content-Length: <file_size>`

**Error Response (401 Unauthorized)**
```json
{
  "status": 401,
  "message": "Unauthorized",
  "error": "Invalid or missing token"
}
```

**Error Response (403 Forbidden)**
```json
{
  "status": 403,
  "message": "Forbidden",
  "error": "Access denied. Admin role required."
}
```

**Error Response (500 Internal Server Error)**
```json
{
  "status": 500,
  "message": "Internal Server Error",
  "error": "Error message"
}
```

### Excel File Format

File Excel sẽ chứa các cột sau:
1. **ID** - Contact ID
2. **Full Name** - Tên đầy đủ
3. **Email** - Email
4. **Phone** - Số điện thoại
5. **Company** - Công ty
6. **Personal Role** - Vai trò
7. **Subject** - Chủ đề
8. **Message** - Nội dung tin nhắn
9. **Service Name** - Tên service (nếu có)
10. **Status** - Trạng thái
11. **Created At** - Ngày tạo
12. **Updated At** - Ngày cập nhật

### Example Requests

#### 1. Export tất cả contacts
```http
POST /api/v1/contacts/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{}
```

#### 2. Export contacts có email chứa "gmail.com"
```http
POST /api/v1/contacts/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "email",
      "operation": "CONTAINS",
      "value": "gmail.com",
      "logicType": "AND"
    }
  ]
}
```

#### 3. Export contacts với filter và sort
```http
POST /api/v1/contacts/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "status",
      "operation": "EQUALS",
      "value": 1,
      "logicType": "AND"
    },
    {
      "fieldName": "createdAt",
      "operation": "GREATER_THAN_OR_EQUAL",
      "value": "2024-01-01T00:00:00Z",
      "logicType": "AND"
    }
  ],
  "sorts": [
    {
      "fieldName": "createdAt",
      "direction": "DESC"
    },
    {
      "fieldName": "fullName",
      "direction": "ASC"
    }
  ]
}
```

#### 4. Export contacts với multiple filters (AND/OR)
```http
POST /api/v1/contacts/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "email",
      "operation": "CONTAINS",
      "value": "gmail",
      "logicType": "AND"
    },
    {
      "fieldName": "status",
      "operation": "EQUALS",
      "value": 1,
      "logicType": "OR"
    }
  ]
}
```

---

## 2. Export Transactions to Excel

### Endpoint
```
POST /api/v1/transactions/export/excel
```

### Authentication
- **Required**: Yes
- **Role**: ADMIN only
- **Header**: `Authorization: Bearer <JWT_TOKEN>`

### Request Body
Request body là **optional**. Nếu không gửi body hoặc gửi `{}`, API sẽ export tất cả transactions.

```json
{
  "filters": [
    {
      "fieldName": "string",
      "operation": "EQUALS | NOT_EQUALS | GREATER_THAN | LESS_THAN | GREATER_THAN_OR_EQUAL | LESS_THAN_OR_EQUAL | CONTAINS | NOT_CONTAINS | STARTS_WITH | ENDS_WITH | IN | NOT_IN",
      "value": "any",
      "logicType": "AND | OR"
    }
  ],
  "sorts": [
    {
      "fieldName": "string",
      "direction": "ASC | DESC"
    }
  ],
  "page": 0,
  "size": 1000
}
```

### Request Body Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `filters` | Array | No | `[]` | Danh sách điều kiện lọc (giống như `/filter` endpoint) |
| `sorts` | Array | No | `[]` | Danh sách sắp xếp (giống như `/filter` endpoint) |
| `page` | Integer | No | `0` | Số trang bắt đầu (API sẽ tự động lấy tất cả pages) |
| `size` | Integer | No | `1000` | Kích thước mỗi page khi fetch dữ liệu |

### Filter Criteria

Mỗi filter object có cấu trúc:

```json
{
  "fieldName": "amount",
  "operation": "GREATER_THAN",
  "value": 1000000,
  "logicType": "AND"
}
```

#### Các field có thể filter:
- `id` (Integer)
- `amount` (BigDecimal)
- `method` (String)
- `responseCode` (Integer)
- `status` (Integer)
- `createdAt` (Instant/DateTime)
- `updatedAt` (Instant/DateTime)
- `isDeleted` (Integer)
- `user.id` (UUID) - Filter theo user ID
- `user.email` (String) - Filter theo user email

#### Các toán tử filter: (giống như Contact API)

### Sort Criteria

Mỗi sort object có cấu trúc:

```json
{
  "fieldName": "createdAt",
  "direction": "DESC"
}
```

### Response

**Success Response (200 OK)**
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Body**: Excel file (binary)
- **Headers**:
  - `Content-Disposition: attachment; filename="transactions_export.xlsx"`
  - `Content-Length: <file_size>`

**Error Responses**: (giống như Contact API)

### Excel File Format

File Excel sẽ chứa các cột sau:
1. **ID** - Transaction ID
2. **User ID** - ID của user
3. **User Email** - Email của user
4. **User Name** - Tên đầy đủ của user
5. **Amount** - Số tiền (formatted as currency)
6. **Method** - Phương thức thanh toán
7. **Response Code** - Mã phản hồi
8. **Status** - Trạng thái
9. **Detail** - Chi tiết (JSON string)
10. **Created At** - Ngày tạo
11. **Updated At** - Ngày cập nhật

### Example Requests

#### 1. Export tất cả transactions
```http
POST /api/v1/transactions/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{}
```

#### 2. Export transactions có amount > 1,000,000
```http
POST /api/v1/transactions/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "amount",
      "operation": "GREATER_THAN",
      "value": 1000000,
      "logicType": "AND"
    }
  ],
  "sorts": [
    {
      "fieldName": "amount",
      "direction": "DESC"
    }
  ]
}
```

#### 3. Export transactions theo user email
```http
POST /api/v1/transactions/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "user.email",
      "operation": "EQUALS",
      "value": "user@example.com",
      "logicType": "AND"
    }
  ],
  "sorts": [
    {
      "fieldName": "createdAt",
      "direction": "DESC"
    }
  ]
}
```

#### 4. Export transactions trong khoảng thời gian
```http
POST /api/v1/transactions/export/excel
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "filters": [
    {
      "fieldName": "createdAt",
      "operation": "GREATER_THAN_OR_EQUAL",
      "value": "2024-01-01T00:00:00Z",
      "logicType": "AND"
    },
    {
      "fieldName": "createdAt",
      "operation": "LESS_THAN_OR_EQUAL",
      "value": "2024-12-31T23:59:59Z",
      "logicType": "AND"
    },
    {
      "fieldName": "status",
      "operation": "EQUALS",
      "value": 1,
      "logicType": "AND"
    }
  ],
  "sorts": [
    {
      "fieldName": "createdAt",
      "direction": "DESC"
    }
  ]
}
```

---

## Frontend Integration Guide

### JavaScript/TypeScript Example

#### 1. Export Contacts với Axios

```typescript
import axios from 'axios';

interface FilterCriteria {
  fieldName: string;
  operation: string;
  value: any;
  logicType?: 'AND' | 'OR';
}

interface SortCriteria {
  fieldName: string;
  direction: 'ASC' | 'DESC';
}

interface ExportRequest {
  filters?: FilterCriteria[];
  sorts?: SortCriteria[];
  page?: number;
  size?: number;
}

async function exportContactsToExcel(
  token: string,
  filters?: FilterCriteria[],
  sorts?: SortCriteria[]
): Promise<void> {
  try {
    const requestBody: ExportRequest = {
      filters: filters || [],
      sorts: sorts || [],
      page: 0,
      size: 1000
    };

    const response = await axios.post(
      '/api/v1/contacts/export/excel',
      requestBody,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        responseType: 'blob' // Quan trọng: phải set responseType là 'blob'
      }
    );

    // Tạo download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'contacts_export.xlsx');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Error exporting contacts:', error);
    throw error;
  }
}

// Sử dụng
exportContactsToExcel(
  'your-jwt-token',
  [
    {
      fieldName: 'email',
      operation: 'CONTAINS',
      value: 'gmail.com',
      logicType: 'AND'
    }
  ],
  [
    {
      fieldName: 'createdAt',
      direction: 'DESC'
    }
  ]
);
```

#### 2. Export Transactions với Fetch API

```typescript
async function exportTransactionsToExcel(
  token: string,
  filters?: FilterCriteria[],
  sorts?: SortCriteria[]
): Promise<void> {
  try {
    const requestBody: ExportRequest = {
      filters: filters || [],
      sorts: sorts || [],
      page: 0,
      size: 1000
    };

    const response = await fetch('/api/v1/transactions/export/excel', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'transactions_export.xlsx');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Error exporting transactions:', error);
    throw error;
  }
}
```

#### 3. React Component Example

```tsx
import React, { useState } from 'react';
import axios from 'axios';

interface ExportButtonProps {
  token: string;
  filters?: FilterCriteria[];
  sorts?: SortCriteria[];
  type: 'contacts' | 'transactions';
}

const ExportButton: React.FC<ExportButtonProps> = ({ token, filters, sorts, type }) => {
  const [loading, setLoading] = useState(false);

  const handleExport = async () => {
    setLoading(true);
    try {
      const endpoint = type === 'contacts' 
        ? '/api/v1/contacts/export/excel'
        : '/api/v1/transactions/export/excel';

      const response = await axios.post(
        endpoint,
        {
          filters: filters || [],
          sorts: sorts || [],
          page: 0,
          size: 1000
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          responseType: 'blob'
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${type}_export.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Export error:', error);
      alert('Export failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <button 
      onClick={handleExport} 
      disabled={loading}
      className="export-button"
    >
      {loading ? 'Exporting...' : `Export ${type} to Excel`}
    </button>
  );
};

export default ExportButton;
```

### Vue.js Example

```vue
<template>
  <button 
    @click="exportToExcel" 
    :disabled="loading"
    class="export-btn"
  >
    {{ loading ? 'Exporting...' : 'Export to Excel' }}
  </button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import axios from 'axios';

const props = defineProps<{
  token: string;
  filters?: FilterCriteria[];
  sorts?: SortCriteria[];
  type: 'contacts' | 'transactions';
}>();

const loading = ref(false);

const exportToExcel = async () => {
  loading.value = true;
  try {
    const endpoint = props.type === 'contacts'
      ? '/api/v1/contacts/export/excel'
      : '/api/v1/transactions/export/excel';

    const response = await axios.post(
      endpoint,
      {
        filters: props.filters || [],
        sorts: props.sorts || [],
        page: 0,
        size: 1000
      },
      {
        headers: {
          'Authorization': `Bearer ${props.token}`,
          'Content-Type': 'application/json'
        },
        responseType: 'blob'
      }
    );

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${props.type}_export.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Export error:', error);
    alert('Export failed. Please try again.');
  } finally {
    loading.value = false;
  }
};
</script>
```

---

## Lưu ý quan trọng

1. **Response Type**: Phải set `responseType: 'blob'` khi gọi API để nhận file binary
2. **Authentication**: Luôn cần JWT token với role ADMIN
3. **Request Body**: Có thể gửi `{}` hoặc không gửi body để export tất cả
4. **Pagination**: API tự động lấy tất cả pages, không cần lo về pagination
5. **File Download**: Frontend cần xử lý download file từ blob response
6. **Error Handling**: Nên xử lý các trường hợp lỗi (401, 403, 500)

---

## Testing với Postman/cURL

### cURL Example

```bash
# Export all contacts
curl -X POST "http://localhost:8080/api/v1/contacts/export/excel" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' \
  --output contacts_export.xlsx

# Export with filter
curl -X POST "http://localhost:8080/api/v1/contacts/export/excel" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "filters": [
      {
        "fieldName": "email",
        "operation": "CONTAINS",
        "value": "gmail.com",
        "logicType": "AND"
      }
    ],
    "sorts": [
      {
        "fieldName": "createdAt",
        "direction": "DESC"
      }
    ]
  }' \
  --output filtered_contacts_export.xlsx
```

---

## Troubleshooting

### Lỗi 401 Unauthorized
- Kiểm tra JWT token có hợp lệ không
- Kiểm tra token có hết hạn không
- Kiểm tra header Authorization có đúng format: `Bearer <token>`

### Lỗi 403 Forbidden
- Kiểm tra user có role ADMIN không
- Token phải được tạo từ user có quyền ADMIN

### File Excel không download được
- Kiểm tra `responseType: 'blob'` đã được set chưa
- Kiểm tra browser có block download không
- Kiểm tra CORS settings

### Filter không hoạt động
- Kiểm tra tên field có đúng không (case-sensitive)
- Kiểm tra operation có được hỗ trợ không
- Kiểm tra value type có đúng với field type không

---

## Support

Nếu gặp vấn đề, vui lòng liên hệ backend team hoặc kiểm tra:
- API documentation: `/swagger-ui.html`
- Filter API documentation: `HUONG_DAN_API_FILTER.txt`

