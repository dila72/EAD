-- Mock Data for Services
INSERT INTO services (id, name, description, price, estimated_duration_minutes, active, created_at, updated_at) 
VALUES 
(1, 'Oil Change', 'Complete engine oil change with filter replacement', 49.99, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Brake Inspection', 'Comprehensive brake system inspection and testing', 79.99, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tire Rotation', 'Professional tire rotation and balance', 39.99, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Engine Diagnostic', 'Complete engine diagnostic scan and analysis', 99.99, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'AC Service', 'Air conditioning system service and recharge', 129.99, 90, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Battery Replacement', 'Battery testing and replacement service', 149.99, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Transmission Service', 'Transmission fluid change and inspection', 199.99, 120, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Wheel Alignment', 'Four-wheel computerized alignment', 89.99, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Mock Data for Service Slots
INSERT INTO service_slot (id, date, time, available) 
VALUES 
-- Today's slots
(1, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '09:00 AM', true),
(2, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '10:00 AM', true),
(3, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '11:00 AM', true),
(4, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '01:00 PM', false),
(5, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '02:00 PM', true),
(6, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '03:00 PM', true),
(7, TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD'), '04:00 PM', true),

-- Tomorrow's slots
(8, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '09:00 AM', true),
(9, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '10:00 AM', true),
(10, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '11:00 AM', true),
(11, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '12:00 PM', true),
(12, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '01:00 PM', true),
(13, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '02:00 PM', true),
(14, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '03:00 PM', true),
(15, TO_CHAR(CURRENT_DATE + INTERVAL '1 day', 'YYYY-MM-DD'), '04:00 PM', true),

-- Day after tomorrow's slots
(16, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '09:00 AM', true),
(17, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '10:00 AM', true),
(18, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '11:00 AM', true),
(19, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '12:00 PM', true),
(20, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '01:00 PM', true),
(21, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '02:00 PM', true),
(22, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '03:00 PM', true),
(23, TO_CHAR(CURRENT_DATE + INTERVAL '2 days', 'YYYY-MM-DD'), '04:00 PM', true)
ON CONFLICT (id) DO NOTHING;

-- Update sequence for services
SELECT setval('services_id_seq', (SELECT MAX(id) FROM services));

-- Update sequence for service_slot
SELECT setval('service_slot_id_seq', (SELECT MAX(id) FROM service_slot));
