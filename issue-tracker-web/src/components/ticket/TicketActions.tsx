import React from 'react';
import { TicketResponse } from '../../TicketResponse';
import useAuth from '../../hooks/useAuth';
import { toast } from 'react-toastify';
import api from '../../api/axios';

interface TicketActionsProps {
  ticket: TicketResponse | null | undefined;
  onUpdate?: () => void;
}

/* ── Permission badge ── */
function PermBadge({ active, label, sub, color }: {
  active: boolean;
  label: string;
  sub?: string;
  color: string;
}) {
  return (
    <div className={`flex items-center gap-2 px-3 py-2 rounded-xl border text-xs font-medium transition-all ${
      active
        ? `border-${color}-200 dark:border-${color}-800 bg-${color}-50 dark:bg-${color}-900/20 text-${color}-700 dark:text-${color}-300`
        : 'border-gray-100 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/60 text-gray-400 dark:text-gray-600'
    }`}>
      <span className="text-sm leading-none">{active ? '✅' : '❌'}</span>
      <span>{label}</span>
      {active && sub && (
        <span className={`ml-auto text-[9px] font-bold uppercase tracking-widest text-${color}-400`}>
          {sub}
        </span>
      )}
    </div>
  );
}

/* ── Transition row ── */
function TransRow({ from, to, icon, permission, highlight }: {
  from: string; to: string; icon: string; permission: string; highlight?: boolean;
}) {
  return (
    <div className={`flex items-center gap-1.5 px-2 py-1.5 rounded-lg text-[10px] transition-colors ${
      highlight
        ? 'bg-red-50 dark:bg-red-950/20 border border-red-100 dark:border-red-900'
        : 'bg-gray-50 dark:bg-gray-800/60'
    }`}>
      <span className="px-1.5 py-0.5 rounded bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 font-medium whitespace-nowrap">
        {from}
      </span>
      <span className="text-gray-400 flex-shrink-0">{icon}</span>
      <span className="px-1.5 py-0.5 rounded bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300 font-medium whitespace-nowrap">
        {to}
      </span>
      <span className="ml-auto text-gray-400 dark:text-gray-500 whitespace-nowrap flex-shrink-0">
        {permission}
      </span>
    </div>
  );
}

