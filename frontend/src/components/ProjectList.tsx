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
          <h2 className="text-lg font-medium text-gray-900 dark:text-white">
            Your Projects
          </h2>
        </div>
        {onNewProject && (
          <div className="mt-4 flex md:mt-0 md:ml-4">
            <button
              type="button"
              onClick={onNewProject}
              className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              New Project
            </button>
          </div>
        )}
      </div>

      <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
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
  );
}