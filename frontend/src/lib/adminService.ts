import api from './apiClient';
import { CreateEmployeeRequest } from '@/types/auth.types';

export const adminService = {
  // Employee endpoints
  createEmployee: async (data: CreateEmployeeRequest) => {
    return await api.post('/admin/employees', data);
  },
  
  getAllEmployees: async () => {
    return await api.get('/admin/employees');
  },
  
  getEmployeeById: async (id: string) => {
    return await api.get(`/admin/employees/${id}`);
  },
  
  updateEmployee: async (id: string, data: any) => {
    return await api.put(`/admin/employees/${id}`, data);
  },
  
  deleteEmployee: async (id: string) => {
    return await api.delete(`/admin/employees/${id}`);
  },

  // Appointments endpoint
  getAllAppointments: async () => {
    return await api.get('/admin/appointments');
  },

  // Assign employee to appointment
  assignEmployeeToAppointment: async (appointmentId: string, employeeId: number) => {
    return await api.put(`/appointments/${appointmentId}/assign-employee`, {
      employeeId
    });
  },

  // Customers endpoint
  getAllCustomers: async () => {
    return await api.get('/admin/customers');
  },

  // Projects endpoint
  getAllProjects: async () => {
    return await api.get('/admin/projects');
  },

  // Assign employee to project
  assignEmployeeToProject: async (projectId: string, employeeId: number) => {
    return await api.put(`/projects/${projectId}/assign-employee`, {
      employeeId
    });
  },
};
