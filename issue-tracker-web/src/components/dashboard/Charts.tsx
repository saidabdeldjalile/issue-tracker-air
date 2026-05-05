import React from "react";
import { TicketStatusDistribution, DepartmentStats, UserStats } from "../../types/dashboard";

interface ChartsProps {
  statusDistribution?: TicketStatusDistribution[];
  departmentStats?: DepartmentStats[];
  userStats?: UserStats[];
}

const Charts: React.FC<ChartsProps> = ({ 
  statusDistribution = [],
  departmentStats = [],
  userStats = []
}) => {
  // Defensive checks
  const safeStatusDist = Array.isArray(statusDistribution) ? statusDistribution : [];
  const safeDeptStats = Array.isArray(departmentStats) ? departmentStats : [];
  const safeUserStats = Array.isArray(userStats) ? userStats : [];

   // Simple chart components using CSS and basic HTML
   const StatusChart: React.FC = () => {
     const total = safeStatusDist.reduce((sum, item) => sum + (item?.count || 0), 0);
     if (safeStatusDist.length === 0) {
       return (
         <div className="overflow-hidden rounded-2xl border border-base-300/60 bg-base-100/80 p-6 shadow-sm backdrop-blur-xl h-full flex flex-col items-center justify-center min-h-[300px]">
           <svg className="w-16 h-16 text-base-content/20 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" /></svg>
           <h3 className="text-sm font-semibold text-base-content/60 uppercase tracking-wider mb-2">Status Distribution</h3>
           <p className="text-base-content/40 text-sm">No data available</p>
         </div>
       );
     }

     return (
       <div className="overflow-hidden rounded-2xl border border-base-300/60 bg-base-100/80 p-6 shadow-sm backdrop-blur-xl hover:shadow-md transition-shadow h-full flex flex-col">
         <div className="flex items-center justify-between mb-6">
           <h3 className="text-sm font-semibold text-base-content/70 uppercase tracking-wider flex items-center gap-2">
             <svg className="w-4 h-4 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z" /></svg>
             Status Distribution
           </h3>
           <span className="text-xs font-medium bg-base-200 text-base-content/70 px-2 py-1 rounded-full">{total} total</span>
         </div>
         <div className="space-y-5 flex-1 justify-center flex flex-col">
           {safeStatusDist.map((item, index) => {
             const percentage = total > 0 ? (item.count / total) * 100 : 0;
             const colors = ['from-blue-500 to-cyan-400', 'from-amber-400 to-orange-500', 'from-emerald-400 to-green-500', 'from-indigo-500 to-purple-500', 'from-pink-500 to-rose-500'];
             const bgColors = ['bg-blue-500', 'bg-amber-400', 'bg-emerald-400', 'bg-indigo-500', 'bg-pink-500'];
             const color = colors[index % colors.length];
             const bgColor = bgColors[index % bgColors.length];

             return (
               <div key={index} className="group">
                 <div className="flex items-center justify-between mb-1.5">
                   <div className="flex items-center gap-2">
                     <div className={`h-2.5 w-2.5 rounded-full ${bgColor} shadow-sm shadow-${bgColor.split('-')[1]}-500/50`}></div>
                     <span className="text-sm font-medium text-base-content/80 group-hover:text-base-content transition-colors">{item.status}</span>
                   </div>
                   <div className="flex items-center gap-2">
                     <span className="text-sm font-bold text-base-content">{item.count}</span>
                     <span className="text-xs text-base-content/50 w-8 text-right">{percentage.toFixed(0)}%</span>
                   </div>
                 </div>
                 <div className="h-2 w-full rounded-full bg-base-200/50 overflow-hidden">
                   <div
                     className={`h-full rounded-full bg-gradient-to-r ${color} transition-all duration-1000 ease-out`}
                     style={{ width: `${percentage}%` }}
                   ></div>
                 </div>
               </div>
             );
           })}
         </div>
       </div>
     );
   };

   const DepartmentChart: React.FC = () => {
     return (
       <div className="overflow-hidden rounded-2xl border border-base-300/60 bg-base-100/80 p-6 shadow-sm backdrop-blur-xl hover:shadow-md transition-shadow h-full flex flex-col">
         <div className="flex items-center justify-between mb-6">
           <h3 className="text-sm font-semibold text-base-content/70 uppercase tracking-wider flex items-center gap-2">
             <svg className="w-4 h-4 text-secondary" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>
             Department Performance
           </h3>
         </div>
         <div className="space-y-4 flex-1">
           {safeDeptStats.length === 0 ? (
             <div className="h-full flex flex-col items-center justify-center text-base-content/40 text-sm">
               No data available
             </div>
           ) : safeDeptStats.slice(0, 5).map((dept, index) => {
             const colors = ['bg-blue-500', 'bg-emerald-400', 'bg-purple-500', 'bg-rose-500', 'bg-amber-400'];
             const color = colors[index % colors.length];

             return (
               <div key={dept.departmentName || index} className="flex items-center justify-between p-3 rounded-xl hover:bg-base-200/50 transition-colors group">
                 <div className="flex items-center gap-3">
                   <div className={`flex h-10 w-10 items-center justify-center rounded-lg ${color} bg-opacity-10 text-${color.split('-')[1]}-600 group-hover:scale-110 transition-transform`}>
                     <span className="font-bold text-sm">{dept.departmentName?.charAt(0) || '?'}</span>
                   </div>
                   <div>
                     <p className="font-semibold text-base-content text-sm">{dept.departmentName || 'Unknown'}</p>
                     <p className="text-xs text-base-content/60">{dept.ticketCount || 0} total tickets</p>
                   </div>
                 </div>
                 <div className="text-right">
                   <p className="text-sm font-bold text-base-content">{(dept.averageResolutionTime || 0).toFixed(1)}d <span className="text-xs font-normal text-base-content/50">avg</span></p>
                   <p className="text-xs font-medium text-success">{dept.closedCount || 0} resolved</p>
                 </div>
               </div>
             );
           })}
         </div>
       </div>
     );
   };

   const UserChart: React.FC = () => {
     return (
       <div className="overflow-hidden rounded-2xl border border-base-300/60 bg-base-100/80 p-6 shadow-sm backdrop-blur-xl hover:shadow-md transition-shadow h-full flex flex-col">
         <div className="flex items-center justify-between mb-6">
           <h3 className="text-sm font-semibold text-base-content/70 uppercase tracking-wider flex items-center gap-2">
             <svg className="w-4 h-4 text-accent" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" /></svg>
             Top Performers
           </h3>
         </div>
         <div className="space-y-4 flex-1">
           {safeUserStats.length === 0 ? (
             <div className="h-full flex flex-col items-center justify-center text-base-content/40 text-sm">
               No data available
             </div>
           ) : safeUserStats.slice(0, 5).map((user, index) => {
             const colors = ['bg-amber-400', 'bg-slate-300', 'bg-orange-400', 'bg-blue-400', 'bg-purple-400'];
             const color = colors[index % colors.length];

             return (
               <div key={user.userName || index} className="flex items-center justify-between p-3 rounded-xl hover:bg-base-200/50 transition-colors group">
                 <div className="flex items-center gap-3">
                   <div className="relative">
                     <div className="h-10 w-10 rounded-full bg-base-300 flex items-center justify-center text-base-content/70 font-bold group-hover:scale-110 transition-transform shadow-inner">
                       {user.userName?.charAt(0).toUpperCase() || '?'}
                     </div>
                     {index < 3 && (
                       <div className={`absolute -top-1 -right-1 h-4 w-4 rounded-full ${color} border-2 border-base-100 flex items-center justify-center`}>
                         <span className="text-[8px] font-bold text-white">{index + 1}</span>
                       </div>
                     )}
                   </div>
                   <div>
                     <p className="font-semibold text-base-content text-sm">{user.userName}</p>
                     <p className="text-xs text-base-content/60">{user.ticketCount || 0} tickets handled</p>
                   </div>
                 </div>
                 <div className="text-right">
                   <p className="text-sm font-bold text-success flex items-center justify-end gap-1">
                     <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" /></svg>
                     {(user.averageResolutionTime || 0).toFixed(1)}d
                   </p>
                   <p className="text-[10px] text-base-content/40 uppercase tracking-wider mt-0.5">Avg Time</p>
                 </div>
               </div>
             );
           })}
         </div>
       </div>
     );
   };

   return (
     <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
       <div className="lg:col-span-1">
         <StatusChart />
       </div>
       <div className="lg:col-span-1">
         <DepartmentChart />
       </div>
       <div className="lg:col-span-1">
         <UserChart />
       </div>
     </div>
   );
 };

export default Charts;