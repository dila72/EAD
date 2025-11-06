/**
 * Project API Service
 * Handles all project-related API calls
 */

import { axiosInstance } from '@/lib/apiClient';
import { ProjectDTO, ProjectStatus } from '@/types/project.type';

export interface Project {
  id: string;
  customerId?: string;
  taskName: string;
  description: string;
  startDate: string;
  endDate?: string;
  status: string;
  vehicleNumber?: string;
  vehicleType?: string;
  assignedEmployee?: string;
  approvedBy?: string;
  estimatedCost?: number;
  notes?: string;
}

/**
 * Map backend ProjectDTO to frontend Project interface
 */
const mapProjectDtoToProject = (dto: ProjectDTO): Project => {
  // Map backend status enum to user-friendly status
  const statusMap: Record<string, string> = {
    [ProjectStatus.PLANNED]: 'Pending',
    [ProjectStatus.IN_PROGRESS]: 'Ongoing',
    [ProjectStatus.COMPLETED]: 'Completed',
    [ProjectStatus.CANCELLED]: 'Cancelled',
    [ProjectStatus.ON_HOLD]: 'On Hold'
  };

  const backendStatus = typeof dto.status === 'string' ? dto.status.toUpperCase() : dto.status;
  const friendlyStatus = statusMap[backendStatus] || 'Pending';

  return {
    id: dto.projectId || '',
    customerId: dto.customerId,
    taskName: dto.name,
    description: dto.description,
    startDate: dto.startDate,
    endDate: dto.endDate,
    status: friendlyStatus,
  };
};

/**
 * Map frontend Project to backend ProjectDTO
 */
const mapProjectToDto = (project: Partial<Project>): Partial<ProjectDTO> => {
  // Map friendly status back to backend enum
  const statusMap: Record<string, ProjectStatus> = {
    'Pending': ProjectStatus.PLANNED,
    'Ongoing': ProjectStatus.IN_PROGRESS,
    'Completed': ProjectStatus.COMPLETED,
    'Cancelled': ProjectStatus.CANCELLED,
    'On Hold': ProjectStatus.ON_HOLD
  };

  const backendStatus = project.status ? statusMap[project.status] : ProjectStatus.PLANNED;

  return {
    projectId: project.id,
    name: project.taskName || '',
    description: project.description || '',
    customerId: project.customerId,
    startDate: project.startDate || '',
    endDate: project.endDate,
    status: backendStatus
  };
};

export const projectService = {
  /**
   * Get all projects (optionally filtered by customer)
   */
  getAllProjects: async (): Promise<Project[]> => {
    const { data } = await axiosInstance.get<ProjectDTO[]>('/projects');
    return (data || []).map(mapProjectDtoToProject);
  },

  /**
   * Get projects by customer ID
   */
  getCustomerProjects: async (customerId?: string): Promise<Project[]> => {
    const { data } = await axiosInstance.get<ProjectDTO[]>('/projects', {
      params: customerId ? { customerId } : undefined
    });
    return (data || []).map(mapProjectDtoToProject);
  },

  /**
   * Get ongoing projects for a customer
   */
  getOngoingProjects: async (customerId?: string): Promise<Project[]> => {
    const allProjects = await projectService.getCustomerProjects(customerId);
    return allProjects.filter(p => p.status === 'Ongoing');
  },

  /**
   * Get completed projects for a customer
   */
  getCompletedProjects: async (customerId?: string): Promise<Project[]> => {
    const allProjects = await projectService.getCustomerProjects(customerId);
    return allProjects.filter(p => p.status === 'Completed');
  },

  /**
   * Get a single project by ID
   */
  getProjectById: async (projectId: string): Promise<Project> => {
    const { data } = await axiosInstance.get<ProjectDTO>(`/projects/${projectId}`);
    return mapProjectDtoToProject(data);
  },

  /**
   * Create a new project
   */
  createProject: async (projectData: Partial<Project>): Promise<Project> => {
    const dto = mapProjectToDto(projectData);
    const { data } = await axiosInstance.post<ProjectDTO>('/projects', dto);
    return mapProjectDtoToProject(data);
  },

  /**
   * Update a project
   */
  updateProject: async (projectId: string, projectData: Partial<Project>): Promise<Project> => {
    const dto = mapProjectToDto(projectData);
    const { data } = await axiosInstance.put<ProjectDTO>(`/projects/${projectId}`, dto);
    return mapProjectDtoToProject(data);
  },

  /**
   * Delete a project
   */
  deleteProject: async (projectId: string): Promise<void> => {
    await axiosInstance.delete(`/projects/${projectId}`);
  }
};
