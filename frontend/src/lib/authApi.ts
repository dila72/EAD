import { LoginRequest, SignupRequest, AuthResponse } from '@/types/auth.types';
import { axiosInstance } from './apiClient';

export async function loginUser(credentials: LoginRequest): Promise<AuthResponse> {
  try {
    const response = await axiosInstance.post('/auth/login', credentials);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data || error.message || 'Login failed');
  }
}

export async function signupUser(data: SignupRequest): Promise<AuthResponse> {
  try {
    const response = await axiosInstance.post('/auth/signup', data);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data || error.message || 'Signup failed');
  }
}

export async function logoutUser(token: string): Promise<void> {
  try {
    await axiosInstance.post('/auth/logout', null, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
  } catch (error) {
    console.error('Logout failed on server:', error);
  }
}
