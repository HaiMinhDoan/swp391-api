# API Documentation - Quiz Excel Import & Template Download

## Tổng quan
Tài liệu này mô tả 2 API liên quan đến việc import Quiz từ file Excel và download template Excel.

---

## 1. API Download Excel Template

### Endpoint
```
GET /api/v1/quizzes/template
```

### Mô tả
API này cho phép download file template Excel để import Quiz. Template đã được format sẵn với header, hướng dẫn và ví dụ.

### Authentication
- **Không yêu cầu** authentication (permitAll)

### Request
- **Method**: `GET`
- **Headers**: Không cần header đặc biệt
- **Query Parameters**: Không có

### Response

#### Success Response (200 OK)
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Content-Disposition**: `attachment; filename="quiz_import_template.xlsx"`
- **Body**: File Excel (.xlsx) dạng binary

#### Error Response (500 Internal Server Error)
- **Body**: `null`

### Cấu trúc Template Excel

Template Excel có cấu trúc như sau:

**Row 0 (Header)**: Các cột header
- Lesson ID
- Question
- Option 1
- Is Correct 1
- Option 2
- Is Correct 2
- Option 3
- Is Correct 3
- Option 4
- Is Correct 4

**Row 1**: Hướng dẫn (màu xanh lá)
- "Lưu ý: Question là bắt buộc. Type mặc định là 'Multiple Choice'. Status mặc định là 1. Is Correct có thể là: true/false, yes/no, 1/0, đúng/sai"

**Row 2**: Ví dụ dữ liệu (màu xám)
- Lesson ID: 1
- Question: "Đâu là thủ đô của Việt Nam?"
- Option 1: "Hà Nội", Is Correct 1: true
- Option 2: "Hồ Chí Minh", Is Correct 2: false
- Option 3: "Đà Nẵng", Is Correct 3: false
- Option 4: "Huế", Is Correct 4: false

**Row 3+**: Người dùng điền dữ liệu từ đây (import sẽ bắt đầu từ row 3)

### Ví dụ sử dụng (JavaScript/TypeScript)

```javascript
// Download template
async function downloadTemplate() {
  try {
    const response = await fetch('/api/v1/quizzes/template', {
      method: 'GET',
    });
    
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'quiz_import_template.xlsx';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } else {
      console.error('Failed to download template');
    }
  } catch (error) {
    console.error('Error:', error);
  }
}
```

### Ví dụ sử dụng (Axios)

```javascript
import axios from 'axios';

async function downloadTemplate() {
  try {
    const response = await axios.get('/api/v1/quizzes/template', {
      responseType: 'blob',
    });
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'quiz_import_template.xlsx');
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (error) {
    console.error('Error downloading template:', error);
  }
}
```

---

## 2. API Import Quiz từ Excel

### Endpoint
```
POST /api/v1/quizzes/import-excel
```

### Mô tả
API này cho phép import nhiều Quiz từ file Excel. API sẽ đọc file Excel, validate dữ liệu và tạo các Quiz cùng với QuizOption tương ứng.

### Authentication
- **Yêu cầu**: Role `ADMIN` hoặc `USER`
- **Header**: `Authorization: Bearer <token>`

### Request
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Body**: 
  - `file` (MultipartFile): File Excel (.xlsx hoặc .xls)

### Response

#### Success Response (200 OK)
```json
{
  "status": 200,
  "message": "Import completed: 5 successful, 2 failed out of 7 total rows",
  "error": null,
  "data": {
    "totalRows": 7,
    "successfulImports": 5,
    "failedImports": 2,
    "errors": [
      {
        "rowNumber": 3,
        "message": "Question is required",
        "quizQuestion": null
      },
      {
        "rowNumber": 6,
        "message": "Error: Lesson with ID 999 not found",
        "quizQuestion": "Câu hỏi test"
      }
    ]
  }
}
```

#### Error Response (400 Bad Request)
```json
{
  "status": 400,
  "message": "File is required",
  "error": "File cannot be empty",
  "data": null
}
```

