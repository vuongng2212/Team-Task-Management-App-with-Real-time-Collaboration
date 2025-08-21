'use client';

import { useState } from 'react';
import MemberList from '@/components/MemberList';

export default function TeamPage() {
  const [members] = useState([
    {
      id: 1,
      name: 'Alex Johnson',
      email: 'alex.johnson@example.com',
      role: 'Designer',
      status: 'online' as const,
    },
    {
      id: 2,
      name: 'Sam Smith',
      email: 'sam.smith@example.com',
      role: 'Developer',
      status: 'offline' as const,
    },
    {
      id: 3,
      name: 'Jordan Williams',
      email: 'jordan.williams@example.com',
      role: 'Project Manager',
      status: 'away' as const,
    },
    {
      id: 4,
      name: 'Taylor Reed',
      email: 'taylor.reed@example.com',
      role: 'Developer',
      status: 'online' as const,
    },
    {
      id: 5,
      name: 'Morgan Lee',
      email: 'morgan.lee@example.com',
      role: 'Designer',
      status: 'offline' as const,
    },
    {
      id: 6,
      name: 'Casey Brown',
      email: 'casey.brown@example.com',
      role: 'Marketing',
      status: 'online' as const,
    },
  ]);

  const handleAddMember = () => {
    console.log('Add member clicked');
    // Implement add member functionality
  };

  return (
    <div className="py-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="min-w-0 flex-1">
          <h1 className="text-2xl font-bold leading-7 text-gray-900 dark:text-white sm:text-3xl sm:truncate">
            Team
          </h1>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4">
          <button
            type="button"
            className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Invite Member
          </button>
        </div>
      </div>

      <div className="mt-8">
        <MemberList members={members} onAddMember={handleAddMember} />
      </div>

      <div className="mt-12">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-medium text-gray-900 dark:text-white">Pending Invitations</h2>
        </div>
        
        <div className="mt-4 bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
            <li>
              <div className="px-4 py-4 sm:px-6">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-medium text-gray-900 dark:text-white truncate">
                    patrick.wilson@example.com
                  </p>
                  <div className="ml-2 flex-shrink-0 flex">
                    <p className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200">
                      Pending
                    </p>
                  </div>
                </div>
                <div className="mt-2 sm:flex sm:justify-between">
                  <div className="sm:flex">
                    <p className="flex items-center text-sm text-gray-500 dark:text-gray-400">
                      Invited by Alex Johnson
                    </p>
                  </div>
                  <div className="mt-2 flex items-center text-sm text-gray-500 dark:text-gray-400 sm:mt-0">
                    <p>Invited 2 days ago</p>
                  </div>
                </div>
                <div className="mt-2 flex items-center">
                  <button className="text-blue-600 dark:text-blue-400 hover:text-blue-500 text-sm mr-4">
                    Resend Invitation
                  </button>
                  <button className="text-red-600 dark:text-red-400 hover:text-red-500 text-sm">
                    Cancel Invitation
                  </button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}