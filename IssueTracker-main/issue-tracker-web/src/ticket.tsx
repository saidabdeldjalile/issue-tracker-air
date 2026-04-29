import useSWR from "swr";
import axios, { AxiosResponse } from "axios";
import { TicketDetails } from "./ticketdetails";
import { TicketBody } from "./ticketbody";
import { TicketResponse } from "./TicketResponse";
import { useParams } from "react-router-dom";
import config from "./config";

export function Ticket({ id: propid }: { id?: number }) {
  const { id: paramid } = useParams();
  const id = propid ?? paramid;
  const url = `${config.apiUrl}/api/tickets/${id}`;
  const key = `ticket${id}`;

  const fetcher = (url: string) => {
    return axios
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
      <div className="flex h-screen items-center justify-center">
        <progress className="progress w-56"></progress>
      </div>
    );
  if (isLoading)
    return (
      <div className="flex h-screen items-center justify-center">
        <progress className="progress w-56"></progress>
      </div>
    );

  return (
    <>
      {/* <NavBar /> */}
      <div className=" flex">
        <div className="h-screen w-14 flex-1 basis-2/3 ">
          <TicketBody ticket={ticket} />
        </div>
        <div className="h-screen w-6 flex-auto basis-1/3 ">
          <TicketDetails ticket={ticket} />
        </div>
      </div>
    </>
  );
}
