# Admin Dashboard API Documentation

Tài liệu API cho Admin Dashboard với các biểu đồ Line Chart và Pie Chart.

## Base URL
```
/api/v1/admin/dashboard
```

## Authentication
Tất cả API yêu cầu:
- Header: `Authorization: Bearer <token>`
- Role: `ADMIN`

---

## 1. Tổng quan Dashboard

### GET `/api/v1/admin/dashboard/overview`

Lấy tổng quan thống kê dashboard.

**Response:**
```json
{
  "status": 200,
  "message": "Dashboard overview retrieved successfully",
  "error": null,
  "data": {
    "totalUsers": 150,
    "totalTeachers": 25,
    "totalStudents": 125,
    "totalCourses": 50,
    "activeCourses": 45,
    "totalEnrollments": 500,
    "totalRevenue": 100000000.00,
    "monthlyRevenue": 10000000.00,
    "totalFeedbacks": 200,
    "averageRating": 4.5,
    "totalTransactions": 500,
    "pendingTransactions": 10,
    "completedTransactions": 490
  }
}
```

**Fields:**
- `totalUsers`: Tổng số users
- `totalTeachers`: Tổng số giáo viên
- `totalStudents`: Tổng số học sinh
- `totalCourses`: Tổng số khóa học
- `activeCourses`: Số khóa học đang hoạt động
- `totalEnrollments`: Tổng số lượt đăng ký
- `totalRevenue`: Tổng doanh thu (BigDecimal)
- `monthlyRevenue`: Doanh thu tháng hiện tại (BigDecimal)
- `totalFeedbacks`: Tổng số feedback
- `averageRating`: Đánh giá trung bình (Double)
- `totalTransactions`: Tổng số giao dịch
- `pendingTransactions`: Số giao dịch đang chờ
- `completedTransactions`: Số giao dịch đã hoàn thành

---

## 2. Line Chart APIs

### 2.1. Doanh thu theo thời gian

#### GET `/api/v1/admin/dashboard/charts/revenue-line`

**Query Parameters:**
- `period` (optional, default: "month"): `"month"`, `"week"`, hoặc `"day"`
- `months` (optional, default: 12): Số tháng để lấy dữ liệu (chỉ áp dụng khi period="month")

**Ví dụ:**
```bash
GET /api/v1/admin/dashboard/charts/revenue-line?period=month&months=12
GET /api/v1/admin/dashboard/charts/revenue-line?period=week&months=3
GET /api/v1/admin/dashboard/charts/revenue-line?period=day&months=1
```

**Response:**
```json
{
  "status": 200,
  "message": "Revenue line chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "label": "2024-01",
      "value": 150,
      "amount": 5000000.00,
      "date": "2024-01-01T00:00:00Z"
    },
    {
      "label": "2024-02",
      "value": 200,
      "amount": 7000000.00,
      "date": "2024-02-01T00:00:00Z"
    }
  ]
}
```

**Chart Data Structure:**
- `label`: Nhãn hiển thị (ví dụ: "2024-01", "2024-W05", "2024-01-15")
- `value`: Số lượng giao dịch (Long)
- `amount`: Tổng doanh thu (BigDecimal)
- `date`: Ngày thực tế để sắp xếp (Instant)

**Frontend Usage (Chart.js/Recharts):**
```javascript
// Line Chart với 2 datasets
const chartData = {
  labels: data.map(item => item.label),
  datasets: [
    {
      label: 'Số giao dịch',
      data: data.map(item => item.value),
      borderColor: 'rgb(75, 192, 192)',
      yAxisID: 'y',
    },
    {
      label: 'Doanh thu (VND)',
      data: data.map(item => item.amount),
      borderColor: 'rgb(255, 99, 132)',
      yAxisID: 'y1',
    }
  ]
};
```

---

### 2.2. Số user đăng ký theo thời gian

#### GET `/api/v1/admin/dashboard/charts/users-line`

**Query Parameters:**
- `period` (optional, default: "month"): `"month"` hoặc `"day"`
- `months` (optional, default: 12): Số tháng để lấy dữ liệu

