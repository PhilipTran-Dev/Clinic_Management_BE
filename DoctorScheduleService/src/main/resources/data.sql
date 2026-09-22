-- ====================================================================
-- 1. DANH MỤC CHUYÊN KHOA (DEPARTMENTS)
-- ====================================================================
INSERT INTO departments (id, code, name) VALUES
                                             (1, 'INT-CARD', 'Khoa Nội Tổng quát & Tim mạch'),
                                             (2, 'RESP-ALLERGY', 'Khoa Hô hấp & Dị ứng - Miễn dịch lâm sàng'),
                                             (3, 'DERM', 'Khoa Da liễu')
    ON CONFLICT (id) DO NOTHING;

-- Đồng bộ sequence cho bảng departments
SELECT setval('departments_id_seq', (SELECT MAX(id) FROM departments));

-- ====================================================================
-- 2. TÀI KHOẢN NGƯỜI DÙNG (USERS)
-- Mật khẩu mặc định: 123456 (demo text)
-- ====================================================================
INSERT INTO users (id, email, password, full_name, phone, role, active) VALUES
-- Admin
(1, 'admin@smartclinic.vn', '123456', 'Quản trị viên Hệ thống', '0901000001', 'ADMIN', true),

-- 9 Bác sĩ (3 BS / Khoa)
(2, 'tuan.tran@smartclinic.vn', '123456', 'PGS. TS. BS. Trần Minh Tuấn', '0902000001', 'DOCTOR', true),
(3, 'dung.nguyen@smartclinic.vn', '123456', 'BS. CKI. Nguyễn Văn Dũng', '0902000002', 'DOCTOR', true),
(4, 'bao.pham@smartclinic.vn', '123456', 'ThS. BS. Phạm Quốc Bảo', '0902000003', 'DOCTOR', true),
(5, 'yen.le@smartclinic.vn', '123456', 'BS. CKI. Lê Thị Hoàng Yến', '0902000004', 'DOCTOR', true),
(6, 'hoa.pham@smartclinic.vn', '123456', 'BS. CKII. Phạm Thị Hoa', '0902000005', 'DOCTOR', true),
(7, 'nam.hoang@smartclinic.vn', '123456', 'ThS. BS. Hoàng Hoài Nam', '0902000006', 'DOCTOR', true),
(8, 'lan.nguyen@smartclinic.vn', '123456', 'BS. CKI. Nguyễn Thị Lan', '0902000007', 'DOCTOR', true),
(9, 'duc.vu@smartclinic.vn', '123456', 'ThS. BS. Vũ Minh Đức', '0902000008', 'DOCTOR', true),
(10, 'huong.mai@smartclinic.vn', '123456', 'BS. Mai Thu Hương', '0902000009', 'DOCTOR', true),

-- 9 Điều dưỡng
(11, 'hang.trinh@smartclinic.vn', '123456', 'ĐD. Trịnh Thu Hằng', '0903000001', 'NURSE', true),
(12, 'mai.do@smartclinic.vn', '123456', 'ĐD. Đỗ Phương Mai', '0903000002', 'NURSE', true),
(13, 'ly.nguyen@smartclinic.vn', '123456', 'ĐD. Nguyễn Thảo Ly', '0903000003', 'NURSE', true),
(14, 'oanh.tran@smartclinic.vn', '123456', 'ĐD. Trần Kim Oanh', '0903000004', 'NURSE', true),
(15, 'thao.le@smartclinic.vn', '123456', 'ĐD. Lê Phương Thảo', '0903000005', 'NURSE', true),
(16, 'nhi.pham@smartclinic.vn', '123456', 'ĐD. Phạm Yến Nhi', '0903000006', 'NURSE', true),
(17, 'linh.vu@smartclinic.vn', '123456', 'ĐD. Vũ Thùy Linh', '0903000007', 'NURSE', true),
(18, 'ngoc.bui@smartclinic.vn', '123456', 'ĐD. Bùi Ánh Ngọc', '0903000008', 'NURSE', true),
(19, 'tram.hoang@smartclinic.vn', '123456', 'ĐD. Hoàng Bích Trâm', '0903000009', 'NURSE', true),

