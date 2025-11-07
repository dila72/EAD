/**
 * Dashboard API Service
 * Computes dashboard statistics from appointments and projects
 */

import { appointmentService } from './appointmentService';
import { projectService } from './projectService';
import { vehicleApi } from '../vehicleApi';
import { DashboardStats } from '@/types/dashboard.types';

export const dashboardService = {
  /**
   * Get dashboard statistics for a customer
   * Fetches appointments, projects, and vehicles, then computes stats
   */
  getDashboardStats: async (customerId?: string): Promise<DashboardStats> => {
    try {
      // Fetch all data in parallel
      const [appointments, projects, vehicles] = await Promise.all([
        appointmentService.getAllAppointments(),
        projectService.getAllProjects(),
        vehicleApi.getAllVehicles()
      ]);

      // Filter by customer if provided (though APIs should already return only user's data)
      const customerAppointments = customerId 
        ? appointments.filter(a => a.customerId === customerId)
        : appointments;
      
      const customerProjects = customerId
        ? projects.filter(p => p.customerId === customerId)
        : projects;

      // Vehicle API already returns only customer's vehicles
      const totalVehicles = vehicles.length;

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
        p => {
          const statusLower = p.status.toLowerCase();
          return statusLower === 'ongoing' || 
                 statusLower === 'in progress' ||
                 statusLower === 'in_progress' ||
                 statusLower === 'pending';
        }
      ).length;

      const completedProjects = customerProjects.filter(
        p => p.status.toLowerCase() === 'completed'
      ).length;

      return {
        totalVehicles,
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
    try {
      const appointments = await appointmentService.getAllAppointments();
      const filtered = customerId 
        ? appointments.filter(a => a.customerId === customerId)
        : appointments;
      
      return filtered
        .filter(a => {
          const statusLower = a.status.toLowerCase();
          return statusLower === 'upcoming' || 
                 statusLower === 'pending' ||
                 statusLower === 'approved';
        })
        .slice(0, limit);
    } catch (error) {
      console.error('Failed to fetch upcoming appointments:', error);
      return [];
    }
  },

  /**
   * Get ongoing projects for dashboard display
   */
  getOngoingProjects: async (customerId?: string, limit: number = 4) => {
    try {
      const projects = await projectService.getAllProjects();
      console.log('All projects fetched:', projects); // Debug log
      
      const filtered = customerId
        ? projects.filter(p => p.customerId === customerId)
        : projects;
      
      console.log('Filtered projects:', filtered); // Debug log
      
      const ongoing = filtered.filter(p => {
        const statusLower = p.status.toLowerCase();
        console.log('Project:', p.taskName, 'status:', p.status, 'lowercase:', statusLower); // Debug log
        return statusLower === 'ongoing' || 
               statusLower === 'in progress' ||
               statusLower === 'in_progress' ||
               statusLower === 'pending';
      });
      
      console.log('Ongoing projects:', ongoing); // Debug log
      return ongoing.slice(0, limit);
    } catch (error) {
      console.error('Failed to fetch ongoing projects:', error);
      return [];
    }
  }
};
