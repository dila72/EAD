import api from './apiClient';
import { Appointment } from '@/types';

export interface ProgressUpdateData {
  stage: string;
  percentage: number;
  remarks?: string;
}

export const employeeService = {
  // Get all appointments assigned to the logged-in employee
  getMyAppointments: async (): Promise<Appointment[]> => {
    const data = await api.get<any[]>('/employee/appointments');
    // Transform backend AppointmentDTO to frontend Appointment
    return data.map((apt: any) => ({
      id: apt.appointmentId,
      customerId: apt.customerId,
      vehicleId: apt.vehicleId,
      vehicleNumber: apt.vehicleNo,
      serviceName: apt.service,
      date: apt.date,
      time: apt.startTime,
      status: apt.status,
      assignedEmployee: apt.employee ? `${apt.employee.firstName} ${apt.employee.lastName}` : undefined,
    }));
  },

  // Get appointments by status for the logged-in employee
  getAppointmentsByStatus: async (status: string): Promise<Appointment[]> => {
    return await api.get<Appointment[]>(`/employee/appointments/${status}`);
  },

  // Update appointment status (uses existing appointment update endpoint)
  updateAppointmentStatus: async (appointmentId: string, status: string): Promise<Appointment> => {
    return await api.put<Appointment>(`/appointments/${appointmentId}`, { status });
  },

  // Get progress updates for an appointment
  getProgressUpdates: async (appointmentId: string): Promise<any[]> => {
    return await api.get(`/employee/progress/appointment/${appointmentId}`);
  },

  // Create a progress update for an appointment
  createProgressUpdate: async (appointmentId: string, data: ProgressUpdateData): Promise<any> => {
    return await api.post('/employee/progress', {
      appointmentId,
      ...data
    });
  },

  // Update an existing progress update
  updateProgressUpdate: async (progressId: string, data: ProgressUpdateData): Promise<any> => {
    return await api.put(`/employee/progress/${progressId}`, data);
  },

  // Delete a progress update
  deleteProgressUpdate: async (progressId: string): Promise<void> => {
    await api.delete(`/employee/progress/${progressId}`);
  }
};

