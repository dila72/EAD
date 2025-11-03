'use client';

import { useEffect, useState } from 'react';
import Sidebar from "@/components/layout/Sidebar";
import TopBar from "@/components/layout/Header";
import { appointmentService, customerService } from '@/api/mockApiService';
import type { Appointment, Customer } from '@/types';

export default function MyAppointments() {
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [upcomingAppointments, setUpcomingAppointments] = useState<Appointment[]>([]);
  const [completedAppointments, setCompletedAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      const customerData = await customerService.getProfile();
      setCustomer(customerData);

      const [upcoming, completed] = await Promise.all([
        appointmentService.getUpcomingAppointments(customerData.id),
        appointmentService.getCompletedAppointments(customerData.id)
      ]);

      setUpcomingAppointments(upcoming);
      setCompletedAppointments(completed);
    } catch (error) {
      console.error('Failed to load appointments:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading appointments...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <TopBar title="My Appointments" />

      <div className="md:ml-16 lg:ml-16 xl:ml-80
       md:mr-16 lg:mr-16 xl:mr-16 xl:mt-16">
        <div className="pt-16 px-6 md:px-8 lg:px-10 mt-4 mb-10">
          <p className="text-gray-700 text-sm md:text-base">Hello,</p>
          <h2 className="text-base md:text-lg font-bold mb-6">Hi {customer?.name}</h2>

          {/* Stats */}
          <div className="flex md:grid md:grid-cols-2 gap-6 mb-8 overflow-x-auto scrollbar-hide py-2 -my-2">
            <div className="bg-white rounded-lg shadow-md p-4 md:p-5 text-center min-w-[160px] md:min-w-0 flex-shrink-0">
              <p className="text-2xl md:text-3xl font-bold text-green-500">{completedAppointments.length}</p>
              <p className="text-sm md:text-base text-gray-600 mt-1">Completed</p>
            </div>
            <div className="bg-white rounded-lg shadow-md p-4 md:p-5 text-center min-w-[160px] md:min-w-0 flex-shrink-0">
              <p className="text-2xl md:text-3xl font-bold text-yellow-500">{upcomingAppointments.length}</p>
              <p className="text-sm md:text-base text-gray-600 mt-1">Upcoming</p>
            </div>
          </div>

          {/* Upcoming Appointments */}
          <div className="flex justify-between items-center mb-3">
            <h3 className="text-lg md:text-xl font-bold">Upcoming Appointments</h3>
            <button className="bg-orange-400 hover:bg-orange-500 text-white px-3 md:px-4 py-2 rounded-lg text-sm md:text-base font-medium transition-colors">+ New</button>
          </div>
          
          {upcomingAppointments.length === 0 ? (
            <div className="bg-gray-100 rounded-lg p-3 md:p-4 mb-8 text-center text-gray-500 text-sm md:text-base">
              No upcoming appointments
            </div>
          ) : (
            <div className="overflow-x-auto bg-white rounded-lg shadow-md mb-8">
            <table className="w-full text-left text-sm md:text-base">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-4 font-semibold">ID</th>
                  <th className="p-4 font-semibold">Service</th>
                  <th className="p-4 font-semibold">Vehicle</th>
                  <th className="p-4 font-semibold">Date</th>
                  <th className="p-4 font-semibold">Time</th>
                  <th className="p-4 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody>
                {upcomingAppointments.map((appointment) => (
                  <tr key={appointment.id} className="border-t hover:bg-gray-50 transition-colors">
                    <td className="p-3 md:p-4">{appointment.id}</td>
                    <td className="p-3 md:p-4">{appointment.serviceName}</td>
                    <td className="p-3 md:p-4">{appointment.vehicleNumber}</td>
                    <td className="p-3 md:p-4">{formatDate(appointment.date)}</td>
                    <td className="p-3 md:p-4">{appointment.time}</td>
                    <td className="p-3 md:p-4 text-orange-500 font-semibold">{appointment.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

          {/* Completed */}
          <h3 className="text-lg font-bold mb-3">Completed Appointments</h3>
          {completedAppointments.length === 0 ? (
            <div className="bg-gray-100 rounded-lg p-4 text-center text-gray-500 text-base">
              No completed appointments yet
            </div>
          ) : (
            <div className="overflow-x-auto bg-white rounded-lg shadow-md">
            <table className="w-full text-left text-base">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-4 font-semibold">ID</th>
                  <th className="p-4 font-semibold">Service</th>
                  <th className="p-4 font-semibold">Vehicle</th>
                  <th className="p-4 font-semibold">Date</th>
                  <th className="p-4 font-semibold">Time</th>
                  <th className="p-4 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody>
                {completedAppointments.map((appointment) => (
                  <tr key={appointment.id} className="border-t hover:bg-gray-50 transition-colors">
                    <td className="p-4">{appointment.id}</td>
                    <td className="p-4">{appointment.serviceName}</td>
                    <td className="p-4">{appointment.vehicleNumber}</td>
                    <td className="p-4">{formatDate(appointment.date)}</td>
                    <td className="p-4">{appointment.time}</td>
                    <td className="p-4 text-green-600 font-semibold">{appointment.status}</td>
                  </tr>
                ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
