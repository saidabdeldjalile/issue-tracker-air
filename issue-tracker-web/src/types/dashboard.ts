export interface DashboardStats {
  totalTickets: number;
  openTickets: number;
  closedTickets: number;
  inProgressTickets: number;
  totalProjects: number;
  totalUsers: number;
  totalDepartments: number;
  averageResolutionTime: number; // in days
}

export interface SlaStats {
  totalTickets: number;
  breachedTickets: number;
  atRiskTickets: number;
  resolvedOnTime: number;
  resolvedLate: number;
  complianceRate: number;
}

export interface TicketStatusDistribution {
  status: string;
  count: number;
  percentage: number;
}

export interface DepartmentStats {
  departmentId: number;
  departmentName: string;
  ticketCount: number;
  openCount: number;
  closedCount: number;
  averageResolutionTime: number;
}

export interface UserStats {
  userId: number;
  userName: string;
  ticketCount: number;
  assignedCount: number;
  resolvedCount: number;
  averageResolutionTime: number;
}

export interface TimeRangeFilter {
  startDate: string;
  endDate: string;
  departmentId?: number;
  projectId?: number;
}

export interface DashboardData {
  stats: DashboardStats;
  statusDistribution: TicketStatusDistribution[];
  departmentStats: DepartmentStats[];
  userStats: UserStats[];
  timeRange: TimeRangeFilter;
  slaStats?: SlaStats;
}