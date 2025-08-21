'use client';

import { useRouter } from 'next/navigation';
import TaskForm from '@/components/TaskForm';

export default function CreateTaskPage() {
  const router = useRouter();

  // Mock data for projects and team members
  const projects = [
    { id: '1', name: 'Website Redesign' },
    { id: '2', name: 'Mobile App Development' },
    { id: '3', name: 'Marketing Campaign' },
  ];

  const teamMembers = [
    { id: '1', name: 'Alex Johnson' },
    { id: '2', name: 'Sam Smith' },
    { id: '3', name: 'Jordan Williams' },
    { id: '4', name: 'Taylor Reed' },
  ];

  const handleSubmit = (data: {
    title: string;
    description: string;
    projectId: string;
    status: 'todo' | 'in-progress' | 'review' | 'done';
    priority: 'low' | 'medium' | 'high';
    assigneeId: string;
    dueDate: string;
  }) => {
    console.log('Task data:', data);
    // Here you would typically send the data to your backend
    // For now, we'll just redirect back to the tasks list
    router.push('/tasks');
  };

  const handleCancel = () => {
    router.push('/tasks');
  };

  return (
    <div className="py-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
            Create New Task
          </h1>
        </div>
      </div>

      <div className="mt-8">
        <TaskForm 
          projects={projects}
          teamMembers={teamMembers}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
        />
      </div>
    </div>
  );
}