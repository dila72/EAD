'use client';

import { useEffect, useState } from 'react';
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { projectService, customerService } from '@/api/mockApiService';
import type { Project, Customer } from '@/types';

export default function Projects() {
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [ongoingProjects, setOngoingProjects] = useState<Project[]>([]);
  const [completedProjects, setCompletedProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      setLoading(true);
      const customerData = await customerService.getProfile();
      setCustomer(customerData);

      const [ongoing, completed] = await Promise.all([
        projectService.getOngoingProjects(customerData.id),
        projectService.getCompletedProjects(customerData.id)
      ]);

      setOngoingProjects(ongoing);
      setCompletedProjects(completed);
    } catch (error) {
      console.error('Failed to load projects:', error);
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
          <p className="text-gray-600">Loading projects...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <Header title="My Projects" />

      <div className="pt-16 md:pl-56 px-4 md:ml-10 mt-4 md:mr-5 mb-10">
        <p className="text-gray-700 text-base">Hello,</p>
        <h2 className="text-lg font-bold mb-6">Hi {customer?.name}</h2>

        {/* Stats */}
        <div className="flex md:grid md:grid-cols-2 gap-4 mb-8 overflow-x-auto scrollbar-hide py-2 -my-2">
          <div className="bg-white rounded-lg shadow-md p-4 text-center min-w-[200px] flex-shrink-0">
            <p className="text-3xl font-bold text-green-500">{completedProjects.length}</p>
            <p className="text-base text-gray-600 mt-1">Completed</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4 text-center min-w-[200px] flex-shrink-0">
            <p className="text-3xl font-bold text-yellow-500">{ongoingProjects.length}</p>
            <p className="text-base text-gray-600 mt-1">Ongoing</p>
          </div>
        </div>

        {/* Ongoing Projects */}
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-lg font-bold">Ongoing Projects</h3>
          <button className="bg-orange-400 hover:bg-orange-500 text-white px-4 py-2 rounded-lg text-base font-medium transition-colors">+ New</button>
        </div>

        {ongoingProjects.length === 0 ? (
          <div className="bg-gray-100 rounded-lg p-4 mb-8 text-center text-gray-500 text-base">
            No ongoing projects
          </div>
        ) : (
          <div className="overflow-x-auto bg-white rounded-lg shadow-md mb-8">
            <table className="w-full text-left text-base">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-4 font-semibold">ID</th>
                  <th className="p-4 font-semibold">Task</th>
                  <th className="p-4 font-semibold">Vehicle No</th>
                  <th className="p-4 font-semibold">Date</th>
                  <th className="p-4 font-semibold">Time</th>
                  <th className="p-4 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody>
                {ongoingProjects.map((project) => (
                  <tr key={project.id} className="border-t hover:bg-gray-50 transition-colors">
                    <td className="p-4">{project.id}</td>
                    <td className="p-4">{project.taskName}</td>
                    <td className="p-4">{project.vehicleNumber}</td>
                    <td className="p-4">{formatDate(project.startDate)}</td>
                    <td className="p-4">{project.time}</td>
                    <td className="p-4 text-orange-500 font-semibold">{project.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Completed Projects */}
        <h3 className="text-lg font-bold mb-3">Completed Projects</h3>
        {completedProjects.length === 0 ? (
          <div className="bg-gray-100 rounded-lg p-4 text-center text-gray-500 text-base">
            No completed projects yet
          </div>
        ) : (
          <div className="overflow-x-auto bg-white rounded-lg shadow-md">
            <table className="w-full text-left text-base">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-4 font-semibold">ID</th>
                  <th className="p-4 font-semibold">Task</th>
                  <th className="p-4 font-semibold">Vehicle No</th>
                  <th className="p-4 font-semibold">Date</th>
                  <th className="p-4 font-semibold">Time</th>
                  <th className="p-4 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody>
                {completedProjects.map((project) => (
                  <tr key={project.id} className="border-t hover:bg-gray-50 transition-colors">
                    <td className="p-4">{project.id}</td>
                    <td className="p-4">{project.taskName}</td>
                    <td className="p-4">{project.vehicleNumber}</td>
                    <td className="p-4">{formatDate(project.startDate)}</td>
                    <td className="p-4">{project.time}</td>
                    <td className="p-4 text-green-600 font-semibold">{project.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
