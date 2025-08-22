'use client';

import { useState } from 'react';

interface Task {
  id: string;
  content: string;
  assignee?: string;
  dueDate?: string;
}

interface Column {
  id: string;
  title: string;
  taskIds: string[];
}

interface KanbanBoardProps {
  initialColumns: Column[];
  initialTasks: Record<string, Task>;
  onTaskMove?: (taskId: string, fromColumnId: string, toColumnId: string) => void;
  onTaskAdd?: (columnId: string, content: string) => void;
}

export default function KanbanBoard({
  initialColumns,
  initialTasks,
  onTaskMove,
  onTaskAdd,
}: KanbanBoardProps) {
  const [columns, setColumns] = useState<Column[]>(initialColumns);
  const [tasks, setTasks] = useState<Record<string, Task>>(initialTasks);
  const [newTaskContents, setNewTaskContents] = useState<Record<string, string>>(
    initialColumns.reduce((acc, column) => ({ ...acc, [column.id]: '' }), {})
  );

  const handleAddTask = (columnId: string) => {
    const content = newTaskContents[columnId];
    if (!content.trim()) return;

    if (onTaskAdd) {
      onTaskAdd(columnId, content);
      setNewTaskContents({ ...newTaskContents, [columnId]: '' });
      return;
    }

    // Default implementation if no onTaskAdd handler is provided
    const newTaskId = `task${Date.now()}`;
    const newTask = {
      id: newTaskId,
      content,
    };

    setTasks((prev) => ({
      ...prev,
      [newTaskId]: newTask,
    }));

    setColumns((prev) =>
      prev.map((column) =>
        column.id === columnId
          ? { ...column, taskIds: [...column.taskIds, newTaskId] }
          : column
      )
    );

    setNewTaskContents({ ...newTaskContents, [columnId]: '' });
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5 mt-6">
      {columns.map((column) => (
        <div
          key={column.id}
          className="bg-gray-50 dark:bg-gray-800/50 rounded-xl shadow-sm p-4 border border-gray-200 dark:border-gray-700"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-900 dark:text-white">
              {column.title}{' '}
              <span className="text-gray-500 dark:text-gray-400 text-sm">
                ({column.taskIds.length})
              </span>
            </h3>
            <button className="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 rounded-full p-1 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
              <svg
                className="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
              </svg>
            </button>
          </div>

          <div className="space-y-3 min-h-[120px]">
            {column.taskIds.map((taskId) => {
              const task = tasks[taskId];
              if (!task) return null;
              return (
                <div
                  key={task.id}
                  className="bg-white dark:bg-gray-700 rounded-lg shadow p-4 cursor-move hover:shadow-md transition-all duration-200 border border-gray-200 dark:border-gray-600"
                  draggable
                >
                  <p className="text-sm text-gray-900 dark:text-white font-medium">
                    {task.content}
                  </p>
                  {task.dueDate && (
                    <div className="mt-2 flex items-center text-xs text-gray-500 dark:text-gray-400">
                      <svg className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      {new Date(task.dueDate).toLocaleDateString()}
                    </div>
                  )}
                  <div className="mt-3 flex items-center justify-between">
                    {task.assignee ? (
                      <div className="flex items-center">
                        <div className="flex-shrink-0 h-6 w-6 rounded-full bg-gradient-to-r from-blue-400 to-indigo-500 flex items-center justify-center">
                          <span className="text-xs text-white">
                            {task.assignee.charAt(0)}
                          </span>
                        </div>
                        <span className="ml-2 text-xs text-gray-500 dark:text-gray-400">
                          {task.assignee}
                        </span>
                      </div>
                    ) : (
                      <div></div>
                    )}
                    <button className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 p-1 rounded-full hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
                      <svg
                        className="h-4 w-4"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"
                        />
                      </svg>
                    </button>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Add Task Form */}
          <div className="mt-4">
            <textarea
              value={newTaskContents[column.id] || ''}
              onChange={(e) =>
                setNewTaskContents({
                  ...newTaskContents,
                  [column.id]: e.target.value,
                })
              }
              placeholder="Add a task..."
              className="w-full px-3 py-2 text-sm text-gray-900 dark:text-white bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg shadow-sm placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
              rows={2}
            />
            <div className="mt-2 flex justify-end">
              <button
                onClick={() => handleAddTask(column.id)}
                className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-lg shadow-sm text-white bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all"
              >
                Add Task
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}