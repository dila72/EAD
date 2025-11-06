/**
 * API Module for Employee Operations
 */

import api from './apiClient';

export interface Appointment {
  id: string | number;
  customerId?: string;
  vehicleId?: string;
  vehicleNumber?: string;
  serviceName: string;
  date?: string;
  time?: string;
  status?: string;
  assignedEmployee?: string;
  approvedBy?: string;
  notes?: string;
  location?: string;
  customerName?: string;
  type?: string;
  progressPercentage?: number;
  actualHours?: number;
  estimatedHours?: number;
  timerRunning?: boolean;
}

/**
 * Get appointments assigned to employee
 */
export async function getEmployeeAppointments(employeeId?: string): Promise<Appointment[]> {
  try {
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
      type: 'Appointment',
      progressPercentage: 0,
      actualHours: 0,
      estimatedHours: 2,
      timerRunning: false,
      notes: '',
      location: 'Workshop',
      customerName: apt.customerName || 'Customer'
    }));
  } catch (error) {
    console.error('Error fetching appointments:', error);
    throw error;
  }
}

/**
 * Start timer for an appointment
 */
export async function startTimer(appointmentId: string | number): Promise<void> {
  try {
    // This would be an endpoint to start tracking time
    console.log('Timer started for appointment:', appointmentId);
    // await api.post(`/employee/appointments/${appointmentId}/start-timer`);
  } catch (error) {
    console.error('Error starting timer:', error);
    throw error;
  }
}

/**
 * Pause timer for an appointment
 */
export async function pauseTimer(appointmentId: string | number): Promise<void> {
  try {
    // This would be an endpoint to pause tracking time
    console.log('Timer paused for appointment:', appointmentId);
    // await api.post(`/employee/appointments/${appointmentId}/pause-timer`);
  } catch (error) {
    console.error('Error pausing timer:', error);
    throw error;
  }
}

/**
 * Log time for an appointment
 */
export async function logTime(appointmentId: string | number, timeData: any): Promise<void> {
  try {
    // This would be an endpoint to log time
    console.log('Time logged for appointment:', appointmentId, timeData);
    // await api.post(`/employee/appointments/${appointmentId}/log-time`, timeData);
  } catch (error) {
    console.error('Error logging time:', error);
    throw error;
  }
}

/**
 * Update appointment progress
 */
export async function updateProgress(appointmentId: string | number, progressData: any): Promise<void> {
  try {
    console.log('Updating progress for appointment:', appointmentId, 'with data:', progressData);
    const requestData = {
      stage: progressData.stage || 'in progress',
      percentage: parseInt(progressData.percentage?.toString() || '0'),
      remarks: progressData.remarks || ''
    };
    console.log('Sending request:', requestData);
    await api.put(`/employee/progress/${appointmentId}`, requestData);
  } catch (error) {
    console.error('Error updating progress:', error);
    throw error;
  }
}
