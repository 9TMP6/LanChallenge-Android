# LanChallenge - Personalized Vocabulary Trainer

**LanChallenge là ứng dụng Android giúp người dùng học và luyện tập từ vựng cá nhân hóa thông qua các bài test trắc nghiệm gamified, hỗ trợ quản lý dữ liệu linh hoạt từ file `.txt`.

---

## 🚀 Tính năng chính
* **Quản lý từ vựng:** Thêm, sửa, xóa từ vựng cá nhân hóa.
* **Nhập/Xuất dữ liệu (.txt):** Tích hợp Storage Access Framework (SAF) để đọc/ghi file từ bộ nhớ máy.
* **Thuật toán ôn tập:** Luyện tập trắc nghiệm thông minh dựa trên lịch sử từ hay sai.
* **Đăng nhập & Đồng bộ:** Xác thực tài khoản với Firebase Authentication.

---

## 🛠 Kiến trúc & Công nghệ (Tech Stack)
* **Language:** Kotlin
* **UI:** XML Layouts, Material Design
* **Architecture:** MVVM (Model-View-ViewModel) / Clean Architecture
* **Database & Storage:** Room Database, File I/O (`FileInputStream`/`FileOutputStream`), ContentResolver
* **Backend:** Firebase Auth