-- 6 Dược sĩ (2 ĐH, 4 CĐ)
(20, 'thao.dang@smartclinic.vn', '123456', 'DS. Đặng Thu Thảo', '0904000001', 'PHARMACIST', true),
(21, 'kien.nguyen@smartclinic.vn', '123456', 'DS. Nguyễn Trung Kiên', '0904000002', 'PHARMACIST', true),
(22, 'ha.tran@smartclinic.vn', '123456', 'DSCĐ. Trần Thanh Hà', '0904000003', 'PHARMACIST', true),
(23, 'long.le@smartclinic.vn', '123456', 'DSCĐ. Lê Hoàng Long', '0904000004', 'PHARMACIST', true),
(24, 'phuc.do@smartclinic.vn', '123456', 'DSCĐ. Đỗ Hồng Phúc', '0904000005', 'PHARMACIST', true),
(25, 'nhung.vu@smartclinic.vn', '123456', 'DSCĐ. Vũ Cẩm Nhung', '0904000006', 'PHARMACIST', true)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- ====================================================================
-- 3. DANH SÁCH BÁC SĨ (DOCTORS)
-- ====================================================================
INSERT INTO doctors (id, full_name, title, room_number, department_id, user_id, active) VALUES
-- Khoa 1: Nội Tổng quát & Tim mạch (Department ID = 1)
(1, 'PGS. TS. BS. Trần Minh Tuấn', 'PGS.TS', 'Phòng 101', 1, 2, true),
(2, 'BS. CKI. Nguyễn Văn Dũng', 'BS.CKI', 'Phòng 102', 1, 3, true),
(3, 'ThS. BS. Phạm Quốc Bảo', 'ThS.BS', 'Phòng 101', 1, 4, true),

-- Khoa 2: Hô hấp - Dị ứng (Department ID = 2)
(4, 'BS. CKI. Lê Thị Hoàng Yến', 'BS.CKI', 'Phòng 201', 2, 5, true),
(5, 'BS. CKII. Phạm Thị Hoa', 'BS.CKII', 'Phòng 202', 2, 6, true),
(6, 'ThS. BS. Hoàng Hoài Nam', 'ThS.BS', 'Phòng 201', 2, 7, true),

-- Khoa 3: Da liễu (Department ID = 3)
(7, 'BS. CKI. Nguyễn Thị Lan', 'BS.CKI', 'Phòng 205', 3, 8, true),
(8, 'ThS. BS. Vũ Minh Đức', 'ThS.BS', 'Phòng 206', 3, 9, true),
(9, 'BS. Mai Thu Hương', 'BS', 'Phòng 205', 3, 10, true)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('doctors_id_seq', (SELECT MAX(id) FROM doctors));

-- ====================================================================
-- 4. DANH SÁCH ĐIỀU DƯỠNG (NURSES)
-- ====================================================================
INSERT INTO nurses (id, full_name, qualification, assigned_station, department_id, user_id, active) VALUES
-- Điều dưỡng hỗ trợ phòng khám (3 người)
(1, 'ĐD. Trịnh Thu Hằng', 'Cử nhân Điều dưỡng', 'Phòng khám 101 (Nội)', 1, 11, true),
(2, 'ĐD. Đỗ Phương Mai', 'Cử nhân Điều dưỡng', 'Phòng khám 201 (Dị ứng)', 2, 12, true),
(3, 'ĐD. Nguyễn Thảo Ly', 'Cử nhân Điều dưỡng', 'Phòng khám 205 (Da liễu)', 3, 13, true),

-- Điều dưỡng buồng thủ thuật / can thiệp (3 người)
(4, 'ĐD. Trần Kim Oanh', 'Cử nhân Điều dưỡng', 'Phòng Cấp cứu & Điện tim', 1, 14, true),
(5, 'ĐD. Lê Phương Thảo', 'Cử nhân Điều dưỡng', 'Buồng Test dị nguyên & Hô hấp ký', 2, 15, true),
(6, 'ĐD. Phạm Yến Nhi', 'Cao đẳng Điều dưỡng', 'Buồng Tiểu phẫu & Laser da', 3, 16, true),

-- Điều dưỡng tiếp đón / Kiosk phân luồng (3 người)
(7, 'ĐD. Vũ Thùy Linh', 'Cử nhân Điều dưỡng', 'Quầy Tiếp đón Sảnh A', 1, 17, true),
(8, 'ĐD. Bùi Ánh Ngọc', 'Cao đẳng Điều dưỡng', 'Khu Kiosk Tự phục vụ', 2, 18, true),
(9, 'ĐD. Hoàng Bích Trâm', 'Cao đẳng Điều dưỡng', 'Bàn Đo Dấu hiệu Sinh tồn', 3, 19, true)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('nurses_id_seq', (SELECT MAX(id) FROM nurses));

-- ====================================================================
-- 5. DANH SÁCH DƯỢC SĨ (PHARMACISTS)
-- ====================================================================
INSERT INTO pharmacists (id, full_name, license_number, default_counter, user_id, active) VALUES
-- 2 Dược sĩ Đại học (Phụ trách duyệt đơn & dược lâm sàng)
(1, 'DS. Đặng Thu Thảo', 'CCHN-00128/HCM', 'Quầy Dược Lâm Sàng #1', 20, true),
(2, 'DS. Nguyễn Trung Kiên', 'CCHN-00341/HCM', 'Quầy Dược Lâm Sàng #2', 21, true),

