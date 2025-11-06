/**
 * Service Management API
 * Handles all service-related API calls to the backend
 */

import api from '../apiClient';
import { Service, CreateServiceRequest, UpdateServiceRequest } from '@/types/service.types';

const SERVICE_ENDPOINTS = {
  ADMIN: {
    BASE: '/admin/services',
    WITH_IMAGE: '/admin/services/with-image',
    BY_ID: (id: number) => `/admin/services/${id}`,
    WITH_IMAGE_BY_ID: (id: number) => `/admin/services/${id}/with-image`,
    TOGGLE_STATUS: (id: number) => `/admin/services/${id}/toggle-status`,
  },
  CUSTOMER: {
    BASE: '/customer/services',
    BY_ID: (id: number) => `/customer/services/${id}`,
  },
};

// ==================== ADMIN OPERATIONS ====================

/**
 * Create a new service without image
 */
export const createService = async (serviceData: CreateServiceRequest): Promise<Service> => {
  return api.post<Service>(SERVICE_ENDPOINTS.ADMIN.BASE, serviceData);
};

/**
 * Create a new service with image
 */
export const createServiceWithImage = async (
  serviceData: CreateServiceRequest,
  imageFile: File
): Promise<Service> => {
  const formData = new FormData();
  
  // Add service data as JSON string
  const serviceBlob = new Blob([JSON.stringify(serviceData)], { 
    type: 'application/json' 
  });
  formData.append('service', serviceBlob);
  
  // Add image file
  formData.append('image', imageFile);

  return api.post<Service>(SERVICE_ENDPOINTS.ADMIN.WITH_IMAGE, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

/**
 * Get all services (including inactive) - Admin only
 */
export const getAllServices = async (): Promise<Service[]> => {
  return api.get<Service[]>(SERVICE_ENDPOINTS.ADMIN.BASE);
};

/**
 * Get service by ID - Admin view
 */
export const getServiceById = async (id: number): Promise<Service> => {
  return api.get<Service>(SERVICE_ENDPOINTS.ADMIN.BY_ID(id));
};

/**
 * Update service without changing image
 */
export const updateService = async (
  id: number,
  serviceData: UpdateServiceRequest
): Promise<Service> => {
  return api.put<Service>(SERVICE_ENDPOINTS.ADMIN.BY_ID(id), serviceData);
};

/**
 * Update service with new image
 */
export const updateServiceWithImage = async (
  id: number,
  serviceData: UpdateServiceRequest,
  imageFile: File
): Promise<Service> => {
  const formData = new FormData();
  
  // Add service data as JSON string
  const serviceBlob = new Blob([JSON.stringify(serviceData)], { 
    type: 'application/json' 
  });
  formData.append('service', serviceBlob);
  
  // Add image file
  formData.append('image', imageFile);

  return api.put<Service>(SERVICE_ENDPOINTS.ADMIN.WITH_IMAGE_BY_ID(id), formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

/**
 * Toggle service active/inactive status
 */
export const toggleServiceStatus = async (id: number): Promise<Service> => {
  return api.patch<Service>(SERVICE_ENDPOINTS.ADMIN.TOGGLE_STATUS(id));
};

/**
 * Delete a service
 */
export const deleteService = async (id: number): Promise<string> => {
  return api.delete<string>(SERVICE_ENDPOINTS.ADMIN.BY_ID(id));
};

// ==================== CUSTOMER OPERATIONS ====================

/**
 * Get all active services for customer selection
 */
export const getActiveServices = async (): Promise<Service[]> => {
  return api.get<Service[]>(SERVICE_ENDPOINTS.CUSTOMER.BASE);
};

/**
 * Get service details by ID - Customer view (only active services)
 */
export const getActiveServiceById = async (id: number): Promise<Service> => {
  return api.get<Service>(SERVICE_ENDPOINTS.CUSTOMER.BY_ID(id));
};

// ==================== HELPER FUNCTIONS ====================

/**
 * Format duration from minutes to readable string
 */
export const formatDuration = (minutes: number): string => {
  if (minutes < 60) {
    return `${minutes} minutes`;
  }
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;
  if (remainingMinutes === 0) {
    return `${hours} ${hours === 1 ? 'hour' : 'hours'}`;
  }
  return `${hours} ${hours === 1 ? 'hour' : 'hours'} ${remainingMinutes} minutes`;
};

/**
 * Parse duration string to minutes
 */
export const parseDurationToMinutes = (duration: string): number => {
  const hourMatch = duration.match(/(\d+)\s*(?:hour|hr)/i);
  const minuteMatch = duration.match(/(\d+)\s*(?:minute|min)/i);
  
  let totalMinutes = 0;
  if (hourMatch) {
    totalMinutes += parseInt(hourMatch[1]) * 60;
  }
  if (minuteMatch) {
    totalMinutes += parseInt(minuteMatch[1]);
  }
  
  return totalMinutes || 0;
};
