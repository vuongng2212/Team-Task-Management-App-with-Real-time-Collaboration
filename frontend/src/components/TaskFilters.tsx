'use client';

import { useState } from 'react';

interface TaskFiltersProps {
  onFilterChange: (filters: {
    status: string;
    priority: string;
    assignee: string;
    search: string;
  }) => void;
}

export default function TaskFilters({ onFilterChange }: TaskFiltersProps) {
  const [filters, setFilters] = useState({
    status: 'all',
    priority: 'all',
    assignee: 'all',
    search: '',
  });

  const handleChange = (field: string, value: string) => {
    const newFilters = { ...filters, [field]: value };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  return (
    <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-4 mb-6">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label htmlFor="search" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Search
          </label>
          <input
            type="text"
            id="search"
            value={filters.search}
            onChange={(e) => handleChange('search', e.target.value)}
            placeholder="Search tasks..."
            className="block w-full rounded-md border-gray-300 dark:border-gray-600 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:text-white"
          />
        </div>
        
        <div>
          <label htmlFor="status" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Status
          </label>
          <select
            id="status"
            value={filters.status}
            onChange={(e) => handleChange('status', e.target.value)}
            className="block w-full rounded-md border-gray-300 dark:border-gray-600 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:text-white"
          >
            <option value="all">All Statuses</option>
            <option value="todo">To Do</option>
            <option value="in-progress">In Progress</option>
            <option value="review">Review</option>
            <option value="done">Done</option>
          </select>
        </div>
        
        <div>
          <label htmlFor="priority" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Priority
          </label>
          <select
            id="priority"
            value={filters.priority}
            onChange={(e) => handleChange('priority', e.target.value)}
            className="block w-full rounded-md border-gray-300 dark:border-gray-600 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:text-white"
          >
            <option value="all">All Priorities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>
        </div>
        
        <div>
          <label htmlFor="assignee" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Assignee
          </label>
          <select
            id="assignee"
            value={filters.assignee}
            onChange={(e) => handleChange('assignee', e.target.value)}
            className="block w-full rounded-md border-gray-300 dark:border-gray-600 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:text-white"
          >
            <option value="all">All Assignees</option>
            <option value="alex">Alex Johnson</option>
            <option value="sam">Sam Smith</option>
            <option value="jordan">Jordan Williams</option>
            <option value="taylor">Taylor Reed</option>
          </select>
        </div>
      </div>
    </div>
  );
}