'use client';

import { useRouter } from 'next/navigation';
import { useState, useEffect } from 'react';
import TopNavbar from '@/components/TopNavbar';

export const dynamic = 'force-dynamic';

export default function SearchPage() {
  const router = useRouter();
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<{
    tasks: {
      id: number;
      title: string;
      project: string;
      status: string;
    }[];
    projects: {
      id: number;
      name: string;
      description: string;
    }[];
    people: {
      id: number;
      name: string;
      role: string;
    }[];
  } | null>(null);

  useEffect(() => {
    // Get query from URL
    const urlParams = new URLSearchParams(window.location.search);
    const q = urlParams.get('q');
    if (q) {
      setQuery(q);
      // Mock search results
      setResults({
        tasks: [
          {
            id: 1,
            title: 'Create wireframes for homepage',
            project: 'Website Redesign',
            status: 'completed',
          },
          {
            id: 2,
            title: 'Design color palette and typography',
            project: 'Website Redesign',
            status: 'completed',
          },
          {
            id: 3,
            title: 'Research competitor websites',
            project: 'Website Redesign',
            status: 'in-progress',
          },
        ],
        projects: [
          {
            id: 1,
            name: 'Website Redesign',
            description: 'Complete redesign of company website to improve user experience',
          },
          {
            id: 2,
            name: 'Mobile App Development',
            description: 'Development of new mobile application for customer engagement',
          },
        ],
        people: [
          {
            id: 1,
            name: 'Alex Johnson',
            role: 'Designer',
          },
          {
            id: 2,
            name: 'Sam Smith',
            role: 'Developer',
          },
        ],
      });
    }
  }, []);

  if (!query) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <TopNavbar />
        
        {/* Main content */}
        <div className="flex flex-col flex-1">
          <main className="flex-1 pb-8">
            <div className="py-6">
              <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <div className="py-6">
                  <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                    <div className="min-w-0 flex-1">
                      <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
                        Search
                      </h1>
                      <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                        Enter a search term to find tasks, projects, and people.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </main>
        </div>
      </div>
    );
  }

  if (!results) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <TopNavbar />
        
        {/* Main content */}
        <div className="flex flex-col flex-1">
          <main className="flex-1 pb-8">
            <div className="py-6">
              <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <div className="py-6">
                  <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                    <div className="min-w-0 flex-1">
                      <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
                        Searching...
                      </h1>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </main>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <TopNavbar />
      
      {/* Main content */}
      <div className="flex flex-col flex-1">
        <main className="flex-1 pb-8">
          <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
              <div className="py-6">
                <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                  <div className="min-w-0 flex-1">
                    <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
                      Search Results
                    </h1>
                    <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                      Results for &quot;{query}&quot;
                    </p>
                  </div>
                </div>

                <div className="mt-8">
                  {/* Tasks Section */}
                  <div className="mb-12">
                    <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                      Tasks <span className="text-gray-500 dark:text-gray-400">({results.tasks.length})</span>
                    </h2>
                    <div className="bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-md">
                      <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                        {results.tasks.map((task) => (
                          <li key={task.id}>
                            <div className="px-4 py-4 sm:px-6">
                              <div className="flex items-center justify-between">
                                <p className="text-sm font-medium text-blue-600 dark:text-blue-400 truncate">
                                  {task.title}
                                </p>
                                <div className="ml-2 flex-shrink-0 flex">
                                  <p className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200">
                                    {task.status}
                                  </p>
                                </div>
                              </div>
                              <div className="mt-2 sm:flex sm:justify-between">
                                <div className="sm:flex">
                                  <p className="flex items-center text-sm text-gray-500 dark:text-gray-400">
                                    {task.project}
                                  </p>
                                </div>
                              </div>
                            </div>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>

                  {/* Projects Section */}
                  <div className="mb-12">
                    <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                      Projects <span className="text-gray-500 dark:text-gray-400">({results.projects.length})</span>
                    </h2>
                    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
                      {results.projects.map((project) => (
                        <div
                          key={project.id}
                          className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow"
                        >
                          <div className="px-4 py-5 sm:p-6">
                            <h3 className="text-lg font-medium text-gray-900 dark:text-white">{project.name}</h3>
                            <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                              {project.description}
                            </p>
                            <div className="mt-4">
                              <button className="text-sm font-medium text-blue-600 dark:text-blue-400 hover:text-blue-500">
                                View project<span aria-hidden="true"> &rarr;</span>
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* People Section */}
                  <div>
                    <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                      People <span className="text-gray-500 dark:text-gray-400">({results.people.length})</span>
                    </h2>
                    <div className="bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-md">
                      <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                        {results.people.map((person) => (
                          <li key={person.id}>
                            <div className="px-4 py-4 sm:px-6">
                              <div className="flex items-center">
                                <div className="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300 dark:bg-gray-600 flex items-center justify-center">
                                  <span className="text-gray-700 dark:text-gray-300 font-medium">
                                    {person.name.charAt(0)}
                                  </span>
                                </div>
                                <div className="ml-4">
                                  <div className="flex items-center">
                                    <h3 className="text-sm font-medium text-gray-900 dark:text-white">
                                      {person.name}
                                    </h3>
                                  </div>
                                  <p className="text-sm text-gray-500 dark:text-gray-400">{person.role}</p>
                                </div>
                              </div>
                            </div>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}