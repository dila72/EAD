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
      customerName: apt.customerName || 'Customer', // Include customer name
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

  // Get all projects assigned to the logged-in employee
  getMyProjects: async (): Promise<any[]> => {
    const data = await api.get<any[]>('/employee/projects');
    // Transform backend ProjectDTO to frontend format
    return data.map((proj: any) => ({
      id: proj.projectId,
      customerId: proj.customerId,
      taskName: proj.name,
      description: proj.description,
      startDate: proj.startDate,
      completedDate: proj.endDate,
      status: proj.status,
      assignedEmployee: proj.employee ? `${proj.employee.firstName} ${proj.employee.lastName}` : undefined,
      progressPercentage: proj.progressPercentage ?? 0,  // Use backend value or default to 0
      type: 'project' // Add type to differentiate from appointments
    }));
  },

  // Update appointment status (uses progress update endpoint)
  updateAppointmentStatus: async (appointmentId: string, status: string): Promise<any> => {
    return await api.post(`/employee/progress/${appointmentId}/status`, null, {
      params: { status }
    });
  },

  // Get progress updates for an appointment
  getProgressUpdates: async (appointmentId: string): Promise<any[]> => {
    return await api.get(`/employee/progress/appointment/${appointmentId}`);
  },

  // Create a progress update for an appointment
  createProgressUpdate: async (appointmentId: string, data: ProgressUpdateData): Promise<any> => {
    return await api.put(`/employee/progress/${appointmentId}`, {
      stage: data.stage,
      percentage: data.percentage,
      remarks: data.remarks || ''
    });
  },

  // Update an existing progress update
  updateProgressUpdate: async (progressId: string, data: ProgressUpdateData): Promise<any> => {
    return await api.put(`/employee/progress/${progressId}`, {
      stage: data.stage,
      percentage: data.percentage,
      remarks: data.remarks || ''
    });
  },

  // Delete a progress update
  deleteProgressUpdate: async (progressId: string): Promise<void> => {
    await api.delete(`/employee/progress/${progressId}`);
  }
};

