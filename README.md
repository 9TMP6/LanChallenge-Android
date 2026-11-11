# 📱 LanChallenge - Custom Flashcard & Quiz Learning Platform

LanChallenge là ứng dụng Android hỗ trợ tự ôn tập và kiểm tra kiến thức theo mô hình thẻ ghi nhớ (Flashcard) kết hợp trò chơi trắc nghiệm. Ứng dụng được thiết kế theo tư duy **mở**, không gò ép người dùng theo bất kỳ giáo trình hay kho bài học cố định nào. Bạn nắm quyền chủ động 100% đối với thư viện dữ liệu của mình: tự nhập, tự quản lý và tự chọn nội dung ôn tập.

Ứng dụng không chỉ dành riêng cho Tiếng Anh mà hỗ trợ mọi loại kiến thức có thể biểu diễn dưới dạng bộ ba thông tin: `Từ khóa / Khái niệm` – `Ý nghĩa / Định nghĩa` – `Mô tả / Ví dụ` (Key - Meaning - Description).

---

## 🎯 Triết lý sản phẩm

* **Tự chủ dữ liệu 100%:** Nhập kho dữ liệu cá nhân từ tệp tin `.txt` bên ngoài mà không phụ thuộc vào dữ liệu có sẵn của hệ thống.
* **Không giới hạn chủ đề:** Học từ vựng đa ngôn ngữ (Anh, Nhật, Trung, Hàn...), thuật ngữ lập trình, khái niệm lịch sử, định nghĩa y học hoặc các bộ quy tắc ghi nhớ.
* **Cấu trúc dữ liệu chuẩn hóa:** Mọi bộ bài học đều xoay quanh mô hình 3 trường cơ bản (`Key` - `Mean` - `Desc`), giúp việc tạo và nạp dữ liệu đơn giản.

---

## 🚀 Chức năng chính

### 1. Quản lý thư viện dữ liệu cá nhân
* **Tải dữ liệu từ file TXT:** Đọc và phân tích trực tiếp tệp văn bản `.txt` để tạo bộ thẻ ghi nhớ mới trong vài giây.
* **Quản lý danh mục:** Tạo, sửa, xóa các chủ đề hoặc bộ thẻ trực tiếp trên giao diện ứng dụng.

### 2. Các chế độ ôn tập & Luyện tập
* **Trắc nghiệm 4 đáp án (Multiple Choice Quiz):** Hệ thống tạo ngẫu nhiên 4 lựa chọn cho mỗi câu hỏi từ khóa, yêu cầu chọn đúng nghĩa tương ứng.
* **Gõ đáp án (Text Input Mode):** Kiểm tra độ ghi nhớ chính xác bằng cách yêu cầu người dùng tự nhập câu trả lời.
* **Duyệt thẻ ghi nhớ (Card Review):** Xem chi tiết từng thẻ gồm từ khóa, nghĩa và phần mô tả/ví dụ đi kèm.

### 3. Tài khoản & Đồng bộ đám mây
* Đăng ký, đăng nhập tài khoản cá nhân.
* Lưu trữ và đồng bộ dữ liệu bộ thẻ lên Firebase Cloud, tránh thất lạc khi đổi thiết bị.

---

## 🛠️ Công nghệ sử dụng

* **Nền tảng phát triển:** Android SDK (Java / Kotlin)
* **Giao diện người dùng (UI):** Android XML Layouts, Material Design Components
* **Dịch vụ đám mây (Backend Service):** Firebase Authentication, Firebase Realtime Database
* **Xử lý tệp tin:** Java I/O (File parsing & Text stream processing)
* **Kiến trúc ứng dụng:** Activity-Adapter Pattern kết hợp Model-View-Controller (MVC)

---

## 📄 Quy chuẩn cấu trúc tệp dữ liệu (.txt)

Để nạp dữ liệu vào ứng dụng, bạn chỉ cần chuẩn bị một tệp văn bản thuần `.txt`. Mỗi dòng đại diện cho 1 thẻ ghi nhớ, các trường phân tách bằng dấu gạch đứng `|`.

### Cấu trúc định dạng
```text
Key | Meaning | Description
```

### Ví dụ tệp nạp dữ liệu
```text
Algorithm | Thuật toán | Tập hợp các bước có thứ tự để giải quyết một bài toán cụ thể.
Recursion | Đệ quy | Hàm tự gọi lại chính nó trong quá trình thực thi.
Encapsulation | Tính đóng gói | Gói dữ liệu và các phương thức xử lý dữ liệu vào trong một đối tượng.
Async/Await | Bất đồng bộ | Cú pháp giúp xử lý các tác vụ bất đồng bộ dễ đọc hơn trong mã nguồn.
```

---

## 🏗️ Cấu trúc thư mục dự án

```text
LanChallenge-Android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/lanchallenge/
│   │       │   ├── activities/     # Màn hình đăng nhập, danh sách bộ thẻ, chế độ chơi
│   │       │   ├── adapters/       # Adapter hiển thị RecyclerView cho thẻ và thư viện
│   │       │   ├── models/         # Khai báo cấu trúc CardItem, Category, User
│   │       │   └── utils/          # Hỗ trợ đọc file TXT, thao tác Firebase
│   │       └── res/
│   │           ├── layout/         # Giao diện XML màn hình và item
│   │           ├── drawable/       # Hình ảnh, icon và bộ nguyên liệu giao diện
│   │           └── values/         # Khai báo màu sắc, chuỗi ký tự, style
│   └── build.gradle
├── build.gradle
└── README.md
```

---

## 📋 Hướng dẫn cài đặt & Thiết lập dự án

### Yêu cầu hệ thống
* Android Studio (phiên bản 2022.3 trở lên).
* JDK 11 hoặc JDK 17.
* Thiết bị Android chạy hệ điều hành Android 7.0 (API level 24) trở lên.

### Các bước thiết lập

1. **Tải mã nguồn về máy**
   ```bash
   git clone https://github.com/9TMP6/LanChallenge-Android.git
   cd LanChallenge-Android
   ```

2. **Cấu hình Firebase**
   * Truy cập [Firebase Console](https://console.firebase.google.com/) và tạo một dự án mới.
   * Thêm ứng dụng Android với Package Name tương ứng dự án (ví dụ `com.example.lanchallenge`).
   * Bật tính năng **Authentication** (phương thức Email/Password) và **Realtime Database**.
   * Tải tệp `google-services.json` về và chép vào thư mục `app/` của dự án.

3. **Biên dịch và khởi chạy**
   * Mở thư mục dự án bằng **Android Studio**.
   * Chờ Gradle đồng bộ dependencies (`File -> Sync Project with Gradle Files`).
   * Chọn thiết bị giả lập hoặc máy thật và nhấn `Run` (`Shift + F10`).
