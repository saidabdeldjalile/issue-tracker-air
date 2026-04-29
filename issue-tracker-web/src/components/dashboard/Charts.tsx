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
         <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm">
           <div className="p-5">
             <h3 className="text-lg font-bold mb-4">Ticket Status Distribution</h3>
             <p className="text-base-content/50">No data available</p>
           </div>
         </div>
       );
     }

     return (
       <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm">
         <div className="p-5">
           <h3 className="text-lg font-bold mb-4">Ticket Status Distribution</h3>
           <div className="space-y-2">
             {safeStatusDist.map((item, index) => {
               const percentage = total > 0 ? (item.count / total) * 100 : 0;
               const colors = ['bg-blue-500', 'bg-yellow-500', 'bg-green-500', 'bg-orange-500', 'bg-purple-500'];
               const color = colors[index % colors.length];

               return (
                 <div key={index} className="flex items-center justify-between">
                   <div className="flex items-center gap-2">
                     <div className={`h-3 w-3 rounded-full ${color}`}></div>
                     <span className="text-sm text-base-content">{item.status}</span>
                   </div>
                   <div className="flex items-center gap-2">
                     <div className="h-2 w-24 rounded-full bg-base-200">
                       <div
                         className={`h-2 rounded-full ${color}`}
                         style={{ width: `${percentage}%` }}
                       ></div>
                     </div>
                     <span className="text-sm text-base-content/70">{item.count}</span>
                   </div>
                 </div>
               );
             })}
           </div>
         </div>
       </div>
     );
   };

   const DepartmentChart: React.FC = () => {
     return (
       <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm">
         <div className="p-5">
           <h3 className="text-lg font-bold mb-4">Department Performance</h3>
           <div className="space-y-3">
             {safeDeptStats.slice(0, 5).map((dept, index) => {
               const colors = ['bg-blue-500', 'bg-green-500', 'bg-purple-500', 'bg-pink-500', 'bg-indigo-500'];
               const color = colors[index % colors.length];

               return (
                 <div key={dept.departmentName || index} className="flex items-center justify-between">
                   <div className="flex items-center gap-3">
                     <div className={`h-3 w-3 rounded-full ${color}`}></div>
                     <div>
                       <p className="font-medium text-base-content">{dept.departmentName || 'Unknown'}</p>
                       <p className="text-xs text-base-content/60">{dept.ticketCount || 0} tickets</p>
                     </div>
                   </div>
                   <div className="text-right">
                     <p className="text-sm text-base-content">{(dept.averageResolutionTime || 0).toFixed(1)} days avg</p>
                     <p className="text-xs text-success">{dept.closedCount || 0} resolved</p>
                   </div>
                 </div>
               );
             })}
           </div>
         </div>
       </div>
     );
   };

   const UserChart: React.FC = () => {
     return (
       <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm">
         <div className="p-5">
           <h3 className="text-lg font-bold mb-4">Top Performers</h3>
           <div className="space-y-3">
             {safeUserStats.slice(0, 5).map((user, index) => {
               const colors = ['bg-blue-500', 'bg-green-500', 'bg-purple-500', 'bg-pink-500', 'bg-indigo-500'];
               const color = colors[index % colors.length];

               return (
                 <div key={user.userName || index} className="flex items-center justify-between">
                   <div className="flex items-center gap-3">
                     <div className={`h-3 w-3 rounded-full ${color}`}></div>
                     <div>
                       <p className="font-medium text-base-content">{user.userName}</p>
                       <p className="text-xs text-base-content/60">{user.ticketCount || 0} tickets</p>
                     </div>
                   </div>
                   <div className="text-right">
                     <p className="text-sm text-base-content">{user.ticketCount || 0} tickets</p>
                     <p className="text-xs text-success">{(user.averageResolutionTime || 0).toFixed(1)} days avg</p>
                     </div>
                 </div>
               );
             })}
           </div>
         </div>
       </div>
     );
   };

   return (
     <div className="grid grid-cols-1 gap-5 lg:grid-cols-3">
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