import React, { useState, useEffect } from "react";
import axios from "../../api/axios";
import { Department, Project } from "../../TicketResponse";


import { TimeRangeFilter } from "../../types/dashboard";

interface FilterPanelProps {
  filters: TimeRangeFilter;
  onFilterChange: (filters: TimeRangeFilter) => void;
  onReset: () => void;
}

const FilterPanel: React.FC<FilterPanelProps> = ({ 
  filters, 
  onFilterChange, 
  onReset 
}) => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLists = async () => {
      try {
        setLoading(true);
        const [deptsRes, projsRes] = await Promise.all([
          axios.get('/departments'),
          axios.get('/projects')
        ]);
        // Handle paginated responses - extract content array from Page object
        const deptsData = deptsRes.data?.content || deptsRes.data || [];
        const projsData = projsRes.data?.content || projsRes.data || [];
        setDepartments(Array.isArray(deptsData) ? deptsData : []);
        setProjects(Array.isArray(projsData) ? projsData : []);
      } catch (error) {
        console.error('Error fetching lists:', error);
        setDepartments([]);
        setProjects([]);
      } finally {
        setLoading(false);
      }
    };

    fetchLists();
  }, []);

  const handleDateChange = (field: 'startDate' | 'endDate', value: string) => {
    onFilterChange({
      ...filters,
      [field]: value
    });
  };

  const handleDepartmentChange = (value: string) => {
    onFilterChange({
      ...filters,
      departmentId: value ? parseInt(value) : undefined
    });
  };

  const handleProjectChange = (value: string) => {
    onFilterChange({
      ...filters,
      projectId: value ? parseInt(value) : undefined
    });
  };

  return (
    <div className="bg-base-100 rounded-lg shadow-sm border border-base-200 p-4">
      <div className="flex flex-col sm:flex-row gap-4 items-center justify-between">
        <div className="flex flex-col sm:flex-row gap-4 items-center flex-1">
          <div className="form-control">
            <label className="label text-sm text-base-content/70">
              Start Date
            </label>
            <input
              type="date"
              value={filters.startDate}
              onChange={(e) => handleDateChange('startDate', e.target.value)}
              className="input input-bordered input-sm w-full max-w-xs"
            />
          </div>
          
          <div className="form-control">
            <label className="label text-sm text-base-content/70">
              End Date
            </label>
            <input
              type="date"
              value={filters.endDate}
              onChange={(e) => handleDateChange('endDate', e.target.value)}
              className="input input-bordered input-sm w-full max-w-xs"
            />
          </div>

          <div className="form-control">
            <label className="label text-sm text-base-content/70">
              Department
            </label>
<select
              value={filters.departmentId || ''}
              onChange={(e) => handleDepartmentChange(e.target.value)}
              className="select select-bordered select-sm w-full max-w-xs"
              disabled={loading}
            >
              <option value="">All Departments</option>
              {loading ? (
                <option disabled>Loading...</option>
              ) : (
                departments.map((dept) => (
                  <option key={dept.id} value={dept.id.toString()}>
                    {dept.name}
                  </option>
                ))
              )}
            </select>

          </div>

          <div className="form-control">
            <label className="label text-sm text-base-content/70">
              Project
            </label>
<select
              value={filters.projectId || ''}
              onChange={(e) => handleProjectChange(e.target.value)}
              className="select select-bordered select-sm w-full max-w-xs"
              disabled={loading}
            >
              <option value="">All Projects</option>
              {loading ? (
                <option disabled>Loading...</option>
              ) : (
                projects.map((proj) => (
                  <option key={proj.id} value={proj.id.toString()}>
                    {proj.name}
                  </option>
                ))
              )}
            </select>

          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={onReset}
            className="btn btn-sm btn-outline"
          >
            Reset Filters
          </button>
          <button
            onClick={() => onFilterChange(filters)}
            className="btn btn-sm btn-primary"
          >
            Apply Filters
          </button>
        </div>
      </div>
    </div>
  );
};

export default FilterPanel;