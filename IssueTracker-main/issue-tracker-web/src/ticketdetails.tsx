import { useEffect, useState } from "react";
import { TicketResponse, User } from "./TicketResponse";
import axios, { AxiosResponse } from "axios";
import { toast } from "react-toastify";
import useAuth from "./hooks/useAuth";
import config from "./config";

type TicketBodyProps = {
  ticket: TicketResponse | null | undefined;
};

export function TicketDetails({ ticket }: TicketBodyProps) {
  const [status, setStatus] = useState(ticket?.status);
  const [priority, setPriority] = useState(ticket?.priority);
  const [assigned, setAssigned] = useState(ticket?.assigned?.email ?? "");
  const [users, setUsers] = useState<User[]>([]);
  const { auth } = useAuth();

  useEffect(() => {
    axios
      .get(`${config.apiUrl}/api/users`)
      .then((res: AxiosResponse) => {
        setUsers(res.data);
      });
  }, []);

  function updateAssigned(e: React.ChangeEvent<HTMLSelectElement>) {
    setAssigned(e.target.value);
    axios
      .post(`${config.apiUrl}/api/tickets/${ticket?.id}/assign`, {
        email: e.target.value,
      })
      .then(() => {
        toast.success(
          "Ticket(#" + ticket?.id + ") assignee updated successfully",
        );
      });
  }

  function updateStatus(e: React.ChangeEvent<HTMLSelectElement>) {
    setStatus(e.target.value);
    axios
      .post(`${config.apiUrl}/api/tickets/${ticket?.id}`, {
        status: e.target.value,
      })
      .then(() => {
        toast.success(
          "Ticket(#" + ticket?.id + ") status updated successfully",
        );
      });
  }

  function updatePriority(e: React.ChangeEvent<HTMLSelectElement>) {
    setPriority(e.target.value);
    axios
      .post(`${config.apiUrl}/api/tickets/${ticket?.id}`, {
        priority: e.target.value,
      })
      .then(() => {
        toast.success(
          "Ticket(#" + ticket?.id + ") priority updated successfully",
        );
      });
  }
  return (
    <>
      <div className=" p-6">
        <div className="collapse  collapse-open bg-base-200">
          <input type="checkbox" />
          <div className="collapse-title bg-base-300 text-base-content peer-checked:bg-base-300 peer-checked:text-base-content">
            Details
          </div>
          <div className="collapse-content bg-base-200 text-base-content peer-checked:bg-base-200 peer-checked:text-base-content">
            <div className="flex">
              <div>
                <div className=" inline-flex gap-2 p-3">
                  <select
                    value={status}
                    onChange={(e) => updateStatus(e)}
                    disabled={auth.email !== assigned}
                    className="select select-bordered select-sm w-full max-w-xs"
                  >
                    <option value="Open">OPEN</option>
                    <option value="ToDo">TO DO</option>
                    <option value="InProgress">IN PROGRESS</option>
                    <option value="Done">DONE</option>
                    <option value="Closed">CLOSED</option>
                  </select>
                  <select
                    value={priority}
                    onChange={(e) => updatePriority(e)}
                    disabled={auth.email !== assigned}
                    className="select select-bordered select-sm w-full max-w-xs"
                  >
                    <option value="High">HIGH</option>
                    <option value="Medium">MEDIUM</option>
                    <option value="Low">LOW</option>
                    <option value="Critical">CRITICAL</option>
                  </select>
                  {/* <div className="bg-error text-error-content mr-2 rounded-full px-3 py-2 text-sm font-semibold">
                    {ticket?.priority}
                  </div> */}
                </div>
                <div>
                  <div className=" p-3  ">
                    <span className="font-bold">Created At: </span>
                    {ticket?.createdAt
                      ? new Date(ticket.createdAt).toLocaleString("en-IN", {
                          timeZone: "Asia/Kolkata",
                        })
                      : "N/A"}
                  </div>{" "}
                  <div className=" text rounded p-3 ">
                    <span className=" font-bold">Modifed At: </span>
                    {ticket?.modifiedAt
                      ? new Date(ticket.modifiedAt).toLocaleString("en-IN", {
                          timeZone: "Asia/Kolkata",
                        })
                      : "N/A"}
                  </div>{" "}
                </div>
                <div className=" p-3  ">
                  <span className="font-bold">Reported By: </span>
                  {ticket?.created?.email}
                </div>
                <div className=" px-3">
                  <span className="font-bold">Assigned To: </span>
                  <span>
                    <select
                      value={assigned}
                      onChange={(e) => updateAssigned(e)}
                      className="select select-ghost  max-w-lg"
                      disabled={auth.role !== "ADMIN"}
                    >
                      <option value="">None</option>
                      {users.map((user) => (
                        <option key={user.email} value={user.email}>
                          {user.email}
                        </option>
                      ))}
                    </select>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
