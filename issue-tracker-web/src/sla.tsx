import { useEffect, useState } from "react";
import api from "./api/axios";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";

interface SlaMetrics {
  totalTickets: number;
  breachedTickets: number;
  atRiskTickets: number;
  resolvedOnTime: number;
  resolvedLate: number;
  complianceRate: number;
}

interface TicketSla {
  ticketId: number;
  ticketTitle: string;
  priority: string;
  slaType: string;
  dueAt: string;
  minutesOverdue: number;
  minutesRemaining?: number;
}

const getPriorityBadge = (priority: string): string => {
  switch (priority) {
    case "Critical":
    case "High":
      return "badge-error";
    case "Medium":
      return "badge-warning";
    case "Low":
      return "badge-success";
    default:
      return "badge-ghost";
  }
};

export default function SlaPage() {
  const { t } = useTranslation();
  const [metrics, setMetrics] = useState<SlaMetrics | null>(null);
  const [breached, setBreached] = useState<TicketSla[]>([]);
  const [atRisk, setAtRisk] = useState<TicketSla[]>([]);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState<"overview" | "breached" | "at-risk">("overview");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
const [metricsRes, breachedRes, atRiskRes] = await Promise.all([
        api.get("/sla/metrics"),
        api.get("/sla/breached"),
        api.get("/sla/at-risk"),
      ]);
      setMetrics({
        ...metricsRes.data,
        complianceRate: Number(metricsRes.data.complianceRate),
      });
      setBreached(breachedRes.data);
      setAtRisk(atRiskRes.data);
    } catch (error) {
      toast.error("Erreur chargement SLA");
    } finally {
      setLoading(false);
    }
  };

  const getComplianceColor = (rate: number) => {
    if (rate >= 90) return "text-success";
    if (rate >= 70) return "text-warning";
    return "text-error";
  };

  return (
    <div className="space-y-8">
      <section className="page-section overflow-hidden">
        <div className="border-b border-base-300/60 bg-gradient-to-r from-red-600/10 via-red-800/5 to-gray-500/5 p-6 md:p-8">
          <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
            <div className="space-y-3.5">
              <div className="inline-flex items-center gap-2 rounded-full border border-base-300/70 bg-base-100/80 px-4 py-2 text-xs font-bold uppercase tracking-[0.2em] text-base-content/60 shadow-sm">
                Analytics
              </div>
              <div>
                <h1 className="section-heading">📊 {t('sla.title', { default: 'Suivi SLA' })}</h1>
                <p className="mt-2.5 max-w-2xl text-sm leading-6 text-base-content/65">
                  Suivez les métriques de niveau de service et la conformité des tickets.
                </p>
              </div>
            </div>

            <div className="flex gap-3">
              <button onClick={loadData} className="btn btn-outline">
                🔄 {t('sla.refresh', { default: 'Actualiser' })}
              </button>
            </div>
          </div>
        </div>

        <div className="p-6 md:p-8">
          {loading ? (
            <div className="flex justify-center py-10">
              <span className="loading loading-spinner loading-lg text-primary" />
            </div>
          ) : (
            <>
              <div className="tabs tabs-boxed mb-6">
                <button
                  className={`tab ${tab === "overview" ? "tab-active" : ""}`}
                  onClick={() => setTab("overview")}
                >
                  Vue d'ensemble
                </button>
                <button
                  className={`tab ${tab === "breached" ? "tab-active" : ""}`}
                  onClick={() => setTab("breached")}
                >
                  Dépassés ({breached.length})
                </button>
                <button
                  className={`tab ${tab === "at-risk" ? "tab-active" : ""}`}
                  onClick={() => setTab("at-risk")}
                >
                  À risque ({atRisk.length})
                </button>
              </div>

              {tab === "overview" && metrics && (
                <div className="grid grid-cols-1 gap-5 md:grid-cols-2 lg:grid-cols-4">
                  <div className="stat bg-base-100 border border-base-300/60 rounded-xl p-5 shadow-sm">
                    <div className="stat-title text-sm">{'Total Tickets'}</div>
                    <div className="stat-value text-3xl">{metrics.totalTickets}</div>
                  </div>
                  <div className="stat bg-base-100 border border-base-300/60 rounded-xl p-5 shadow-sm">
                    <div className="stat-title text-sm">{'Taux de conformité'}</div>
                    <div className={`stat-value text-3xl ${getComplianceColor(metrics.complianceRate)}`}>
                      {metrics.complianceRate.toFixed(1)}%
                    </div>
                  </div>
                  <div className="stat bg-base-100 border border-base-300/60 rounded-xl p-5 shadow-sm">
                    <div className="stat-title text-sm">{'Résolus dans les temps'}</div>
                    <div className="stat-value text-success text-3xl">
                      {metrics.resolvedOnTime}
                    </div>
                  </div>
                  <div className="stat bg-base-100 border border-base-300/60 rounded-xl p-5 shadow-sm">
                    <div className="stat-title text-sm">{'Résolus en retard'}</div>
                    <div className="stat-value text-error text-3xl">
                      {metrics.resolvedLate}
                    </div>
                  </div>
                </div>
              )}

              {tab === "breached" && (
                <div className="card bg-base-100 rounded-xl shadow-lg">
                  <div className="card-body">
                    <h2 className="card-title text-lg">Tickets dépassés ({breached.length})</h2>
                    {breached.length === 0 ? (
                      <div className="py-8 text-center text-base-content/60">Aucun ticket dépassé</div>
                    ) : (
                      <div className="overflow-x-auto">
                        <table className="table">
                          <thead>
                            <tr>
                              <th>Ticket ID</th>
                              <th>Title</th>
                              <th>Priority</th>
                              <th>SLA Type</th>
                              <th>Due Date</th>
                              <th>Overdue</th>
                            </tr>
                          </thead>
                          <tbody>
                            {breached.map((t) => (
                              <tr key={t.ticketId}>
                                <td>#{t.ticketId}</td>
                                <td>{t.ticketTitle}</td>
                                <td><span className={`badge ${getPriorityBadge(t.priority)}`}>{t.priority}</span></td>
                                <td>{t.slaType}</td>
                                <td>{new Date(t.dueAt).toLocaleDateString()}</td>
                                <td className="text-error font-semibold">
                                  {Math.floor(t.minutesOverdue / 60)}h {t.minutesOverdue % 60}m
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {tab === "at-risk" && (
                <div className="card bg-base-100 rounded-xl shadow-lg">
                  <div className="card-body">
                    <h2 className="card-title text-lg">Tickets à risque ({atRisk.length})</h2>
                    {atRisk.length === 0 ? (
                      <div className="py-8 text-center text-base-content/60">Aucun ticket à risque</div>
                    ) : (
                      <div className="overflow-x-auto">
                        <table className="table">
                          <thead>
                            <tr>
                              <th>Ticket ID</th>
                              <th>Title</th>
                              <th>Priority</th>
                              <th>SLA Type</th>
                              <th>Due Date</th>
                              <th>Remaining</th>
                            </tr>
                          </thead>
                          <tbody>
                            {atRisk.map((t) => (
                              <tr key={t.ticketId}>
                                <td>#{t.ticketId}</td>
                                <td>{t.ticketTitle}</td>
                                <td><span className={`badge ${getPriorityBadge(t.priority)}`}>{t.priority}</span></td>
                                <td>{t.slaType}</td>
                                <td>{new Date(t.dueAt).toLocaleDateString()}</td>
                                <td className="text-warning font-semibold">
                                  {t.minutesRemaining !== undefined && Math.floor(t.minutesRemaining / 60)}h {t.minutesRemaining ? t.minutesRemaining % 60 : 0}m
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </section>
    </div>
  );
}