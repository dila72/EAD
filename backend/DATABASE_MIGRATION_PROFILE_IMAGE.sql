-- Migration Script: Add Profile Image Support to Customers Table
-- Date: 2025-11-07
-- Description: Adds profileImageUrl and profileImagePublicId columns to support Cloudinary image uploads

-- Add profile image URL column (stores Cloudinary HTTPS URL)
ALTER TABLE customers 
ADD COLUMN profile_image_url VARCHAR(500) NULL;

-- Add profile image public ID column (stores Cloudinary public ID for deletion)
ALTER TABLE customers 
ADD COLUMN profile_image_public_id VARCHAR(255) NULL;

-- Add comment for documentation
COMMENT ON COLUMN customers.profile_image_url IS 'Cloudinary HTTPS URL for customer profile image';
COMMENT ON COLUMN customers.profile_image_public_id IS 'Cloudinary public ID for image deletion';

-- Verify the changes
SELECT 
    column_name, 
    data_type, 
    character_maximum_length, 
    is_nullable
FROM 
    information_schema.columns
WHERE 
    table_name = 'customers' 
    AND column_name IN ('profile_image_url', 'profile_image_public_id');
