// Vehicle type definitions matching backend API
export interface Vehicle {
  id: number;
  customerId: number;
  model: string;
  color: string;
  vin: string;
  licensePlate: string;
  year: number;
  registrationDate: string;
  imageUrl: string | null;
}

export interface CreateVehicleRequest {
  model: string;
  color: string;
  vin: string;
  licensePlate: string;
  year: number;
  registrationDate: string;
}

export interface UpdateVehicleRequest {
  model: string;
  color: string;
  vin: string;
  licensePlate: string;
  year: number;
  registrationDate: string;
}

export interface CreateVehicleWithImageRequest {
  vehicle: CreateVehicleRequest;
  image: File;
}

export interface UpdateVehicleWithImageRequest {
  vehicle: UpdateVehicleRequest;
  image: File;
}
