# 📦 master

A simple **Spring Boot** REST API showing:
✅ JWT Authentication  
✅ Role-based access (`admin`, `vendor`, `customer`)  
✅ CRUD for products with inventory  
✅ Orders with multiple product IDs  
✅ `@PreAuthorize` with JWT roles  
✅ Global exception handling that returns `403` properly  
✅ E2E tests with `TestRestTemplate`

---

## ⚡ Features

| Feature | Description |
|---------|-------------|
| 🔒 JWT Auth | Login with username/password, get token |
| 👥 Roles | `admin`, `vendor`, `customer` |
| 📦 Products | Vendor-only add/update/delete, anyone can view |
| 🛒 Orders | Customers place orders with product IDs |
| ✅ RBAC | Uses `@PreAuthorize` to restrict endpoints |
| 🧪 E2E Tests | Full test of JWT + secured flow |
| ⚙️ Global Handler | Returns `403` correctly if roles don’t match |

---

## 🚀 Getting Started

```bash
git clone https://github.com/Himanshu-Koshti-lab/Master.git
cd master
mvn clean spring-boot:run
