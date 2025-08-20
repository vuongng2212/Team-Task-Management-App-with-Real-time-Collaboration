'use client';

import { useState } from 'react';
import TaskList from '@/components/TaskList';

export default function TasksPage() {
  const [tasks] = useState([
    {
      id: 1,
      title: 'Create wireframes for homepage',
      project: 'Website Redesign',
      status: 'done',
      priority: 'high',
      assignee: 'Alex Johnson',
      dueDate: '2023-06-10',
    },
    {
      id: 2,
      title: 'Design color palette and typography',
      project: 'Website Redesign',
      status: 'done',
      priority: 'medium',
      assignee: 'Alex Johnson',
      dueDate: '2023-06-05',
    },
    {
      id: 3,
      title: 'Research competitor websites',
      project: 'Website Redesign',
      status: 'in-progress',
      priority: 'low',
      assignee: 'Jordan Williams',
      dueDate: '2023-06-15',
    },
    {
      id: 4,
      title: 'Implement responsive navigation',
      project: 'Mobile App Development',
      status: 'todo',
      priority: 'high',
      assignee: 'Sam Smith',
      dueDate: '2023-06-20',
    },
    {
      id: 5,
      title: 'Setup development environment',
      project: 'Mobile App Development',
      status: 'todo',
      priority: 'medium',
      assignee: 'Taylor Reed',
      dueDate: '2023-06-18',
    },
    {
      id: 6,
      title: 'Create user personas',
      project: 'Marketing Campaign',
      status: 'todo',
      priority: 'low',
      assignee: 'Casey Brown',
      dueDate: '2023-06-25',
    },
    {
      id: 7,
      title: 'Write unit tests for authentication',
      project: 'Mobile App Development',
      status: 'review',
      priority: 'high',
      assignee: 'Sam Smith',
      dueDate: '2023-06-22',
    },
    {
      id: 8,
      title: 'Create project documentation',
      project: 'Website Redesign',
      status: 'done',
      priority: 'medium',
      assignee: 'Jordan Williams',
      dueDate: '2023-06-12',
    },
  ]);

  return (
    <div className="py-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
            Tasks
          </h1>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4">
          <button
            type="button"
            className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            New Task
          </button>
        </div>
      </div>

      <div className="mt-8">
        <TaskList tasks={tasks} />
      </div>
    </div>
  );
}