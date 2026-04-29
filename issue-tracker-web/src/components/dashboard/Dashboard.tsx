import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { dashboardApi } from "../../api/dashboard";
import { DashboardData, TimeRangeFilter, SlaStats } from "../../types/dashboard";
import FilterPanel from "./FilterPanel";
import StatsCards from "./StatsCards";
import Charts from "./Charts";
import KanbanBoard from "../KanbanBoard";
import GanttChart from "../GanttChart";
import useAuth from "../../hooks/useAuth";

type ViewMode = "stats" | "charts" | "kanban" | "gantt";

const Dashboard: React.FC = () => {
  const { auth } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<DashboardData | null>(null);
  const [slaStats, setSlaStats] = useState<SlaStats | null>(null);
  const [filters, setFilters] = useState<TimeRangeFilter>({
    startDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 30 days ago
    endDate: new Date().toISOString().split('T')[0], // today
  });
  const [viewMode, setViewMode] = useState<ViewMode>("stats");

  // Check if user is admin
  useEffect(() => {
    const token = auth?.token || localStorage.getItem("token");
    if (!token) {
      // If no token, redirect to login
      navigate("/login");
      return;
    }
    
    if (auth?.role !== "ADMIN") {
      navigate("/projects");
    }
  }, [auth, navigate]);

  // Fetch dashboard data
  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [dashboardResult, slaResult] = await Promise.all([
        dashboardApi.getDashboardData(filters),
        dashboardApi.getSlaMetrics(),
      ]);
      setData(dashboardResult);
      setSlaStats(slaResult);
    } catch (err: any) {
      console.error("Error fetching dashboard data:", err);
      setError(err.response?.data?.message || "Failed to load dashboard data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, [filters]);

  const handleFilterChange = (newFilters: TimeRangeFilter) => {
    setFilters(newFilters);
  };

  const handleReset = () => {
    setFilters({
      startDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      endDate: new Date().toISOString().split('T')[0],
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-error shadow-lg">
        <div>
          <span>{error}</span>
        </div>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="alert alert-warning shadow-lg">
        <div>
          <span>No dashboard data available</span>
        </div>
      </div>
    );
  }

  if (!slaStats) {
    return (
      <div className="alert alert-warning shadow-lg">
        <div>
          <span>Loading SLA data...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <section className="page-section overflow-hidden">
        <div className="border-b border-base-300/60 bg-gradient-to-r from-blue-600/10 via-violet-600/10 to-cyan-500/10 p-6 md:p-8">
          <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h1 className="section-heading">Dashboard d'Analyse</h1>
              <p className="mt-2.5 max-w-2xl text-sm leading-6 text-base-content/65">
                Vue d'ensemble des métriques, analyses et visualisations des tickets et projets.
              </p>
            </div>

            {/* View Mode Tabs */}
            <div className="tabs tabs-boxed">
              <a
                className={`tab ${viewMode === "stats" ? "tab-active" : ""}`}
                onClick={() => setViewMode("stats")}
              >
                Stats & Charts
              </a>
              <a
                className={`tab ${viewMode === "kanban" ? "tab-active" : ""}`}
                onClick={() => setViewMode("kanban")}
              >
                Kanban
              </a>
              <a
                className={`tab ${viewMode === "gantt" ? "tab-active" : ""}`}
                onClick={() => setViewMode("gantt")}
              >
                Gantt
              </a>
            </div>
          </div>
        </div>

        <div className="p-6 md:p-8">
          <FilterPanel
            filters={filters}
            onFilterChange={handleFilterChange}
            onReset={handleReset}
          />

          <div className="space-y-6">
            {viewMode === "stats" && (
              <>
                <StatsCards stats={data.stats} />
                {slaStats && (
                  <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                    <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 p-5 shadow-sm">
                      <h3 className="text-sm font-medium text-base-content/70">Taux de Conformité SLA</h3>
                      <p className={`text-2xl font-bold ${slaStats.complianceRate >= 90 ? "text-success" : slaStats.complianceRate >= 70 ? "text-warning" : "text-error"}`}>
                        {slaStats.complianceRate.toFixed(1)}%
                      </p>
                    </div>
                    <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 p-5 shadow-sm">
                      <h3 className="text-sm font-medium text-base-content/70">Tickets SLA Dépassé</h3>
                      <p className="text-2xl font-bold text-error">{slaStats.breachedTickets}</p>
                    </div>
                    <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 p-5 shadow-sm">
                      <h3 className="text-sm font-medium text-base-content/70">Tickets à Risque</h3>
                      <p className="text-2xl font-bold text-warning">{slaStats.atRiskTickets}</p>
                    </div>
                    <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 p-5 shadow-sm">
                      <h3 className="text-sm font-medium text-base-content/70">Résolus dans les Temps</h3>
                      <p className="text-2xl font-bold text-success">{slaStats.resolvedOnTime}</p>
                    </div>
                  </div>
                )}
                <Charts
                  statusDistribution={data.statusDistribution}
                  departmentStats={data.departmentStats}
                  userStats={data.userStats}
                />
              </>
            )}
            {viewMode === "kanban" && <KanbanBoard projectId={undefined} departmentId={undefined} />}
            {viewMode === "gantt" && <GanttChart projectId={undefined} departmentId={undefined} />}
          </div>
        </div>
      </section>
    </div>
  );
};

export default Dashboard;