```json
{
  "status": 400,
  "message": "Invalid file type",
  "error": "File must be an Excel file (.xlsx or .xls)",
  "data": null
}
```

#### Error Response (500 Internal Server Error)
```json
{
  "status": 500,
  "message": "Failed to import quizzes from Excel",
  "error": "Error details...",
  "data": null
}
```

### Response Data Structure

#### QuizExcelImportResultDto
```typescript
interface QuizExcelImportResultDto {
  totalRows: number;              // Tổng số hàng trong file
  successfulImports: number;      // Số Quiz import thành công
  failedImports: number;          // Số Quiz import thất bại
  errors: ImportError[];          // Danh sách lỗi chi tiết
}

interface ImportError {
  rowNumber: number;              // Số hàng bị lỗi (bắt đầu từ 1)
  message: string;                // Thông báo lỗi
  quizQuestion: string | null;     // Nội dung câu hỏi (nếu có) để dễ nhận biết
}
```

### Format Excel File

File Excel phải có cấu trúc như sau:

| Lesson ID | Type | Question | Status | Teacher Note | Option 1 | Is Correct 1 | Option 2 | Is Correct 2 | Option 3 | Is Correct 3 | Option 4 | Is Correct 4 |
|-----------|------|----------|--------|--------------|----------|---------------|----------|---------------|----------|---------------|----------|---------------|
| 1 | Multiple Choice | Đâu là thủ đô của Việt Nam? | 1 | Câu hỏi cơ bản | Hà Nội | true | Hồ Chí Minh | false | Đà Nẵng | false | Huế | false |

### Chi tiết các cột

1. **Lesson ID** (Integer, Optional)
   - ID của Lesson mà Quiz thuộc về
   - Nếu không có, Quiz sẽ không gắn với Lesson nào

2. **Type** (String, Optional)
   - Loại Quiz
   - **Mặc định**: "Multiple Choice" nếu không điền
   - Có thể để trống, hệ thống sẽ tự động set là "Multiple Choice"

3. **Question** (String, **Required**)
   - Nội dung câu hỏi
   - **Bắt buộc**: Nếu thiếu sẽ báo lỗi

4. **Status** (Integer, Optional)
   - Trạng thái Quiz
   - **Mặc định**: 1 nếu không điền
   - 1 = Active, 0 = Inactive

5. **Teacher Note** (String, Optional)
   - Ghi chú của giáo viên
   - Có thể để trống

6. **Option 1-4** (String, Optional)
   - Nội dung của các lựa chọn (tối đa 4 options)
   - Nếu Option rỗng sẽ bỏ qua

7. **Is Correct 1-4** (Boolean, Optional)
   - Đánh dấu option có đúng hay không
   - Chấp nhận các giá trị:
     - `true` / `false`
     - `yes` / `no`
     - `1` / `0`
     - `đúng` / `sai`
   - Mặc định: `false` nếu không có

### Lưu ý về Import

- **Import bắt đầu từ Row 3**: Hệ thống sẽ tự động bỏ qua Row 0 (header), Row 1 (hướng dẫn), và Row 2 (ví dụ)
- **Type mặc định**: Nếu không điền Type, hệ thống sẽ tự động set là "Multiple Choice"
- **Status mặc định**: Nếu không điền Status, hệ thống sẽ tự động set là 1 (Active)

### Validation Rules

1. **Question là bắt buộc**: Nếu thiếu sẽ báo lỗi và bỏ qua hàng đó
2. **Lesson ID**: Nếu không tồn tại trong database, sẽ báo lỗi
3. **File format**: Chỉ chấp nhận file .xlsx hoặc .xls
4. **Empty rows**: Các hàng trống sẽ được bỏ qua tự động
5. **Import từ Row 3**: API tự động bỏ qua Row 0 (header), Row 1 (hướng dẫn), Row 2 (ví dụ) và bắt đầu import từ Row 3
6. **Type mặc định**: Nếu không điền Type, tự động set là "Multiple Choice"
7. **Status mặc định**: Nếu không điền Status, tự động set là 1

