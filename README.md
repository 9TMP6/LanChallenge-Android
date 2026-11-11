# 📚 LanChallenge - Unlimited Self-Paced Flashcard & Learning Tool

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?logo=android)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin%2FXML-orange.svg?logo=kotlin)](https://kotlinlang.org/)
[![Backend](https://img.shields.io/badge/Backend-Firebase-FFCA28.svg?logo=firebase)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **LanChallenge** là ứng dụng di động hỗ trợ tự học và ôn tập kiến thức hoàn toàn chủ động 🧠✨. Khác với các ứng dụng học tập thông thường gò bó theo giáo trình cố định, LanChallenge trao trọn 100% quyền tự chủ thư viện dữ liệu cho người dùng. Bất kỳ nội dung nào biểu diễn dưới dạng dữ liệu **`Key - Meaning - Description`** (Từ khóa - Ý nghĩa - Mô tả/Ví dụ) đều có thể đưa vào app để luyện tập!

---

## 🌟 Triết lý & Điểm nổi bật (Core Features)

* 🎯 **Tự chủ dữ liệu 100% (User-Driven Content)**: Không bị giới hạn bởi các bộ từ vựng hay giáo trình có sẵn. Bạn muốn học gì, tạo thư viện đó!
* 🌐 **Không chỉ là Tiếng Anh**: Hệ thống mở linh hoạt hỗ trợ mọi loại hình kiến thức:
  * 🇬🇧 / 🇯🇵 / 🇰🇷 **Ngoại ngữ**: Từ vựng, ngữ pháp, thuật ngữ (Tiếng Anh, Trung, Nhật, Hàn,...).
  * 💻 **Lập trình**: Hàm, Cú pháp, Pattern (`Key`: Tên hàm, `Meaning`: Chức năng, `Description`: Ví dụ code).
  * 📖 **Lịch sử / Địa lý / Y học / Luật**: Niên đại, Thuật ngữ chuyên ngành, Định nghĩa khái niệm.
* 📁 **Import dữ liệu siêu tốc bằng File TXT**: Hỗ trợ nạp dữ liệu hàng loạt từ file `.txt` đơn giản, không cần gõ tay từng dòng.
* 🎮 **Hệ thống Game & Mini-quiz tương tác**:
  * **Trắc nghiệm 4 đáp án**: Luyện phản xạ ghi nhớ nhanh.
  * **Nhập nghĩa (Type the answer)**: Rèn luyện khả năng nhớ sâu và chính xác.
* 🔐 **Tài khoản & Lưu trữ đám mây**: Tích hợp Firebase giúp bảo mật thông tin, đồng bộ và lưu trữ bộ dữ liệu cá nhân mọi lúc mọi nơi.

---

## 🛠 Công nghệ sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện |
| :--- | :--- |
| **Hệ điều hành** | Android |
| **Ngôn ngữ** | Kotlin / XML |
| **Giao diện (UI)** | Material Design, XML Layouts |
| **Backend & Database** | Firebase Authentication, Firebase Realtime Database / Firestore |
| **File Processing** | Custom Parsing cho định dạng file `.txt` |

---

## 🗂 Cấu trúc dữ liệu linh hoạt (`Key - Meaning - Description`)

Mọi mục dữ liệu trong LanChallenge được chuẩn hóa theo cấu trúc 3 phần linh hoạt:

```json
{
  "key": "Từ khóa / Cú pháp / Khái niệm",
  "meaning": "Ý nghĩa / Định nghĩa chính",
  "description": "Mô tả chi tiết / Ví dụ minh họa / Ghi chú bổ sung"
}