export const TicketActions: React.FC<TicketActionsProps> = ({ ticket, onUpdate }) => {
  const { auth } = useAuth();

  const handleStatusChange = async (newStatus: string) => {
    try {
      await api.patch(`/tickets/${ticket?.id}`, {
        status: newStatus,
        modifierEmail: auth?.email,
        modifierRole: auth?.role,
      });
      toast.success(`Statut mis à jour : ${newStatus}`);
      if (onUpdate) onUpdate();
    } catch (error: any) {
      toast.error(error.response?.data?.message || error.message || "Erreur de mise à jour");
    }
  };

  const isReporter = ticket?.created?.email === auth?.email;
  const isAssigned = ticket?.assigned?.email === auth?.email;
  const isAdmin    = auth?.role === 'ADMIN';

  /* ── Action button ── */
  const Btn = ({ onClick, color, children }: {
    onClick: () => void; color: string; children: React.ReactNode;
  }) => (
    <button
      onClick={onClick}
      className={`w-full px-3 py-2 text-xs font-semibold text-white rounded-xl transition-all active:scale-95
        bg-${color}-600 hover:bg-${color}-700 dark:bg-${color}-500 dark:hover:bg-${color}-600`}
    >
      {children}
    </button>
  );

  /* ── Info chip (no permission) ── */
  const Info = ({ color, children }: { color: string; children: React.ReactNode }) => (
    <div className={`text-xs text-center px-3 py-2 rounded-xl font-medium
      bg-${color}-50 text-${color}-600 dark:bg-${color}-900/20 dark:text-${color}-300`}>
      {children}
    </div>
  );

  const renderActions = () => {
    switch (ticket?.status) {
      case 'Open':
        return isAdmin
          ? <Btn onClick={() => handleStatusChange('ToDo')} color="red">✅ Valider le ticket</Btn>
          : <Info color="amber">⏳ En attente de validation Admin</Info>;

      case 'ToDo':
        return isAssigned
          ? <Btn onClick={() => handleStatusChange('InProgress')} color="green">🚀 Démarrer le travail</Btn>
          : <Info color="amber">⏳ En attente que le Support démarre</Info>;

      case 'InProgress':
        return isAssigned
          ? <div className="flex flex-col gap-1.5">
              <Btn onClick={() => handleStatusChange('WaitingForUserResponse')} color="yellow">⏳ Attendre réponse</Btn>
              <Btn onClick={() => handleStatusChange('Done')} color="red">✅ Terminer le travail</Btn>
            </div>
          : <Info color="red">🔄 En cours de traitement par le Support</Info>;

      case 'WaitingForUserResponse':
        return isAssigned
          ? <Btn onClick={() => handleStatusChange('InProgress')} color="red">🔄 Reprendre le traitement</Btn>
          : <Info color="yellow">⏳ En attente de réponse utilisateur</Info>;

      case 'Done':
        return (isAdmin || isReporter)
          ? <Btn onClick={() => handleStatusChange('Closed')} color="gray">📦 Archiver le ticket</Btn>
          : <Info color="amber">⏳ En attente d'archivage (Admin / Reporter)</Info>;

      case 'Closed':
        return <Info color="green">✅ Ticket archivé — aucune action</Info>;

      default:
        return <Info color="gray">Statut inconnu</Info>;
    }
  };

  const transitions = [
    { from: 'Open',       to: 'To Do',      permission: 'Admin',          icon: '→', match: ticket?.status === 'Open'       },
    { from: 'To Do',      to: 'In Progress', permission: 'Assigné',        icon: '→', match: ticket?.status === 'ToDo'       },
    { from: 'In Progress',to: 'En attente',  permission: 'Assigné',        icon: '→', match: ticket?.status === 'InProgress' },
    { from: 'En attente', to: 'In Progress', permission: 'Assigné',        icon: '←', match: ticket?.status === 'WaitingForUserResponse' },
    { from: 'In Progress',to: 'Done',        permission: 'Assigné',        icon: '→', match: ticket?.status === 'InProgress' },
    { from: 'Done',       to: 'Closed',      permission: 'Admin/Reporter', icon: '→', match: ticket?.status === 'Done'       },
  ];

  return (
    <div className="flex flex-col gap-3">

      {/* ── Actions contextuelles ── */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200 dark:border-gray-800 overflow-hidden">
        <div className="flex items-center justify-between px-4 py-2.5 border-b border-gray-100 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50">
          <span className="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-gray-500">
            🎯 Actions
          </span>
          <span className="text-[10px] font-mono text-gray-400 dark:text-gray-500">
            {ticket?.status}
          </span>
        </div>
        <div className="p-3">
          {renderActions()}
        </div>
      </div>

      {/* ── Vos permissions ── */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200 dark:border-gray-800 overflow-hidden">
        <div className="px-4 py-2.5 border-b border-gray-100 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50">
          <span className="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-gray-500">
            👤 Vos Permissions
          </span>
        </div>
        <div className="p-3 flex flex-col gap-1.5">
          <PermBadge active={isAdmin}    label={auth?.role || 'N/A'} sub="Administrateur" color="red"   />
          <PermBadge active={isReporter} label="Reporter"            sub="Créateur"        color="green"  />
          <PermBadge active={isAssigned} label="Assigné"             sub="Support tech."   color="red" />
        </div>
      </div>

      {/* ── Guide des transitions ── */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200 dark:border-gray-800 overflow-hidden">
        <div className="px-4 py-2.5 border-b border-gray-100 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50">
          <span className="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-gray-500">
            📋 Guide des Transitions
          </span>
        </div>
        <div className="p-3 flex flex-col gap-1">
          {transitions.map((t, i) => (
            <TransRow key={i} from={t.from} to={t.to} icon={t.icon} permission={t.permission} highlight={t.match} />
          ))}
        </div>
      </div>

    </div>
  );
};