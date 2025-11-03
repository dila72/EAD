'use client';

import { useEffect, useState } from 'react';
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { dashboardService, customerService, appointmentService, projectService } from '@/api/mockApiService';
import type { Customer, Appointment, Project, DashboardStats } from '@/types';

export default function Dashboard() {
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [upcomingAppointments, setUpcomingAppointments] = useState<Appointment[]>([]);
  const [ongoingProjects, setOngoingProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const customerData = await customerService.getProfile();
      setCustomer(customerData);

      const [statsData, appointmentsData, projectsData] = await Promise.all([
        dashboardService.getDashboardStats(customerData.id),
        appointmentService.getUpcomingAppointments(customerData.id),
        projectService.getOngoingProjects(customerData.id)
      ]);

      setStats(statsData);
      setUpcomingAppointments(appointmentsData);
      setOngoingProjects(projectsData);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      weekday: 'long', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <Header title="Dashboard" />

      <div className="pt-16 md:pl-56 px-4 md:ml-10 mt-4 md:mr-5 mb-10">
        <p className="text-gray-700 text-base">Hello,</p>
        <h2 className="text-lg font-bold mb-6">Hi {customer?.name}</h2>

        {/* Stats */}
        <div className="flex md:grid md:grid-cols-3 gap-4 mb-8 overflow-x-auto scrollbar-hide py-2 -my-2">
          <div className="bg-white rounded-lg shadow-md p-4 text-center min-w-[200px] flex-shrink-0">
            <p className="text-3xl font-bold text-red-500">{stats?.totalVehicles || 0}</p>
            <p className="text-base text-gray-600 mt-1">Vehicles</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4 text-center min-w-[200px] flex-shrink-0">
            <p className="text-3xl font-bold text-yellow-500">{stats?.upcomingAppointments || 0}</p>
            <p className="text-base text-gray-600 mt-1">Upcoming Appointments</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4 text-center min-w-[200px] flex-shrink-0">
            <p className="text-3xl font-bold text-green-500">{stats?.ongoingProjects || 0}</p>
            <p className="text-base text-gray-600 mt-1">Ongoing Projects</p>
          </div>
        </div>

        {/* Upcoming Appointments */}
        <h3 className="text-lg font-bold mb-3">Upcoming Appointments</h3>
        {upcomingAppointments.length === 0 ? (
          <div className="bg-white shadow-md rounded-lg p-4 mb-4 text-center text-gray-600 text-base">
            No upcoming appointments
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
            {upcomingAppointments.map((appointment) => (
              <div key={appointment.id} className="bg-white text-gray-600 shadow-md rounded-lg p-4">
                <p className="text-lg font-semibold">{appointment.serviceName}</p>
                <p className="text-base mt-1">{appointment.vehicleNumber}</p>
                <div className="flex justify-between text-base mt-3">
                  <span>{formatDate(appointment.date)}</span>
                  <span>{appointment.time}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Ongoing Projects */}
        <h3 className="text-lg font-bold mb-3">Ongoing Projects</h3>
        {ongoingProjects.length === 0 ? (
          <div className="bg-white shadow-md rounded-lg p-4 text-center text-gray-600 text-base">
            No ongoing projects
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {ongoingProjects.map((project) => (
              <div key={project.id} className="bg-white text-gray-600 shadow-md rounded-lg p-4">
                <p className="text-lg font-semibold">{project.taskName}</p>
                <p className="text-base mt-1">{project.vehicleNumber}</p>
                <p className="text-base mt-2">Starting Date: {formatDate(project.startDate)}</p>
                {project.estimatedCost && (
                  <p className="text-base mt-1">Estimated Cost: ${project.estimatedCost}</p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
