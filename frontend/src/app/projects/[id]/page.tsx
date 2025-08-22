'use client';

import { useState } from 'react';
import Link from 'next/link';
import KanbanBoard from '@/components/KanbanBoard';
import MemberList from '@/components/MemberList';
import TopNavbar from '@/components/TopNavbar';

export default function ProjectPage({ params }: any) { // eslint-disable-line @typescript-eslint/no-explicit-any
  const projectId = params.id;
  
  // Mock project data
  const project = {
    id: projectId,
    name: 'Website Redesign',
    description: 'Complete redesign of company website to improve user experience and conversion rates.',
  };

  const members = [
    { id: 1, name: 'Alex Johnson', email: 'alex@example.com', role: 'Designer', status: 'online' as const },
    { id: 2, name: 'Sam Smith', email: 'sam@example.com', role: 'Developer', status: 'offline' as const },
    { id: 3, name: 'Jordan Williams', email: 'jordan@example.com', role: 'Project Manager', status: 'away' as const },
  ];

  // Mock kanban columns and tasks
  const [columns, setColumns] = useState([
    {
      id: 'todo',
      title: 'To Do',
      taskIds: ['task1', 'task2', 'task3'],
    },
    {
      id: 'inprogress',
      title: 'In Progress',
      taskIds: ['task4', 'task5'],
    },
    {
      id: 'review',
      title: 'Review',
      taskIds: ['task6'],
    },
    {
      id: 'done',
      title: 'Done',
      taskIds: ['task7', 'task8'],
    },
  ]);

  const [tasks, setTasks] = useState({
    task1: { id: 'task1', content: 'Create wireframes for homepage' },
    task2: { id: 'task2', content: 'Design color palette and typography' },
    task3: { id: 'task3', content: 'Research competitor websites' },
    task4: { id: 'task4', content: 'Implement responsive navigation' },
    task5: { id: 'task5', content: 'Setup development environment' },
    task6: { id: 'task6', content: 'Write unit tests for authentication' },
    task7: { id: 'task7', content: 'Create project documentation' },
    task8: { id: 'task8', content: 'Setup continuous integration' },
  });

  const handleTaskMove = (taskId: string, fromColumnId: string, toColumnId: string) => {
    // Implementation for moving tasks between columns
    console.log(`Moving task ${taskId} from ${fromColumnId} to ${toColumnId}`);
  };

  const handleTaskAdd = (columnId: string, content: string) => {
    const newTaskId = `task${Date.now()}`;
    const newTask = {
      id: newTaskId,
      content,
    };

    setTasks(prev => ({
      ...prev,
      [newTaskId]: newTask,
    }));

    setColumns(prev => 
      prev.map(column => 
        column.id === columnId
          ? { ...column, taskIds: [...column.taskIds, newTaskId] }
          : column
      )
    );
  };

  const handleAddMember = () => {
    console.log('Add member clicked');
    // Implement add member functionality
  };

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
                      {project.name}
                    </h1>
                    <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                      {project.description}
                    </p>
                  </div>
                  <div className="mt-4 flex md:mt-0 md:ml-4">
                    <button
                      type="button"
                      className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                      Add Task
                    </button>
                  </div>
                </div>

                {/* Project Members */}
                <div className="mt-6">
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Team Members</h2>
                  <MemberList members={members} onAddMember={handleAddMember} />
                </div>

                {/* Kanban Board */}
                <div className="mt-8">
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Project Board</h2>
                  <KanbanBoard 
                    initialColumns={columns}
                    initialTasks={tasks}
                    onTaskMove={handleTaskMove}
                    onTaskAdd={handleTaskAdd}
                  />
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}