**Ví dụ:**
```bash
GET /api/v1/admin/dashboard/charts/users-line?period=month&months=6
GET /api/v1/admin/dashboard/charts/users-line?period=day&months=1
```

**Response:**
```json
{
  "status": 200,
  "message": "Users line chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "label": "2024-01",
      "value": 50,
      "amount": 0.00,
      "date": "2024-01-01T00:00:00Z"
    },
    {
      "label": "2024-02",
      "value": 75,
      "amount": 0.00,
      "date": "2024-02-01T00:00:00Z"
    }
  ]
}
```

**Chart Data Structure:**
- `label`: Nhãn thời gian
- `value`: Số user đăng ký (Long)
- `amount`: Luôn là 0 (không dùng cho chart này)
- `date`: Ngày để sắp xếp

---

### 2.3. Số khóa học tạo theo thời gian

#### GET `/api/v1/admin/dashboard/charts/courses-line`

**Query Parameters:**
- `period` (optional, default: "month"): `"month"` hoặc `"day"`
- `months` (optional, default: 12): Số tháng để lấy dữ liệu

**Ví dụ:**
```bash
GET /api/v1/admin/dashboard/charts/courses-line?period=month&months=12
```

**Response:**
```json
{
  "status": 200,
  "message": "Courses line chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "label": "2024-01",
      "value": 5,
      "amount": 0.00,
      "date": "2024-01-01T00:00:00Z"
    },
    {
      "label": "2024-02",
      "value": 8,
      "amount": 0.00,
      "date": "2024-02-01T00:00:00Z"
    }
  ]
}
```

**Chart Data Structure:**
- `label`: Nhãn thời gian
- `value`: Số khóa học được tạo (Long)
- `amount`: Luôn là 0 (không dùng cho chart này)
- `date`: Ngày để sắp xếp

---

## 3. Pie Chart APIs

### 3.1. Phân bổ user theo role

#### GET `/api/v1/admin/dashboard/charts/users-pie`

**Response:**
```json
{
  "status": 200,
  "message": "Users pie chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "name": "Admin",
      "value": 5,
      "amount": 0.00,
      "color": "#FF6384",
      "percentage": 3.33
    },
    {
      "name": "Teacher",
      "value": 25,
      "amount": 0.00,
      "color": "#36A2EB",
      "percentage": 16.67
    },
    {
      "name": "Student",
      "value": 120,
      "amount": 0.00,
      "color": "#FFCE56",
      "percentage": 80.00
    }
  ]
}
```

**Chart Data Structure:**
- `name`: Tên role (Admin, Teacher, Student, Other)
- `value`: Số lượng user (Long)
- `amount`: Luôn là 0 (không dùng cho chart này)
- `color`: Màu sắc được gợi ý (String, hex color)
- `percentage`: Phần trăm của tổng (Double)

**Frontend Usage (Chart.js):**
```javascript
const pieData = {
  labels: data.map(item => item.name),
  datasets: [{
    data: data.map(item => item.value),
    backgroundColor: data.map(item => item.color),
  }]
};
```

---

### 3.2. Phân bổ khóa học theo category

#### GET `/api/v1/admin/dashboard/charts/courses-pie`

**Response:**
```json
{
  "status": 200,
  "message": "Courses pie chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "name": "Programming",
      "value": 20,
      "amount": 0.00,
      "color": "#FF6384",
      "percentage": 40.00
    },
    {
      "name": "Design",
      "value": 15,
      "amount": 0.00,
      "color": "#36A2EB",
      "percentage": 30.00
    },
    {
      "name": "Business",
      "value": 15,
      "amount": 0.00,
      "color": "#FFCE56",
      "percentage": 30.00
    }
  ]
}
```

**Chart Data Structure:**
- `name`: Tên category (hoặc "Uncategorized")
- `value`: Số lượng khóa học (Long)
- `amount`: Luôn là 0 (không dùng cho chart này)
- `color`: Màu sắc được gợi ý
- `percentage`: Phần trăm của tổng

---

### 3.3. Phân bổ doanh thu theo category

