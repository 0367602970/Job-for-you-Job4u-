# Website Tìm Kiếm Việc Làm – Backend (Spring Boot)

Backend cho hệ thống website tìm kiếm việc làm, được xây dựng bằng Java Spring Boot.
Hệ thống cung cấp các RESTful API phục vụ cho người tìm việc và nhà tuyển dụng, bao gồm đăng tin tuyển dụng, tìm kiếm việc làm, quản lý người dùng và nộp hồ sơ ứng tuyển.


# Công nghệ sử dụng
Java 17
Spring Boot
Spring Web (REST API)
Spring Data JPA / Hibernate
Spring Security + JWT
MySQL
Maven
Lombok
Swagger / OpenAPI

# Chức năng chính
Xác thực & phân quyền
  Đăng ký / đăng nhập người dùng
  Xác thực bằng JWT
  Phân quyền theo vai trò:
    - ADMIN
    - NHÀ TUYỂN DỤNG
    - NGƯỜI TÌM VIỆC
    
Quản lý việc làm
  Nhà tuyển dụng:
    - Đăng tin tuyển dụng
    - Cập nhật / xoá tin tuyển dụng
    - Cập nhật thông tin công ty
    - Xem thông tin và cập nhật trạng thái CV.
  Người tìm việc:
    - Xem danh sách việc làm
    - Tìm kiếm việc làm theo kỹ năng, địa điểm
    - Xem chi tiết việc làm
    - Ứng tuyển việc làm
    - Đăng ký kỹ năng để nhận việc làm phù hợp khi có việc làm mới
  Người quản trị (admin):
    - Quản lý công ty
    - Quản lý người dùng
    - Quản lý hồ sơ CV
    - Quản lý việc làm, kỹ năng
    - Quản lý vai trò, quyền hạn của từng vai trò

    
# Kiến trúc dự án
Dự án được xây dựng theo mô hình Layered Architecture:
  Controller: Xử lý request/response từ client
  Service: Xử lý nghiệp vụ
  Repository: Tương tác với database (JPA)
  Entity / DTO: Mapping dữ liệu
  Security: Xác thực & phân quyền JWT

# Hướng dẫn chạy dự án
Yêu cầu môi trường:
  Java 17 trở lên
  Maven
  MySQL

Cài đặt & chạy:
  1. Clone project:
      git clone https://github.com/0367602970/Job-for-you-Job4u-.git
  2. Tạo DB và cấu hình trong application.properties
  3. Chạy ứng dụng:
      mvn spring-boot:run


Tác giả
Họ tên: Nguyễn Đức Toàn
Vị trí: Java Backend Developer
