// API configuration
import { axiosInstance } from '@/lib/apiClient';

const handleResponse = async (response: any) => {
  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || 'Failed to fetch data');
  }
  return response.data;
};

export interface Appointment {
  id?: string;
  customerId?: string;
  vehicleNumber: string;
  serviceName: string;
  date: string;
  time: string;
  status: string;
}

export const appointmentService = {
  // Get all appointments
  getAllAppointments: async (): Promise<Appointment[]> => {
    const { data } = await axiosInstance.get('/appointments');
  // map backend DTO to frontend Appointment shape
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return (data || []).map((d: any) => ({
      id: d.appointmentId || d.id,
      customerId: d.customerId,
      vehicleNumber: d.vehicleNo || d.vehicleNumber,
      serviceName: d.service || d.serviceName,
      date: d.date,
      time: d.startTime || d.time,
      status: d.status
    }));
  },

  // Get appointments for a specific customer
  getCustomerAppointments: async (customerId: string): Promise<Appointment[]> => {
    const { data } = await axiosInstance.get(`/appointments/customer/${customerId}`);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return (data || []).map((d: any) => ({
      id: d.appointmentId || d.id,
      customerId: d.customerId,
      vehicleNumber: d.vehicleNo || d.vehicleNumber,
      serviceName: d.service || d.serviceName,
      date: d.date,
      time: d.startTime || d.time,
      status: d.status
    }));
  },

  // Create a new appointment
  createAppointment: async (appointmentData: Omit<Appointment, 'id'>): Promise<Appointment> => {
    const { data: d } = await axiosInstance.post('/appointments', appointmentData);
    return {
      id: d.appointmentId || d.id,
      customerId: d.customerId,
      vehicleNumber: d.vehicleNo || d.vehicleNumber,
      serviceName: d.service || d.serviceName,
      date: d.date,
      time: d.startTime || d.time,
      status: d.status
    };
  },

  // Cancel an appointment
  cancelAppointment: async (appointmentId: string): Promise<Appointment> => {
    // Use the generic update endpoint to change status to CANCELLED
    const payload = { status: 'CANCELLED' };
    const { data: d } = await axiosInstance.put(`/appointments/${appointmentId}`, payload);
    // map to frontend shape
    return {
      id: d.appointmentId || d.id,
      customerId: d.customerId,
      vehicleNumber: d.vehicleNo || d.vehicleNumber,
      serviceName: d.service || d.serviceName,
      date: d.date,
      time: d.startTime || d.time,
      status: d.status,
    };
  },

  // Delete an appointment
  deleteAppointment: async (appointmentId: string): Promise<void> => {
    if (!appointmentId) {
      throw new Error('Invalid appointment id');
    }
    await axiosInstance.delete(`/appointments/${encodeURIComponent(appointmentId)}`);
  }
};