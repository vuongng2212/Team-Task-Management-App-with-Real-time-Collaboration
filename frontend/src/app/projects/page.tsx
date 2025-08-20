'use client';

import { useState } from 'react';
import Link from 'next/link';

export default function ProjectsPage() {
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
    {
      id: 4,
      name: 'Database Migration',
      description: 'Migrate legacy database to cloud infrastructure',
      progress: 10,
      tasks: 8,
      completed: 1,
      team: ['Jordan', 'Casey'],
      dueDate: '2023-08-15',
    },
  ]);

  return (
    <div className="py-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
            Projects
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

      <div className="mt-8">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {projects.map((project) => (
            <div
              key={project.id}
              className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow"
            >
              <div className="px-4 py-5 sm:p-6">
                <div className="flex items-center justify-between">
                  <h3 className="text-lg font-medium text-gray-900 dark:text-white">{project.name}</h3>
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200">
                    {project.progress}% complete
                  </span>
                </div>
                
                <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                  {project.description}
                </p>
                
                <div className="mt-4">
                  <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
                    <span>{project.completed} of {project.tasks} tasks completed</span>
                    <span>Due {new Date(project.dueDate).toLocaleDateString()}</span>
                  </div>
                  <div className="mt-1 w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full"
                      style={{ width: `${project.progress}%` }}
                    ></div>
                  </div>
                </div>
                
                <div className="mt-4">
                  <div className="flex items-center">
                    <div className="flex -space-x-2">
                      {project.team.map((member, index) => (
                        <div
                          key={index}
                          className="inline-block h-8 w-8 rounded-full bg-gray-300 dark:bg-gray-600 border-2 border-white dark:border-gray-800"
                        >
                          <div className="flex items-center justify-center h-full text-xs font-medium text-gray-700 dark:text-gray-300">
                            {member.charAt(0)}
                          </div>
                        </div>
                      ))}
                    </div>
                    <button className="ml-4 text-sm text-blue-600 dark:text-blue-400 hover:text-blue-500">
                      + Add member
                    </button>
                  </div>
                </div>
                
                <div className="mt-6">
                  <Link
                    href={`/projects/${project.id}`}
                    className="text-sm font-medium text-blue-600 dark:text-blue-400 hover:text-blue-500"
                  >
                    View project details<span aria-hidden="true"> &rarr;</span>
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}