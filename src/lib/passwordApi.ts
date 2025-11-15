import { axiosInstance } from './apiClient';

export interface ForgotPasswordRequest {
  email: string;
}

export interface VerifyOtpRequest {
  email: string;
  otp: string;
}

export interface ResetPasswordRequest {
  email: string;
  otp: string;
  newPassword: string;
}

export interface ForgotPasswordResponse {
  message: string;
  email: string;
}

export interface VerifyOtpResponse {
  message: string;
}

export interface ResetPasswordResponse {
  message: string;
}

export async function sendOtp(email: string): Promise<ForgotPasswordResponse> {
  try {
    const response = await axiosInstance.post('/password/forgot', { email });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.error || error.message || 'Failed to send OTP');
  }
}

export async function verifyOtp(email: string, otp: string): Promise<VerifyOtpResponse> {
  try {
    const response = await axiosInstance.post('/password/verify-otp', { email, otp });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.error || error.message || 'OTP verification failed');
  }
}

export async function resetPassword(email: string, otp: string, newPassword: string): Promise<ResetPasswordResponse> {
  try {
    const response = await axiosInstance.post('/password/reset', { email, otp, newPassword });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.error || error.message || 'Password reset failed');
  }
}
