-- SQL Script to Update Status Values to New 5-Status System
-- Run this script on your PostgreSQL database: auto-mobile
-- IMPORTANT: Run this before using the application with new status enums

-- First, check current status values
SELECT 'APPOINTMENTS - Current Status Values:' as info;
SELECT status, COUNT(*) as count FROM appointments GROUP BY status ORDER BY status;

SELECT 'PROJECTS - Current Status Values:' as info;
SELECT status, COUNT(*) as count FROM projects GROUP BY status ORDER BY status;

-- Update Appointment Status Values
-- Map old values to new REQUESTING status
UPDATE appointments 
SET status = 'REQUESTING' 
WHERE status IN ('UPCOMING', 'PENDING', 'APPROVED');

-- Map old values to new ASSIGNED status (if any exist)
UPDATE appointments 
SET status = 'ASSIGNED' 
WHERE status = 'ASSIGNED';

-- Ensure IN_PROGRESS uses underscore (not space or hyphen)
UPDATE appointments 
SET status = 'IN_PROGRESS' 
WHERE status IN ('IN_PROGRESS', 'IN PROGRESS', 'INPROGRESS');

-- Update Project Status Values
-- Map old PLANNED/PENDING to REQUESTING
UPDATE projects 
SET status = 'REQUESTING' 
WHERE status IN ('PLANNED', 'PENDING');

-- Map old ONGOING to ASSIGNED
UPDATE projects 
SET status = 'ASSIGNED' 
WHERE status IN ('ONGOING', 'ASSIGNED');

-- Map old ON_HOLD to IN_PROGRESS (or you can map to REQUESTING if you prefer)
UPDATE projects 
SET status = 'IN_PROGRESS' 
WHERE status IN ('ON_HOLD', 'IN_PROGRESS', 'IN PROGRESS');

-- Verify the changes
SELECT 'APPOINTMENTS - After Update:' as info;
SELECT status, COUNT(*) as count FROM appointments GROUP BY status ORDER BY status;

SELECT 'PROJECTS - After Update:' as info;
SELECT status, COUNT(*) as count FROM projects GROUP BY status ORDER BY status;

-- Expected status values after update:
-- REQUESTING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
