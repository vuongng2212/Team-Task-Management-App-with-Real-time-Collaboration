'use client';

import Link from 'next/link';

interface Project {
  id: number;
  name: string;
  description: string;
  progress: number;
  tasks: number;
  completed: number;
  team: string[];
  dueDate: string;
}

interface ProjectListProps {
  projects: Project[];
  onNewProject?: () => void;
}

export default function ProjectList({ projects, onNewProject }: ProjectListProps) {
  return (
    <div>
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            Your Projects
          </h2>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Manage all your projects in one place
          </p>
        </div>
        {onNewProject && (
          <div className="mt-4 flex md:mt-0 md:ml-4">
            <button
              type="button"
              onClick={onNewProject}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all"
            >
              <svg className="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              New Project
            </button>
          </div>
        )}
      </div>

      <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {projects.map((project) => (
          <div
            key={project.id}
            className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-xl hover:shadow-lg transition-shadow duration-300 border border-gray-200 dark:border-gray-700"
          >
            <div className="px-5 py-5 sm:p-6">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white">{project.name}</h3>
                  <p className="mt-1 text-sm text-gray-500 dark:text-gray-400 line-clamp-2">
                    {project.description}
                  </p>
                </div>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200">
                  {project.progress}% complete
                </span>
              </div>

              <div className="mt-4">
                <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
                  <span>{project.completed} of {project.tasks} tasks completed</span>
                  <span>Due {new Date(project.dueDate).toLocaleDateString()}</span>
                </div>
                <div className="mt-2 w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2.5">
                  <div
                    className="bg-gradient-to-r from-blue-400 to-indigo-500 h-2.5 rounded-full"
                    style={{ width: `${project.progress}%` }}
                  ></div>
                </div>
              </div>

              <div className="mt-4">
                <div className="flex items-center justify-between">
                  <div className="flex -space-x-2">
                    {project.team.map((member, index) => (
                      <div
                        key={index}
                        className="inline-block h-8 w-8 rounded-full bg-gradient-to-r from-blue-400 to-indigo-500 border-2 border-white dark:border-gray-800 shadow-sm"
                      >
                        <div className="flex items-center justify-center h-full text-xs font-medium text-white">
                          {member.charAt(0)}
                        </div>
                      </div>
                    ))}
                  </div>
                  <button className="text-sm text-blue-600 dark:text-blue-400 hover:text-blue-500 font-medium transition-colors">
                    + Add member
                  </button>
                </div>
              </div>

              <div className="mt-6">
                <Link
                  href={`/projects/${project.id}`}
                  className="inline-flex items-center text-sm font-medium text-blue-600 dark:text-blue-400 hover:text-blue-500 transition-colors"
                >
                  View project details
                  <svg className="ml-1 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                  </svg>
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}