'use client';

import { useState } from 'react';
import Link from 'next/link';
import ProjectList from '@/components/ProjectList';
import ProjectProgressChart from '@/components/ProjectProgressChart';
import MemberList from '@/components/MemberList';

export default function DashboardPage() {
  const [projects] = useState([
    {
      id: 1,
      name: 'Website Redesign',
      description: 'Complete redesign of company website to improve user experience',
      progress: 75,
      tasks: 12,
      completed: 9,
      team: ['Alex', 'Sam', 'Jordan'],
      dueDate: '2023-06-15',
    },
    {
      id: 2,
      name: 'Mobile App Development',
      description: 'Development of new mobile application for customer engagement',
      progress: 40,
      tasks: 24,
      completed: 10,
      team: ['Taylor', 'Morgan'],
      dueDate: '2023-07-30',
    },
    {
      id: 3,
      name: 'Marketing Campaign',
      description: 'Q3 marketing campaign for product launch',
      progress: 20,
      tasks: 15,
      completed: 3,
      team: ['Casey', 'Riley', 'Avery'],
      dueDate: '2023-06-30',
    },
  ]);

  const chartData = projects.map(project => ({
    name: project.name,
    progress: project.progress,
  }));

  const [recentTasks] = useState([
    { id: 1, title: 'Create wireframes', project: 'Website Redesign', status: 'completed' },
    { id: 2, title: 'Research competitors', project: 'Website Redesign', status: 'completed' },
    { id: 3, title: 'Design homepage mockup', project: 'Website Redesign', status: 'in-progress' },
    { id: 4, title: 'Setup development environment', project: 'Mobile App Development', status: 'todo' },
    { id: 5, title: 'Create user personas', project: 'Marketing Campaign', status: 'todo' },
  ]);

  // Mock team members data for MemberList component
  const teamMembers = [
    {
      id: 1,
      name: 'Alex Johnson',
      email: 'alex.johnson@example.com',
      role: 'Designer',
      status: 'online' as const,
    },
    {
      id: 2,
      name: 'Sam Smith',
      email: 'sam.smith@example.com',
      role: 'Developer',
      status: 'offline' as const,
    },
    {
      id: 3,
      name: 'Jordan Williams',
      email: 'jordan.williams@example.com',
      role: 'Project Manager',
      status: 'away' as const,
    },
    {
      id: 4,
      name: 'Taylor Reed',
      email: 'taylor.reed@example.com',
      role: 'Developer',
      status: 'online' as const,
    },
    {
      id: 5,
      name: 'Morgan Lee',
      email: 'morgan.lee@example.com',
      role: 'Designer',
      status: 'offline' as const,
    },
  ];

  const handleAddMember = () => {
    console.log('Add member clicked');
    // Implement add member functionality
  };

  return (
    <div className="py-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
            Dashboard
          </h1>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4">
          <button
            type="button"
            className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            New Project
          </button>
        </div>
      </div>

      <div className="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <ProjectList projects={projects} />
        </div>
        
        <div>
          <ProjectProgressChart projects={chartData} />
          
          <div className="mt-6">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Team Members</h3>
            <MemberList members={teamMembers} onAddMember={handleAddMember} />
          </div>
        </div>
      </div>

      <div className="mt-12">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-medium text-gray-900 dark:text-white">Recent Tasks</h2>
        </div>
        
        <div className="mt-4 bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
            {recentTasks.map((task) => (
              <li key={task.id}>
                <div className="px-4 py-4 sm:px-6">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {task.title}
                    </p>
                    <div className="ml-2 flex-shrink-0 flex">
                      <p
                        className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          task.status === 'completed'
                            ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
                            : task.status === 'in-progress'
                            ? 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200'
                        }`}
                      >
                        {task.status.replace('-', ' ')}
                      </p>
                    </div>
                  </div>
                  <div className="mt-2 sm:flex sm:justify-between">
                    <div className="sm:flex">
                      <p className="flex items-center text-sm text-gray-500 dark:text-gray-400">
                        {task.project}
                      </p>
                    </div>
                    <div className="mt-2 flex items-center text-sm text-gray-500 dark:text-gray-400 sm:mt-0">
                      <button className="text-blue-600 dark:text-blue-400 hover:text-blue-500 mr-4">
                        Edit
                      </button>
                      <button className="text-red-600 dark:text-red-400 hover:text-red-500">
                        Delete
                      </button>
                    </div>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}