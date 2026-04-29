import React from "react";
import { DashboardStats } from "../../types/dashboard";

interface StatsCardsProps {
  stats: DashboardStats;
}

const StatsCards: React.FC<StatsCardsProps> = ({ stats }) => {
  const statCards = [
    {
      title: "Total Tickets",
      value: stats.totalTickets,
      icon: "📊",
      color: "bg-blue-500",
      change: "+12.5%"
    },
    {
      title: "Open Tickets",
      value: stats.openTickets,
      icon: "⚠️",
      color: "bg-yellow-500",
      change: "+2.3%"
    },
    {
      title: "Closed Tickets",
      value: stats.closedTickets,
      icon: "✅",
      color: "bg-green-500",
      change: "+8.7%"
    },
    {
      title: "In Progress",
      value: stats.inProgressTickets,
      icon: "🔄",
      color: "bg-orange-500",
      change: "-1.2%"
    },
    {
      title: "Total Projects",
      value: stats.totalProjects,
      icon: "📁",
      color: "bg-purple-500",
      change: "+5.1%"
    },
    {
      title: "Total Users",
      value: stats.totalUsers,
      icon: "👥",
      color: "bg-pink-500",
      change: "+3.4%"
    },
    {
      title: "Departments",
      value: stats.totalDepartments,
      icon: "🏢",
      color: "bg-indigo-500",
      change: "0%"
    },
    {
      title: "Avg Resolution Time",
      value: `${stats.averageResolutionTime} days`,
      icon: "⏱️",
      color: "bg-teal-500",
      change: "-0.5 days"
    }
  ];

   return (
     <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
       {statCards.map((card, index) => (
         <div key={index} className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm hover:shadow-md transition-shadow">
           <div className="p-5">
             <div className="flex items-center justify-between">
               <div className="flex items-center gap-3">
                 <div className={`flex h-12 w-12 items-center justify-center rounded-full text-white shadow-lg ${card.color}`}>
                   <span className="text-2xl">{card.icon}</span>
                 </div>
                 <div>
                   <h3 className="text-sm font-medium text-base-content/70">{card.title}</h3>
                   <p className="text-2xl font-bold text-base-content">{card.value}</p>
                 </div>
               </div>
               <div className="text-right">
                 <span className="text-sm text-success font-medium">{card.change}</span>
               </div>
             </div>
           </div>
         </div>
       ))}
     </div>
   );
 };

export default StatsCards;