/**
 * Dashboard API Service
 * Computes dashboard statistics from appointments and projects
 */

import { appointmentService } from './appointmentService';
import { projectService } from './projectService';
import { DashboardStats } from '@/types/dashboard.types';

export const dashboardService = {
  /**
   * Get dashboard statistics for a customer
   * Fetches appointments and projects, then computes stats
   */
  getDashboardStats: async (customerId?: string): Promise<DashboardStats> => {
    try {
      // Fetch all appointments and projects in parallel
      const [appointments, projects] = await Promise.all([
        appointmentService.getAllAppointments(),
        projectService.getAllProjects()
      ]);

      // Filter by customer if provided
      const customerAppointments = customerId 
        ? appointments.filter(a => a.customerId === customerId)
        : appointments;
      
      const customerProjects = customerId
        ? projects.filter(p => p.customerId === customerId)
        : projects;

      // Calculate stats
      const upcomingAppointments = customerAppointments.filter(
        a => a.status.toLowerCase() === 'upcoming' || 
             a.status.toLowerCase() === 'pending' ||
             a.status.toLowerCase() === 'approved'
      ).length;

      const completedAppointments = customerAppointments.filter(
        a => a.status.toLowerCase() === 'completed'
      ).length;

      const ongoingProjects = customerProjects.filter(
        p => p.status.toLowerCase() === 'ongoing' || 
             p.status.toLowerCase() === 'in progress'
      ).length;

      const completedProjects = customerProjects.filter(
        p => p.status.toLowerCase() === 'completed'
      ).length;

      return {
        totalVehicles: 0, // Vehicle count should come from vehicle API
        upcomingAppointments,
        ongoingProjects,
        completedAppointments,
        completedProjects,
        totalAppointments: customerAppointments.length,
        totalProjects: customerProjects.length
      };
    } catch (error) {
      console.error('Failed to fetch dashboard stats:', error);
      // Return empty stats on error
      return {
        totalVehicles: 0,
        upcomingAppointments: 0,
        ongoingProjects: 0,
        completedAppointments: 0,
        completedProjects: 0,
        totalAppointments: 0,
        totalProjects: 0
      };
    }
  },

  /**
   * Get upcoming appointments for dashboard display
   */
  getUpcomingAppointments: async (customerId?: string, limit: number = 4) => {
    const appointments = await appointmentService.getAllAppointments();
    const filtered = customerId 
      ? appointments.filter(a => a.customerId === customerId)
      : appointments;
    
    return filtered
      .filter(a => 
        a.status.toLowerCase() === 'upcoming' || 
        a.status.toLowerCase() === 'pending' ||
        a.status.toLowerCase() === 'approved'
      )
      .slice(0, limit);
  },

  /**
   * Get ongoing projects for dashboard display
   */
  getOngoingProjects: async (customerId?: string, limit: number = 4) => {
    const projects = await projectService.getAllProjects();
    const filtered = customerId
      ? projects.filter(p => p.customerId === customerId)
      : projects;
    
    return filtered
      .filter(p => 
        p.status.toLowerCase() === 'ongoing' || 
        p.status.toLowerCase() === 'in progress'
      )
      .slice(0, limit);
  }
};
