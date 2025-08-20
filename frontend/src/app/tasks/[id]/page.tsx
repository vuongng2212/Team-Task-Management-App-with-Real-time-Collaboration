'use client';

import { useState } from 'react';
import Link from 'next/link';

export default function TaskDetailPage({ params }: { params: { id: string } }) {
  const taskId = params.id;
  
  // Mock task data
  const [task, setTask] = useState({
    id: taskId,
    title: 'Create wireframes for homepage',
    description: 'Create detailed wireframes for the new homepage design, including all breakpoints and interactive elements.',
    project: 'Website Redesign',
    status: 'in-progress',
    priority: 'high',
    assignee: 'Alex Johnson',
    dueDate: '2023-06-15',
    createdAt: '2023-06-01',
    updatedAt: '2023-06-10',
  });

  const [comments, setComments] = useState([
    {
      id: 1,
      author: 'Sam Smith',
      content: 'These wireframes look great! I have a few suggestions for the mobile layout.',
      createdAt: '2023-06-10T14:30:00Z',
    },
    {
      id: 2,
      author: 'Jordan Williams',
      content: 'Thanks for the feedback. I'll make those adjustments and share an updated version.',
      createdAt: '2023-06-10T15:45:00Z',
    },
  ]);

  const [newComment, setNewComment] = useState('');

  const handleAddComment = () => {
    if (!newComment.trim()) return;
    
    const comment = {
      id: comments.length + 1,
      author: 'You',
      content: newComment,
      createdAt: new Date().toISOString(),
    };
    
    setComments([...comments, comment]);
    setNewComment('');
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'todo':
        return 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200';
      case 'in-progress':
        return 'bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200';
      case 'review':
        return 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200';
      case 'done':
        return 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200';
      default:
        return 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high':
        return 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200';
      case 'medium':
        return 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200';
      case 'low':
        return 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200';
      default:
        return 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200';
    }
  };

  return (
    <div className=\"py-6\">
      <div className=\"flex flex-col md:flex-row md:items-center md:justify-between\">
        <div className=\"min-w-0 flex-1\">
          <h1 className=\"text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate\">
            Task Details
          </h1>
        </div>
        <div className=\"mt-4 flex md:mt-0 md:ml-4\">
          <button
            type=\"button\"
            className=\"ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500\"
          >
            Edit Task
          </button>
        </div>
      </div>

      <div className=\"mt-8 grid grid-cols-1 lg:grid-cols-3 gap-6\">
        <div className=\"lg:col-span-2\">
          <div className=\"bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-lg\">
            <div className=\"px-4 py-5 sm:px-6\">
              <h3 className=\"text-lg leading-6 font-medium text-gray-900 dark:text-white\">
                {task.title}
              </h3>
              <p className=\"mt-1 max-w-2xl text-sm text-gray-500 dark:text-gray-400\">
                {task.description}
              </p>
            </div>
            <div className=\"border-t border-gray-200 dark:border-gray-700\">
              <dl>
                <div className=\"bg-gray-50 dark:bg-gray-700 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Project
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    {task.project}
                  </dd>
                </div>
                <div className=\"bg-white dark:bg-gray-800 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Status
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    <span
                      className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full \${getStatusColor(
                        task.status
                      )}`}
                    >
                      {task.status.replace('-', ' ')}
                    </span>
                  </dd>
                </div>
                <div className=\"bg-gray-50 dark:bg-gray-700 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Priority
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    <span
                      className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full \${getPriorityColor(
                        task.priority
                      )}`}
                    >
                      {task.priority}
                    </span>
                  </dd>
                </div>
                <div className=\"bg-white dark:bg-gray-800 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Assignee
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    {task.assignee}
                  </dd>
                </div>
                <div className=\"bg-gray-50 dark:bg-gray-700 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Due Date
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    {new Date(task.dueDate).toLocaleDateString()}
                  </dd>
                </div>
                <div className=\"bg-white dark:bg-gray-800 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Created
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    {new Date(task.createdAt).toLocaleDateString()}
                  </dd>
                </div>
                <div className=\"bg-gray-50 dark:bg-gray-700 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6\">
                  <dt className=\"text-sm font-medium text-gray-500 dark:text-gray-400\">
                    Last Updated
                  </dt>
                  <dd className=\"mt-1 text-sm text-gray-900 dark:text-white sm:mt-0 sm:col-span-2\">
                    {new Date(task.updatedAt).toLocaleDateString()}
                  </dd>
                </div>
              </dl>
            </div>
          </div>
        </div>

        <div>
          <div className=\"bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-lg\">
            <div className=\"px-4 py-5 sm:px-6\">
              <h3 className=\"text-lg leading-6 font-medium text-gray-900 dark:text-white\">
                Activity
              </h3>
            </div>
            <div className=\"border-t border-gray-200 dark:border-gray-700\">
              <ul className=\"divide-y divide-gray-200 dark:divide-gray-700\">
                {comments.map((comment) => (
                  <li key={comment.id} className=\"px-4 py-4 sm:px-6\">
                    <div className=\"flex items-center\">
                      <div className=\"flex-shrink-0 h-10 w-10 rounded-full bg-gray-300 dark:bg-gray-600 flex items-center justify-center\">
                        <span className=\"text-gray-700 dark:text-gray-300 font-medium\">
                          {comment.author.charAt(0)}
                        </span>
                      </div>
                      <div className=\"ml-4\">
                        <div className=\"flex items-center\">
                          <h4 className=\"text-sm font-medium text-gray-900 dark:text-white\">
                            {comment.author}
                          </h4>
                          <span className=\"ml-2 text-xs text-gray-500 dark:text-gray-400\">
                            {new Date(comment.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        <p className=\"mt-1 text-sm text-gray-500 dark:text-gray-400\">
                          {comment.content}
                        </p>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
              <div className=\"px-4 py-5 sm:px-6\">
                <div className=\"flex\">
                  <div className=\"flex-shrink-0 h-10 w-10 rounded-full bg-gray-300 dark:bg-gray-600 flex items-center justify-center\">
                    <span className=\"text-gray-700 dark:text-gray-300 font-medium\">Y</span>
                  </div>
                  <div className=\"ml-4 flex-1\">
                    <textarea
                      value={newComment}
                      onChange={(e) => setNewComment(e.target.value)}
                      placeholder=\"Add a comment...\"
                      className=\"block w-full rounded-md border-gray-300 dark:border-gray-600 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:text-white\"
                      rows={3}
                    />
                    <div className=\"mt-2 flex justify-end\">
                      <button
                        onClick={handleAddComment}
                        className=\"inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500\"
                      >
                        Comment
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}