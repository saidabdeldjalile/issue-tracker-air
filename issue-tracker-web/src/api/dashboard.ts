import api from "./axios";
import { DashboardData, TimeRangeFilter } from "../types/dashboard";

export const dashboardApi = {
  // Get overall dashboard statistics
  getStats: async (): Promise<DashboardData> => {
    const response = await api.get("/dashboard/stats");
    return response.data;
  },

  // Get dashboard data with optional filters
  getDashboardData: async (filters?: TimeRangeFilter): Promise<DashboardData> => {
    const params: any = {};
    if (filters?.startDate) params.startDate = filters.startDate;
    if (filters?.endDate) params.endDate = filters.endDate;
    if (filters?.departmentId) params.departmentId = filters.departmentId;
    if (filters?.projectId) params.projectId = filters.projectId;
    
    const response = await api.get("/dashboard/data", { params });
    return response.data;
  },

  // Get ticket status distribution
  getStatusDistribution: async (filters?: TimeRangeFilter) => {
    const params: any = {};
    if (filters?.startDate) params.startDate = filters.startDate;
    if (filters?.endDate) params.endDate = filters.endDate;
    if (filters?.departmentId) params.departmentId = filters.departmentId;
    if (filters?.projectId) params.projectId = filters.projectId;
    
    const response = await api.get("/dashboard/status-distribution", { params });
    return response.data;
  },

  // Get department statistics
  getDepartmentStats: async (filters?: TimeRangeFilter) => {
    const params: any = {};
    if (filters?.startDate) params.startDate = filters.startDate;
    if (filters?.endDate) params.endDate = filters.endDate;
    if (filters?.departmentId) params.departmentId = filters.departmentId;
    if (filters?.projectId) params.projectId = filters.projectId;
    
    const response = await api.get("/dashboard/department-stats", { params });
    return response.data;
  },

  // Get user statistics
  getUserStats: async (filters?: TimeRangeFilter) => {
    const params: any = {};
    if (filters?.startDate) params.startDate = filters.startDate;
    if (filters?.endDate) params.endDate = filters.endDate;
    if (filters?.departmentId) params.departmentId = filters.departmentId;
    if (filters?.projectId) params.projectId = filters.projectId;
    
    const response = await api.get("/dashboard/user-stats", { params });
    return response.data;
  },

  // Get SLA metrics
  getSlaMetrics: async () => {
    const response = await api.get("/sla/metrics");
    return {
      ...response.data,
      complianceRate: Number(response.data.complianceRate),
    };
  },


};