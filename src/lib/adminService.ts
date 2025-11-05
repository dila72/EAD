import api from './apiClient';
import { CreateEmployeeRequest } from '@/types/auth.types';

export const adminService = {
  createEmployee: async (data: CreateEmployeeRequest) => {
    return api.post('/admin/employees', data);
  },
  
  getAllEmployees: async () => {
    return api.get('/admin/employees');
  },
  
  getEmployeeById: async (id: string) => {
    return api.get(`/admin/employees/${id}`);
  },
  
  updateEmployee: async (id: string, data: any) => {
    return api.put(`/admin/employees/${id}`, data);
  },
  
  deleteEmployee: async (id: string) => {
    return api.delete(`/admin/employees/${id}`);
  },
};
