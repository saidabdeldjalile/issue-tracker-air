import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import api from "../api/axios";
import { TicketResponse } from "../TicketResponse";
// @ts-ignore - frappe-gantt doesn't have TypeScript types
import Gantt from "frappe-gantt";
import "../lib/frappe-gantt.css";

interface PaginatedTickets {
  content: TicketResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

interface GanttTask {
  id: string;
  name: string;
  start: string;
  end: string;
  progress: number;
  dependencies?: string;
  custom_class?: string;
}

interface GanttChartProps {
  projectId?: number;
  departmentId?: number;
}

export default function GanttChart({ projectId, departmentId }: GanttChartProps) {
  const { t } = useTranslation();
  const containerRef = useRef<HTMLDivElement>(null);
  // @ts-ignore - Gantt type is not properly defined
  const [ganttInstance, setGanttInstance] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState<"Day" | "Week" | "Month">("Day");

  useEffect(() => {
    fetchTickets();
  }, [projectId, departmentId, viewMode]);

  const fetchTickets = async () => {
    try {
      setLoading(true);
      const params: Record<string, any> = {};
      if (projectId) params.projectId = projectId;
      if (departmentId) params.departmentId = departmentId;

      // Request a large page size to get all tickets for the Gantt chart
      const response = await api.get<PaginatedTickets>("/tickets/search", { 
        params: { ...params, size: 1000 } 
      });
      
      if (containerRef.current) {
        // Clear previous Gantt
        containerRef.current.innerHTML = '';
        
        // Access the content array from the paginated response
        const tickets = response.data.content || [];
        const tasks: GanttTask[] = tickets
          .filter(ticket => ticket.status !== "Deleted")
          .map((ticket, index) => {
            const startDate = new Date(ticket.createdAt);
            const endDate = ticket.modifiedAt ? new Date(ticket.modifiedAt) : new Date();
            
            // Calculate progress based on status
            let progress = 0;
            switch (ticket.status) {
              case "Open": progress = 0; break;
              case "InProgress": progress = 50; break;
              case "Resolved": progress = 90; break;
              case "Closed": progress = 100; break;
              default: progress = 25;
            }

            return {
              id: `task-${ticket.id}`,
              name: `#${ticket.id} - ${ticket.title}`,
              start: startDate.toISOString().split('T')[0],
              end: endDate.toISOString().split('T')[0],
              progress: progress,
              dependencies: index > 0 ? `task-${tickets[index - 1].id}` : undefined,
              custom_class: getStatusColor(ticket.status),
            };
          });

        if (tasks.length > 0 && containerRef.current) {
          const gantt = new Gantt(containerRef.current, tasks, {
            header_height: 50,
            column_width: 30,
            step: 24,
            view_modes: ["Quarter Day", "Half Day", "Day", "Week", "Month"],
            bar_height: 30,
            bar_corner_radius: 3,
            arrow_curve: 5,
            padding: 18,
            view_mode: viewMode as any,
            date_format: "YYYY-MM-DD",
            language: "fr",
            custom_popup_html: function(task: any) {
              return `
                <div class="card bg-base-100 p-2" style="min-width: 200px;">
                  <h5 class="font-bold">${task.name}</h5>
                  <p class="text-sm">Progress: ${task.progress}%</p>
                  <p class="text-xs text-gray-500">${task.start} → ${task.end}</p>
                </div>
              `;
            },
          });

          setGanttInstance(gantt);
        }
      }
    } catch (error) {
      console.error("Error fetching tickets for Gantt:", error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case "Open": return "bar-blue";
      case "InProgress": return "bar-yellow";
      case "Resolved": return "bar-green";
      case "Closed": return "bar-gray";
      default: return "bar-orange";
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">{t("gantt.title")}</h2>
        <div className="join">
          {(["Day", "Week", "Month"] as const).map((mode) => (
            <button
              key={mode}
              type="button"
              className={`btn btn-sm join-item ${viewMode === mode ? "btn-primary" : "btn-outline"}`}
              onClick={() => setViewMode(mode)}
            >
              {mode}
            </button>
          ))}
        </div>
      </div>

      <div className="overflow-x-auto">
        <div ref={containerRef} className="gantt-container"></div>
      </div>

      <style>{`
        .gantt-container {
          min-height: 400px;
          overflow-x: auto;
        }
        .bar-blue { fill: #3b82f6; }
        .bar-yellow { fill: #eab308; }
        .bar-green { fill: #22c55e; }
        .bar-gray { fill: #6b7280; }
        .bar-orange { fill: #f97316; }
      `}</style>
    </div>
  );
}