#### GET `/api/v1/admin/dashboard/charts/revenue-pie`

**Response:**
```json
{
  "status": 200,
  "message": "Revenue pie chart data retrieved successfully",
  "error": null,
  "data": [
    {
      "name": "Programming",
      "value": 200,
      "amount": 50000000.00,
      "color": "#FF6384",
      "percentage": 50.00
    },
    {
      "name": "Design",
      "value": 150,
      "amount": 30000000.00,
      "color": "#36A2EB",
      "percentage": 30.00
    },
    {
      "name": "Business",
      "value": 100,
      "amount": 20000000.00,
      "color": "#FFCE56",
      "percentage": 20.00
    }
  ]
}
```

**Chart Data Structure:**
- `name`: Tên category
- `value`: Số lượng enrollments (Long)
- `amount`: Tổng doanh thu của category (BigDecimal)
- `color`: Màu sắc được gợi ý
- `percentage`: Phần trăm doanh thu

**Frontend Usage:**
```javascript
// Pie chart với tooltip hiển thị cả value và amount
const pieData = {
  labels: data.map(item => `${item.name} (${item.percentage.toFixed(1)}%)`),
  datasets: [{
    data: data.map(item => item.amount),
    backgroundColor: data.map(item => item.color),
  }]
};
```

---

## 4. Error Responses

Tất cả API có thể trả về các lỗi sau:

**403 Forbidden:**
```json
{
  "status": 403,
  "message": "Access denied",
  "error": "You don't have ADMIN role",
  "data": null
}
```

**500 Internal Server Error:**
```json
{
  "status": 500,
  "message": "Failed to get dashboard overview",
  "error": "Error message details",
  "data": null
}
```

---

## 5. Frontend Integration Examples

### 5.1. React với Axios

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://your-api-url/api/v1/admin/dashboard';

