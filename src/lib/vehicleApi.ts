import { axiosInstance } from './apiClient';
import {
  Vehicle,
  CreateVehicleRequest,
  UpdateVehicleRequest,
} from '@/types/vehicle.types';

const VEHICLE_API_BASE = '/vehicles/customer/my-vehicles';

/**
 * Vehicle API Service
 * Connects to backend vehicle endpoints
 */
export const vehicleApi = {
  /**
   * Get all vehicles for the authenticated customer
   */
  getAllVehicles: async (): Promise<Vehicle[]> => {
    const response = await axiosInstance.get<Vehicle[]>(VEHICLE_API_BASE);
    return response.data;
  },

  /**
   * Get a specific vehicle by ID
   */
  getVehicleById: async (vehicleId: number): Promise<Vehicle> => {
    const response = await axiosInstance.get<Vehicle>(`${VEHICLE_API_BASE}/${vehicleId}`);
    return response.data;
  },

  /**
   * Create a new vehicle without an image
   */
  createVehicle: async (vehicleData: CreateVehicleRequest): Promise<Vehicle> => {
    const response = await axiosInstance.post<Vehicle>(VEHICLE_API_BASE, vehicleData);
    return response.data;
  },

  /**
   * Create a new vehicle with an image
   */
  createVehicleWithImage: async (
    vehicleData: CreateVehicleRequest,
    imageFile: File
  ): Promise<Vehicle> => {
    const formData = new FormData();
    
    // Add vehicle data as JSON string (must match backend @RequestPart name)
    formData.append('vehicle', JSON.stringify(vehicleData));
    
    // Add image file (must match backend @RequestPart name)
    formData.append('image', imageFile);

    // No need to specify headers - the interceptor handles it
    const response = await axiosInstance.post<Vehicle>(
      `${VEHICLE_API_BASE}/with-image`,
      formData
    );
    return response.data;
  },

  /**
   * Update an existing vehicle without changing the image
   */
  updateVehicle: async (
    vehicleId: number,
    vehicleData: UpdateVehicleRequest
  ): Promise<Vehicle> => {
    const response = await axiosInstance.put<Vehicle>(
      `${VEHICLE_API_BASE}/${vehicleId}`,
      vehicleData
    );
    return response.data;
  },

  /**
   * Update an existing vehicle with a new image
   */
  updateVehicleWithImage: async (
    vehicleId: number,
    vehicleData: UpdateVehicleRequest,
    imageFile: File
  ): Promise<Vehicle> => {
    const formData = new FormData();
    
    // Add vehicle data as JSON string (must match backend @RequestPart name)
    formData.append('vehicle', JSON.stringify(vehicleData));
    
    // Add image file (must match backend @RequestPart name)
    formData.append('image', imageFile);

    // No need to specify headers - the interceptor handles it
    const response = await axiosInstance.put<Vehicle>(
      `${VEHICLE_API_BASE}/${vehicleId}/with-image`,
      formData
    );
    return response.data;
  },

  /**
   * Delete a vehicle
   */
  deleteVehicle: async (vehicleId: number): Promise<string> => {
    const response = await axiosInstance.delete<string>(`${VEHICLE_API_BASE}/${vehicleId}`);
    return response.data;
  },
};

export default vehicleApi;