### Ví dụ sử dụng (JavaScript/TypeScript)

```javascript
// Import từ file Excel
async function importQuizzes(file) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch('/api/v1/quizzes/import-excel', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      body: formData,
    });
    
    const result = await response.json();
    
    if (response.ok) {
      console.log(`Import thành công: ${result.data.successfulImports}/${result.data.totalRows}`);
      if (result.data.errors.length > 0) {
        console.warn('Có lỗi:', result.data.errors);
      }
    } else {
      console.error('Import thất bại:', result.message);
    }
    
    return result;
  } catch (error) {
    console.error('Error:', error);
  }
}

// Sử dụng với input file
const fileInput = document.querySelector('input[type="file"]');
fileInput.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (file) {
    await importQuizzes(file);
  }
});
```

### Ví dụ sử dụng (Axios)

```javascript
import axios from 'axios';

async function importQuizzes(file) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await axios.post('/api/v1/quizzes/import-excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`,
      },
    });
    
    const result = response.data;
    console.log(`Import thành công: ${result.data.successfulImports}/${result.data.totalRows}`);
    
    if (result.data.errors.length > 0) {
      console.warn('Có lỗi:', result.data.errors);
      // Hiển thị lỗi cho user
      result.data.errors.forEach(error => {
        console.log(`Hàng ${error.rowNumber}: ${error.message}`);
      });
    }
    
    return result;
  } catch (error) {
    if (error.response) {
      console.error('Error:', error.response.data);
    } else {
      console.error('Error:', error.message);
    }
    throw error;
  }
}
```

### Ví dụ sử dụng với React

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function QuizImportComponent() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleDownloadTemplate = async () => {
    try {
      const response = await axios.get('/api/v1/quizzes/template', {
        responseType: 'blob',
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'quiz_import_template.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading template:', error);
    }
  };

  const handleImport = async () => {
    if (!file) {
      alert('Vui lòng chọn file');
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await axios.post('/api/v1/quizzes/import-excel', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`,
        },
      });
      
      setResult(response.data);
      alert(`Import thành công: ${response.data.data.successfulImports}/${response.data.data.totalRows} Quiz`);
    } catch (error) {
      console.error('Import error:', error);
      alert('Import thất bại: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Import Quiz từ Excel</h2>
      
      <button onClick={handleDownloadTemplate}>
        Download Template
      </button>
      
      <div>
        <input type="file" accept=".xlsx,.xls" onChange={handleFileChange} />
        <button onClick={handleImport} disabled={loading || !file}>
          {loading ? 'Đang import...' : 'Import'}
        </button>
      </div>
      
      {result && result.data && (
        <div>
          <h3>Kết quả:</h3>
          <p>Tổng số hàng: {result.data.totalRows}</p>
          <p>Thành công: {result.data.successfulImports}</p>
          <p>Thất bại: {result.data.failedImports}</p>
          
          {result.data.errors.length > 0 && (
            <div>
              <h4>Lỗi chi tiết:</h4>
              <ul>
                {result.data.errors.map((error, index) => (
                  <li key={index}>
                    Hàng {error.rowNumber}: {error.message}
                    {error.quizQuestion && ` (Câu hỏi: ${error.quizQuestion})`}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default QuizImportComponent;
```

---

## Lưu ý quan trọng

1. **File size**: Nên giới hạn kích thước file (ví dụ: max 10MB)
2. **Error handling**: Luôn kiểm tra và hiển thị lỗi cho user
3. **Progress indicator**: Nên hiển thị loading khi đang import
4. **File validation**: Validate file type ở frontend trước khi gửi
5. **Template**: Luôn khuyến khích user download template trước khi import
6. **Error display**: Hiển thị chi tiết lỗi theo từng hàng để user dễ sửa

---

## Base URL

Tất cả các API endpoint đều có base URL: `/api/v1/quizzes`

---

## Support

Nếu có vấn đề hoặc câu hỏi, vui lòng liên hệ backend team.

