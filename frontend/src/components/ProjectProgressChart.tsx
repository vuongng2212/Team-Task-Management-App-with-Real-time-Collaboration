'use client';

interface ProjectProgressChartProps {
  projects: {
    name: string;
    progress: number;
  }[];
}

export default function ProjectProgressChart({ projects }: ProjectProgressChartProps) {
  return (
    <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6">
      <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Project Progress</h3>
      <div className="space-y-4">
        {projects.map((project, index) => (
          <div key={index}>
            <div className="flex items-center justify-between mb-1">
              <span className="text-sm font-medium text-gray-900 dark:text-white">{project.name}</span>
              <span className="text-sm font-medium text-gray-900 dark:text-white">{project.progress}%</span>
            </div>
            <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
              <div
                className="bg-blue-600 h-2 rounded-full"
                style={{ width: `${project.progress}%` }}
              ></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}