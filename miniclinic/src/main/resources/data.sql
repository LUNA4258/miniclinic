-- 初始醫師資料（5 位）
INSERT OR IGNORE INTO doctor (doctor_id, name, department, specialty) VALUES
    ('D001', '陳志明醫師', '家醫科', '一般內科、慢性病管理'),
    ('D002', '林佩君醫師', '內科',   '心臟血管、高血壓'),
    ('D003', '王建華醫師', '復健科', '運動傷害、脊椎復健'),
    ('D004', '李美玲醫師', '小兒科', '兒童感冒、疫苗接種'),
    ('D005', '張雅筑醫師', '身心科', '焦慮、失眠、情緒調適');

-- 初始病患資料（3 位虛構病患）
INSERT OR IGNORE INTO patient (chart_no, name, gender, birth_date, phone) VALUES
    ('TEST00001', '測試病患甲', '男', '1985-03-15', '0912-345-678'),
    ('TEST00002', '王小明',     '男', '1990-07-22', '0923-456-789'),
    ('TEST00003', '李小華',     '女', '1988-11-30', '0934-567-890');

-- 初始掛號資料（appt_id 必須明確指定）
INSERT OR IGNORE INTO appointment (appt_id, chart_no, doctor_id, appt_date, time_slot, status) VALUES
    (1, 'TEST00001', 'D001', '2026-05-01', 'AM', 'BOOKED'),
    (2, 'TEST00002', 'D002', '2026-05-01', 'AM', 'BOOKED'),
    (3, 'TEST00003', 'D003', '2026-05-02', 'PM', 'BOOKED');