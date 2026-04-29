import axios, { AxiosResponse } from "axios";
import useSWR, { mutate } from "swr";
import { Project, TicketResponse } from "./TicketResponse";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import config from "./config";

interface TicketListProps {
  project: Project;
}

export default function TicketList({ project }: TicketListProps) {
  const { id, name } = project;
  const url = `${config.apiUrl}/api/projects/${id}/tickets`;
  const key = `ticket`;

  const fetcher = (url: string) => {
    return axios
      .get<TicketResponse[]>(url)
      .then((res: AxiosResponse<TicketResponse[]>) => {
        return res.data;
      });
  };

  const {
    data: tickets,
    error,
    isLoading,
  } = useSWR<TicketResponse[] | null>(key, () => fetcher(url));

  function handledelete(e: any) {
    if (window.confirm("Are you sure you want to delete this ticket?")) {
      axios.delete(`${config.apiUrl}/api/tickets/${e}`).then(() => {
        toast.success("Ticket deleted successfully");
        mutate("ticket");
      });
    }
  }

  if (error)
    return (
      <div className="flex h-screen items-center justify-center">
        <progress className="progress w-56"></progress>
      </div>
    );
  if (isLoading)
    return (
      <div className="flex h-screen items-center justify-center">
        Loading, Please Wait
        <progress className="progress w-56"></progress>
      </div>
    );

  // function deleteTicket(id: number) {
  //   axios.delete(`${config.apiUrl}/api/tickets/${id}`).then((res) => {
  //     console.log(res);
  //   });
  // }

  return (
    <>
      <div className="breadcrumbs ml-2 mt-1 text-sm">
        <ul>
          <li>
            <Link to="/projects">Projects</Link>
          </li>
          <li>
            <span className=" opacity-60">#</span>
            {id} {name}
          </li>
          <li>
            <Link to={`/projects/${id}/tickets`}>Issues</Link>
          </li>
        </ul>
      </div>
      <div className="mx-6 overflow-x-auto">
        <table className="table">
          {/* head */}
          <thead>
            <tr>
              <th>Ticket ID</th>
              <th>Title</th>
              <th>Status</th>
              <th>Priority</th>
              <th>Created At</th>
              <th>Last Modified</th>
              <th>Reported By</th>
              <th>Assigned To</th>
            </tr>
          </thead>
          <tbody>
            {tickets?.map((ticket) => (
              <tr key={ticket.id} className="hover">
                <th>
                  <Link to={`/tickets/${ticket.id}`}>
                    <span className=" opacity-60">#</span>
                    {ticket.id}
                  </Link>
                </th>
                <td>
                  <Link
                    to={`/projects/${ticket.project.id}/tickets/${ticket.id}`}
                  >
                    {ticket.title}
                  </Link>
                </td>
                <td>{ticket.status}</td>
                <td>{ticket.priority}</td>
                <td>
                  {ticket?.createdAt
                    ? new Date(ticket.createdAt).toLocaleDateString()
                    : "N/A"}
                </td>
                <td>
                  {ticket?.modifiedAt
                    ? new Date(ticket.modifiedAt).toLocaleDateString()
                    : "N/A"}
                </td>
                <td>
                  {/**projects/${pid}/ */}
                  {ticket.created.firstName}
                </td>
                <td>{ticket.assigned ? ticket.assigned.firstName : "None"}</td>
                <td className="w-6" onClick={() => handledelete(ticket.id)}>
                  <svg
                    style={{ color: "red", width: "16px", height: "16px" }}
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 448 512"
                    className="cursor-pointer"
                  >
                    <path
                      d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"
                      fill="red"
                    ></path>
                  </svg>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