-- 4 Dược sĩ Cao đẳng (Phụ trách cấp phát & đóng gói)
(3, 'DSCĐ. Trần Thanh Hà', 'CCHN-01052/HCM', 'Cửa phát thuốc BHYT #1', 22, true),
(4, 'DSCĐ. Lê Hoàng Long', 'CCHN-01053/HCM', 'Cửa phát thuốc BHYT #2', 23, true),
(5, 'DSCĐ. Đỗ Hồng Phúc', 'CCHN-01054/HCM', 'Cửa phát thuốc Thu phí #1', 24, true),
(6, 'DSCĐ. Vũ Cẩm Nhung', 'CCHN-01055/HCM', 'Cửa phát thuốc Thu phí #2', 25, true)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('pharmacists_id_seq', (SELECT MAX(id) FROM pharmacists));

-- ====================================================================
-- 6. PHÂN CA TRỰC BÁC SĨ (DOCTOR SHIFTS)
-- Thiết lập lịch cho ngày hiện tại (CURRENT_DATE) và ngày mai (CURRENT_DATE + 1)
-- Ràng buộc: Mỗi buổi luôn có 1 BS OUTPATIENT + 1 BS INPATIENT
-- ====================================================================
INSERT INTO doctor_shifts (doctor_id, department_id, shift_date, session, duty_type, max_patients_per_slot) VALUES
-- Khoa 1: Nội Tổng quát & Tim mạch (Hôm nay)
(1, 1, CURRENT_DATE, 'MORNING', 'OUTPATIENT', 4),
(2, 1, CURRENT_DATE, 'MORNING', 'INPATIENT', 4),
(2, 1, CURRENT_DATE, 'AFTERNOON', 'INPATIENT', 4),
(3, 1, CURRENT_DATE, 'AFTERNOON', 'OUTPATIENT', 4),

-- Khoa 2: Hô hấp & Dị ứng (Hôm nay)
(4, 2, CURRENT_DATE, 'MORNING', 'OUTPATIENT', 4),
(5, 2, CURRENT_DATE, 'MORNING', 'INPATIENT', 4),
(5, 2, CURRENT_DATE, 'AFTERNOON', 'INPATIENT', 4),
(6, 2, CURRENT_DATE, 'AFTERNOON', 'OUTPATIENT', 4),

-- Khoa 3: Da liễu (Hôm nay)
(7, 3, CURRENT_DATE, 'MORNING', 'OUTPATIENT', 4),
(8, 3, CURRENT_DATE, 'MORNING', 'INPATIENT', 4),
(8, 3, CURRENT_DATE, 'AFTERNOON', 'INPATIENT', 4),
(9, 3, CURRENT_DATE, 'AFTERNOON', 'OUTPATIENT', 4),

-- Khoa 1: Nội Tổng quát & Tim mạch (Ngày mai)
(1, 1, CURRENT_DATE + 1, 'MORNING', 'OUTPATIENT', 4),
(2, 1, CURRENT_DATE + 1, 'MORNING', 'INPATIENT', 4),
(2, 1, CURRENT_DATE + 1, 'AFTERNOON', 'INPATIENT', 4),
(3, 1, CURRENT_DATE + 1, 'AFTERNOON', 'OUTPATIENT', 4),

-- Khoa 2: Hô hấp & Dị ứng (Ngày mai)
(4, 2, CURRENT_DATE + 1, 'MORNING', 'OUTPATIENT', 4),
(5, 2, CURRENT_DATE + 1, 'MORNING', 'INPATIENT', 4),
(5, 2, CURRENT_DATE + 1, 'AFTERNOON', 'INPATIENT', 4),
(6, 2, CURRENT_DATE + 1, 'AFTERNOON', 'OUTPATIENT', 4),

-- Khoa 3: Da liễu (Ngày mai)
(7, 3, CURRENT_DATE + 1, 'MORNING', 'OUTPATIENT', 4),
(8, 3, CURRENT_DATE + 1, 'MORNING', 'INPATIENT', 4),
(8, 3, CURRENT_DATE + 1, 'AFTERNOON', 'INPATIENT', 4),
(9, 3, CURRENT_DATE + 1, 'AFTERNOON', 'OUTPATIENT', 4)
    ON CONFLICT DO NOTHING;

-- ====================================================================
-- 7. LƯỢT ĐẶT MẪU ĐỂ TEST CÂN BẰNG TẢI (SLOT ASSIGNMENTS)
-- Giả lập ngày mai Khoa 2 slot 07:30 đã có 2 bệnh nhân
-- ====================================================================
INSERT INTO slot_assignments (
    ticket_number,
    doctor_id,
    department_id,
    appointment_date,
    slot_start_time,
    status,
    patient_name,
    patient_phone,
    insurance_code,
    chief_complaint
) VALUES
      ('#A-101', 4, 2, CURRENT_DATE + 1, '07:30:00', 'BOOKED', 'Nguyễn Văn An', '0912345678', 'DN 4 79 79 12345678', 'Sốt cao, đau rát họng 2 ngày nay'),
      ('#A-102', 4, 2, CURRENT_DATE + 1, '07:30:00', 'BOOKED', 'Trần Thị Mai', '0912345679', 'DN 4 79 79 87654321', 'Khó thở cấp, rít thanh quản')
    ON CONFLICT (ticket_number) DO NOTHING;