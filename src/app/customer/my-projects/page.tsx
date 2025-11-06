'use client';

import { useEffect, useState } from 'react';
import { projectService, type Project } from '@/lib/api/projectService';

export default function MyProjects() {
  const [ongoingProjects, setOngoingProjects] = useState<Project[]>([]);
  const [completedProjects, setCompletedProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAllCompleted, setShowAllCompleted] = useState(false);
  const [showNewForm, setShowNewForm] = useState(false);
  const [newName, setNewName] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [newVehicleModel, setNewVehicleModel] = useState('');
  const [newStartDate, setNewStartDate] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch all projects and filter locally
      const allProjects = await projectService.getAllProjects();
      
      const ongoing = allProjects.filter(p => 
        p.status.toLowerCase() === 'ongoing' || 
        p.status.toLowerCase() === 'in progress' ||
        p.status.toLowerCase() === 'pending'
      );
      
      const completed = allProjects.filter(p => 
        p.status.toLowerCase() === 'completed'
      );

      setOngoingProjects(ongoing);
      setCompletedProjects(completed);
    } catch (error) {
      console.error('Failed to load projects:', error);
      setError('Failed to load projects. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  // helper to derive vehicle model from description if stored as 'Vehicle Model: <value>'
  const getVehicleModel = (p: Project) => {
    if (!p.description) return p.vehicleNumber || '';
    const m = p.description.match(/Vehicle Model:\s*(.+)/i);
    return m ? m[1].trim() : p.vehicleNumber || '';
  };

  const handleCreateProject = async () => {
    if (!newName || !newDescription || !newVehicleModel || !newStartDate) {
      alert('Please fill in all fields');
      return;
    }
    try {
      setSubmitting(true);
      const combinedDescription = `${newDescription}\n\nVehicle Model: ${newVehicleModel}`;
      await projectService.createProject({
        taskName: newName,
        description: combinedDescription,
        startDate: newStartDate,
        status: 'Pending',
      });
      // reset and refresh
      setNewName('');
      setNewDescription('');
      setNewVehicleModel('');
      setNewStartDate('');
      setShowNewForm(false);
      await loadProjects();
      alert('Project created successfully');
    } catch (err) {
      console.error('Failed to create project', err);
      alert('Failed to create project. Please try again.');
    } finally {
      setSubmitting(false);
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

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={loadProjects}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-2">My Projects</h1>
        <p className="text-gray-600">View and manage your custom service projects</p>
      </div>

          {/* Stats */}
          <div className="flex md:grid md:grid-cols-2 gap-6 mb-8 overflow-x-auto scrollbar-hide py-2 -my-2">
            <div className="bg-white rounded-lg shadow-md p-4 md:p-5 text-center min-w-[160px] md:min-w-0 flex-shrink-0">
              <p className="text-2xl md:text-3xl font-bold text-green-500">{completedProjects.length}</p>
              <p className="text-sm md:text-base text-gray-600 mt-1">Completed</p>
            </div>
            <div className="bg-white rounded-lg shadow-md p-4 md:p-5 text-center min-w-[160px] md:min-w-0 flex-shrink-0">
              <p className="text-2xl md:text-3xl font-bold text-yellow-500">{ongoingProjects.length}</p>
              <p className="text-sm md:text-base text-gray-600 mt-1">Ongoing</p>
            </div>
          </div>

          {/* Ongoing Projects */}
          <div className="flex justify-between items-center mb-3">
            <h3 className="text-lg md:text-xl font-bold">Ongoing Projects</h3>
            <button
              onClick={() => setShowNewForm(v => !v)}
              className="bg-orange-400 hover:bg-orange-500 text-white px-3 md:px-4 py-2 rounded-lg text-sm md:text-base font-medium transition-colors"
            >
              {showNewForm ? 'Close' : '+ New'}
            </button>
          </div>

          {showNewForm && (
            <div className="bg-white rounded-lg shadow-md p-4 md:p-5 mb-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex flex-col">
                  <label className="text-sm text-gray-600 mb-1">Project Name</label>
                  <input
                    type="text"
                    className="border rounded px-3 py-2"
                    value={newName}
                    onChange={e => setNewName(e.target.value)}
                    placeholder="Enter project name"
                  />
                </div>
                <div className="flex flex-col">
                  <label className="text-sm text-gray-600 mb-1">Vehicle Model</label>
                  <input
                    type="text"
                    className="border rounded px-3 py-2"
                    value={newVehicleModel}
                    onChange={e => setNewVehicleModel(e.target.value)}
                    placeholder="e.g., Toyota Camry 2020"
                  />
                </div>
                <div className="flex flex-col md:col-span-2">
                  <label className="text-sm text-gray-600 mb-1">Project Description</label>
                  <textarea
                    className="border rounded px-3 py-2 min-h-[120px]"
                    value={newDescription}
                    onChange={e => setNewDescription(e.target.value)}
                    placeholder="Describe what kind of project you want"
                  />
                </div>
                <div className="flex flex-col">
                  <label className="text-sm text-gray-600 mb-1">Start Date</label>
                  <input
                    type="date"
                    className="border rounded px-3 py-2"
                    value={newStartDate}
                    onChange={e => setNewStartDate(e.target.value)}
                  />
                </div>
              </div>
              <div className="mt-4 flex gap-3">
                <button
                  onClick={handleCreateProject}
                  disabled={submitting}
                  className="bg-green-600 hover:bg-green-700 disabled:opacity-60 text-white px-4 py-2 rounded"
                >
                  {submitting ? 'Creating...' : 'Create Project'}
                </button>
                <button
                  onClick={() => setShowNewForm(false)}
                  className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}

          {ongoingProjects.length === 0 ? (
            <div className="bg-gray-100 rounded-lg p-3 md:p-4 mb-6 text-center text-gray-500 text-sm md:text-base">
              No ongoing projects
            </div>
          ) : (
            <div className="overflow-x-auto bg-white rounded-lg shadow-md mb-8">
            <table className="w-full text-left text-sm md:text-base">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-4 font-semibold">ID</th>
                  <th className="p-4 font-semibold">Task</th>
                  <th className="p-4 font-semibold">Vehicle Model</th>
                  <th className="p-4 font-semibold">Date</th>
                  <th className="p-4 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody>
                {ongoingProjects.map((project) => (
                  <tr key={project.id} className="border-t hover:bg-gray-50 transition-colors">
                    <td className="p-3 md:p-4">{project.id}</td>
                    <td className="p-3 md:p-4">{project.taskName}</td>
                    <td className="p-3 md:p-4">{getVehicleModel(project)}</td>
                    <td className="p-3 md:p-4">{formatDate(project.startDate)}</td>
                    <td className="p-3 md:p-4 text-orange-500 font-semibold">{project.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

          {/* Completed Projects */}
          <h3 className="text-lg font-bold mb-3 lg:text-xl ">Completed Projects</h3>
          {completedProjects.length === 0 ? (
            <div className="bg-gray-100 rounded-lg p-4 text-center text-gray-500 text-base">
              No completed projects yet
            </div>
          ) : (
            <>
              <div className="overflow-x-auto bg-white rounded-lg shadow-md mb-8">
                <table className="w-full text-left text-base">
                  <thead className="bg-gray-100 text-gray-700">
                    <tr>
                      <th className="p-4 font-semibold">ID</th>
                      <th className="p-4 font-semibold">Task</th>
                      <th className="p-4 font-semibold">Vehicle Model</th>
                      <th className="p-4 font-semibold">Date</th>
                      <th className="p-4 font-semibold">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(showAllCompleted ? completedProjects : completedProjects.slice(0, 5)).map((project) => (
                      <tr key={project.id} className="border-t hover:bg-gray-50 transition-colors">
                        <td className="p-4">{project.id}</td>
                        <td className="p-4">{project.taskName}</td>
                        <td className="p-4">{getVehicleModel(project)}</td>
                        <td className="p-4">{formatDate(project.startDate)}</td>
                        <td className="p-4 text-green-600 font-semibold">{project.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              {completedProjects.length > 5 && (
                <button
                  onClick={() => setShowAllCompleted(!showAllCompleted)}
                  className="mt-3 text-blue-600 hover:text-blue-800 text-sm md:text-base font-medium transition-colors"
                >
                  {showAllCompleted ? 'Show less...' : `Show more... (${completedProjects.length - 5} more)`}
                </button>
              )}
            </>
          )}
    </div>
  );
}
