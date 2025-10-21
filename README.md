# AnimeSimpleAlarm

Ứng dụng Android đơn giản để đặt báo thức với giao diện/motif anime.

## Tính năng
- Đặt/xóa báo thức đơn giản.
- Thông báo hiển thị khi báo thức kích hoạt.
- Hỗ trợ nhiều báo thức (cơ bản).
- Cấu hình âm thanh thông báo và lặp lại (nếu có).

## Yêu cầu
- Android Studio (Arctic Fox hoặc mới hơn khuyến nghị).
- JDK 11+.
- Android SDK với API level tương ứng (xem build.gradle của dự án).

## Cài đặt & chạy
1. Clone repository:
   git clone https://github.com/nguyentuann/AnimeSimpleAlarm.git
2. Mở Android Studio → Open an existing project → chọn thư mục `AnimeSimpleAlarm`.
3. Chờ Gradle sync hoàn thành.
4. Kết nối thiết bị thật hoặc sử dụng Android Emulator.
5. Run ứng dụng từ Android Studio.

## Cấu hình quan trọng
- Kiểm tra `AndroidManifest.xml` để đảm bảo quyền liên quan (nếu cần) đã được khai báo.
- Notification Channel được tạo tại thời điểm khởi động hoặc khi cần để hiển thị thông báo.

## Cấu trúc dự án 
├── 📁 components
├── 📁 data
│   ├── 📁 entity
│   └── 📁 model
├── 📁 di
├── 📁 helpers
├── 📁 local
│   ├── 📁 dao
│   └── 📁 db
├── 📁 repository
├── 📁 session
├── 📁 ui
│   ├── 📁 alarm
│   ├── 📁 home
│   ├── 📁 quickalarm
│   ├── 📁 settings
│   ├── 📁 splash
│   ├── 📁 stopwatch
│   └── 📁 timer
├── 📁 utils
├── 📁 viewModel
├── ☕ MainActivity.kt
└── ☕ MainApplication.kt

