import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import api from "../api/axios";
import { TicketResponse } from "../TicketResponse";
import { toast } from "react-toastify";

interface KanbanColumn {
  status: string;
  tickets: TicketResponse[];
}

interface KanbanData {
  [key: string]: TicketResponse[];
}

interface KanbanBoardProps {
  projectId?: number;
  departmentId?: number;
}

const STATUS_CONFIG = [
  { key: "Open", label: "Open", labelFr: "Ouvert", color: "bg-blue-500" },
  { key: "InProgress", label: "In Progress", labelFr: "En cours", color: "bg-yellow-500" },
  { key: "Resolved", label: "Resolved", labelFr: "Résolu", color: "bg-green-500" },
  { key: "Closed", label: "Closed", labelFr: "Fermé", color: "bg-gray-500" },
  { key: "Pending", label: "Pending", labelFr: "En attente", color: "bg-orange-500" },
  { key: "Reopened", label: "Reopened", labelFr: "Rouvert", color: "bg-purple-500" },
];

export default function KanbanBoard({ projectId, departmentId }: KanbanBoardProps) {
  const { t, i18n } = useTranslation();
  const [columns, setColumns] = useState<KanbanColumn[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchKanbanData = async () => {
    try {
      setLoading(true);
      const params: Record<string, any> = {};
      if (projectId) params.projectId = projectId;
      if (departmentId) params.departmentId = departmentId;

      const response = await api.get<KanbanData>("/tickets/kanban", { params });
      
      const kanbanColumns = STATUS_CONFIG.map((config) => ({
        status: config.key,
        tickets: response.data[config.key] || [],
      }));
      
      setColumns(kanbanColumns);
    } catch (error) {
      console.error("Error fetching kanban data:", error);
      toast.error(t("message.error"));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchKanbanData();
  }, [projectId, departmentId]);

  const getLabel = (config: typeof STATUS_CONFIG[0]) => {
    return i18n.language === 'fr' ? config.labelFr : config.label;
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
      <h2 className="text-2xl font-bold mb-4">{t("kanban.title")}</h2>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 pb-4">
        {columns.map((column) => (
          <div
            key={column.status}
            className="bg-base-200 rounded-lg p-3"
          >
            <div className="flex items-center gap-2 mb-3">
              <div className={`w-3 h-3 rounded-full ${STATUS_CONFIG.find(c => c.key === column.status)?.color || "bg-gray-500"}`}></div>
              <h3 className="font-semibold">
                {getLabel(STATUS_CONFIG.find(c => c.key === column.status)!)}
              </h3>
              <span className="badge badge-sm">{column.tickets.length}</span>
            </div>
            
            <div className="space-y-2">
              {column.tickets.map((ticket) => (
                <div
                  key={ticket.id.toString()}
                  className="card bg-base-100 shadow-sm cursor-pointer hover:shadow-md transition-shadow"
                >
                  <div className="card-body p-3">
                    <h4 className="font-medium text-sm line-clamp-2">{ticket.title}</h4>
                    <div className="flex justify-between items-center mt-2">
                      <span className={`badge badge-xs ${
                        ticket.priority === "High" || ticket.priority === "Critical" || ticket.priority === "Urgent"
                          ? "badge-error"
                          : ticket.priority === "Medium"
                          ? "badge-warning"
                          : "badge-info"
                      }`}>
                        {ticket.priority}
                      </span>
                      <span className="text-xs text-base-content/60">
                        #{ticket.id}
                      </span>
                    </div>
                    {ticket.assigned && (
                      <div className="text-xs text-base-content/70 mt-1">
                        👤 {ticket.assigned.firstName}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}