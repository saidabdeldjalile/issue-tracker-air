import useSWR from "swr";
import { AxiosResponse } from "axios";
import api from "./api/axios";
import { TicketDetails } from "./ticketdetails";
import { TicketBody } from "./ticketbody";
import { TicketResponse } from "./TicketResponse";
import { useParams } from "react-router-dom";
import { useMemo, useState } from "react";
import TicketScreenshots from "./components/ticket/TicketScreenshots";

export function Ticket({ id: propid }: { id?: number }) {
  const { id: paramid } = useParams();
  const id = propid ?? paramid;
  const url = `/tickets/${id}`;
  const key = `ticket${id}`;
  const [activeTab, setActiveTab] = useState<"discussion" | "details" | "screenshots">("discussion");

  const ticketId = useMemo(() => {
    const asNumber = typeof id === "string" ? Number(id) : id;
    return Number.isFinite(asNumber) ? (asNumber as number) : undefined;
  }, [id]);

  const fetcher = (url: string) => {
    return api
      .get<TicketResponse>(url)
      .then((res: AxiosResponse<TicketResponse>) => {
        return res.data;
      });
  };

  const {
    data: ticket,
    error,
    isLoading,
  } = useSWR<TicketResponse | null>(key, () => fetcher(url));

   if (error)
     return (
       <div className="flex min-h-[60vh] items-center justify-center">
         <span className="loading loading-spinner loading-lg text-primary" />
       </div>
     );
   if (isLoading)
     return (
       <div className="flex min-h-[60vh] items-center justify-center">
         <span className="loading loading-spinner loading-lg text-primary" />
       </div>
     );

   return (
     <div className="space-y-6">
       <section className="page-section overflow-hidden">
         <div className="p-4 md:p-6">
           <div className="flex flex-col gap-5">
             <div className="flex flex-wrap items-center justify-between gap-3">
               <div className="tabs tabs-boxed bg-base-200/60 p-1">
                 <button
                   type="button"
                   className={`tab ${activeTab === "discussion" ? "tab-active" : ""}`}
                   onClick={() => setActiveTab("discussion")}
                 >
                   Discussion
                 </button>
                 <button
                   type="button"
                   className={`tab ${activeTab === "details" ? "tab-active" : ""}`}
                   onClick={() => setActiveTab("details")}
                 >
                   Détails
                 </button>
                 <button
                   type="button"
                   className={`tab ${activeTab === "screenshots" ? "tab-active" : ""}`}
                   onClick={() => setActiveTab("screenshots")}
                 >
                   Captures
                 </button>
               </div>
             </div>

             {activeTab === "discussion" && <TicketBody ticket={ticket} />}
             {activeTab === "details" && <TicketDetails ticket={ticket} />}
             {activeTab === "screenshots" && <TicketScreenshots ticketId={ticketId} />}
           </div>
         </div>
       </section>
     </div>
   );
}