// Lấy tổng quan
const getOverview = async (token) => {
  const response = await axios.get(`${API_BASE_URL}/overview`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data.data;
};

// Lấy dữ liệu line chart doanh thu
const getRevenueLineChart = async (token, period = 'month', months = 12) => {
  const response = await axios.get(`${API_BASE_URL}/charts/revenue-line`, {
    params: { period, months },
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data.data;
};

// Lấy dữ liệu pie chart user
const getUsersPieChart = async (token) => {
  const response = await axios.get(`${API_BASE_URL}/charts/users-pie`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data.data;
};
```

### 5.2. Vue.js với Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://your-api-url/api/v1/admin/dashboard',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});

// Lấy tổng quan
export const getOverview = () => api.get('/overview');

// Lấy line chart
export const getRevenueLineChart = (period, months) => 
  api.get('/charts/revenue-line', { params: { period, months } });

// Lấy pie chart
export const getUsersPieChart = () => api.get('/charts/users-pie');
```

### 5.3. Chart.js Integration

```javascript
// Line Chart - Doanh thu
const revenueData = await getRevenueLineChart('month', 12);

const revenueChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: revenueData.map(d => d.label),
    datasets: [
      {
        label: 'Số giao dịch',
        data: revenueData.map(d => d.value),
        borderColor: 'rgb(75, 192, 192)',
        yAxisID: 'y',
      },
      {
        label: 'Doanh thu (VND)',
        data: revenueData.map(d => d.amount),
        borderColor: 'rgb(255, 99, 132)',
        yAxisID: 'y1',
      }
    ]
  },
  options: {
    scales: {
      y: { type: 'linear', position: 'left' },
      y1: { type: 'linear', position: 'right' }
    }
  }
});

// Pie Chart - User theo role
const usersPieData = await getUsersPieChart();

const usersPieChart = new Chart(ctx2, {
  type: 'pie',
  data: {
    labels: usersPieData.map(d => d.name),
    datasets: [{
      data: usersPieData.map(d => d.value),
      backgroundColor: usersPieData.map(d => d.color),
    }]
  },
  options: {
    plugins: {
      tooltip: {
        callbacks: {
          label: function(context) {
            const item = usersPieData[context.dataIndex];
            return `${item.name}: ${item.value} (${item.percentage.toFixed(1)}%)`;
          }
        }
      }
    }
  }
});
```

### 5.4. Recharts Integration (React)

```jsx
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { PieChart, Pie, Cell } from 'recharts';

// Line Chart Component
function RevenueLineChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="label" />
        <YAxis yAxisId="left" />
        <YAxis yAxisId="right" orientation="right" />
        <Tooltip />
        <Legend />
        <Line yAxisId="left" type="monotone" dataKey="value" stroke="#8884d8" name="Số giao dịch" />
        <Line yAxisId="right" type="monotone" dataKey="amount" stroke="#82ca9d" name="Doanh thu" />
      </LineChart>
    </ResponsiveContainer>
  );
}

// Pie Chart Component
function UsersPieChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          labelLine={false}
          label={({ name, percentage }) => `${name}: ${percentage.toFixed(1)}%`}
          outerRadius={80}
          fill="#8884d8"
          dataKey="value"
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={entry.color} />
          ))}
        </Pie>
        <Tooltip />
      </PieChart>
    </ResponsiveContainer>
  );
}
```

---

## 6. Dashboard Layout Suggestion

### Overview Cards
- Hiển thị các số liệu từ `/overview` API
- Cards: Total Users, Total Courses, Total Revenue, Monthly Revenue, etc.

### Line Charts Section
1. **Revenue Chart**: Doanh thu theo thời gian (có thể switch period: month/week/day)
2. **Users Chart**: Số user đăng ký theo thời gian
3. **Courses Chart**: Số khóa học tạo theo thời gian

### Pie Charts Section
1. **Users by Role**: Phân bổ user theo role
2. **Courses by Category**: Phân bổ khóa học theo category
3. **Revenue by Category**: Phân bổ doanh thu theo category

---

## 7. Notes

1. **Authentication**: Tất cả API yêu cầu token trong header
2. **Role Check**: Chỉ user có role `ADMIN` mới có thể truy cập
3. **Data Format**: 
   - `amount` (BigDecimal) cần format thành VND khi hiển thị
   - `date` (Instant) có thể format thành string theo timezone
   - `percentage` đã được tính sẵn, có thể hiển thị trực tiếp
4. **Color**: Màu sắc được gợi ý trong pie chart, có thể override nếu cần
5. **Sorting**: Line chart data đã được sắp xếp theo `date` tăng dần

---

## 8. Example Dashboard UI Structure

```
┌─────────────────────────────────────────────────────────┐
│  Admin Dashboard                                        │
├─────────────────────────────────────────────────────────┤
│  [Overview Cards]                                       │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                │
│  │Users │ │Courses│ │Revenue│ │Rating│                │
│  └──────┘ └──────┘ └──────┘ └──────┘                │
├─────────────────────────────────────────────────────────┤
│  [Line Charts]                                         │
│  ┌──────────────────────────┐ ┌──────────────────────┐ │
│  │ Revenue Line Chart       │ │ Users Line Chart     │ │
│  │ [Month/Week/Day Select]  │ │ [Month/Day Select]   │ │
│  └──────────────────────────┘ └──────────────────────┘ │
│  ┌──────────────────────────┐                          │
│  │ Courses Line Chart        │                          │
│  │ [Month/Day Select]        │                          │
│  └──────────────────────────┘                          │
├─────────────────────────────────────────────────────────┤
│  [Pie Charts]                                           │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐   │
│  │ Users by Role│ │Courses by Cat │ │Revenue by Cat│   │
│  └──────────────┘ └──────────────┘ └──────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 9. API Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/overview` | Tổng quan dashboard |
| GET | `/charts/revenue-line` | Line chart doanh thu |
| GET | `/charts/users-line` | Line chart user đăng ký |
| GET | `/charts/courses-line` | Line chart khóa học tạo |
| GET | `/charts/users-pie` | Pie chart user theo role |
| GET | `/charts/courses-pie` | Pie chart khóa học theo category |
| GET | `/charts/revenue-pie` | Pie chart doanh thu theo category |

---

**Version:** 1.0.0  
**Last Updated:** 2024

