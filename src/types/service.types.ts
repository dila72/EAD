/**
 * Service Type Definitions
 * Matches backend ServiceDTO structure
 */

export interface Service {
  id: number;
  name: string;
  description: string;
  imageUrl: string | null;
  price: number;
  estimatedDurationMinutes: number;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ServiceFormData {
  name: string;
  description: string;
  price: string;
  estimatedDurationMinutes: string;
  active: boolean;
}

export interface CreateServiceRequest {
  name: string;
  description: string;
  price: number;
  estimatedDurationMinutes: number;
  active: boolean;
}

export interface UpdateServiceRequest {
  name: string;
  description: string;
  price: number;
  estimatedDurationMinutes: number;
  active: boolean;
}
