import { axiosInstance } from './apiClient';
import { CreateEmployeeRequest } from '@/types/auth.types';

export const adminService = {
  // Employee endpoints
  createEmployee: async (data: CreateEmployeeRequest) => {
    const response = await axiosInstance.post('/admin/employees', data);
    return response;
  },
  
  getAllEmployees: async () => {
    const response = await axiosInstance.get('/admin/employees');
    return response;
  },
  
  getEmployeeById: async (id: string) => {
    const response = await axiosInstance.get(`/admin/employees/${id}`);
    return response;
  },
  
  updateEmployee: async (id: string, data: any) => {
    const response = await axiosInstance.put(`/admin/employees/${id}`, data);
    return response;
  },
  
  deleteEmployee: async (id: string) => {
    const response = await axiosInstance.delete(`/admin/employees/${id}`);
    return response;
  },

  // Appointments endpoint
  getAllAppointments: async () => {
    const response = await axiosInstance.get('/admin/appointments');
    return response;
  },

  // Customers endpoint
  getAllCustomers: async () => {
    const response = await axiosInstance.get('/admin/customers');
    return response;
  },

  // Projects endpoint
  getAllProjects: async () => {
    const response = await axiosInstance.get('/admin/projects');
    return response;
  },
};
