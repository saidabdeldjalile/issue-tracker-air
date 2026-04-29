import axios, { AxiosResponse } from "axios";
import { useEffect, useState } from "react";
import ReactQuill from "react-quill";
import { mutate } from "swr";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import useAuth from "./hooks/useAuth";
import config from "./config";

interface User {
  email: string;
  firstName: string;
  lastName: string;
}

interface IssueType {
  project: string;
  title: string;
  description: string;
  status: string;
  priority: string;
  label: string;
  issueType: string;
  reporter: string;
  assignee: string;
}

interface Project {
  id: number;
  name: string;
}

interface similarTicket {
  id: number;
  title: string;
}

export default function CreateTicketBody({ key }: { key: number }) {
  const { auth } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [similarTickets, setSimilarTickets] = useState<similarTicket[]>([]);

  const [isLoading, setIsLoading] = useState(false);

  const [newIssue, setNewIssue] = useState<IssueType>({
    project: "",
    title: "",
    description: "",
    status: "",
    priority: "",
    label: "",
    issueType: "",
    reporter: "",
    assignee: "",
  });

  useEffect(() => {
    axios
      .get(`${config.apiUrl}/api/users`)
      .then((res: AxiosResponse) => {
        setUsers(res.data);
      });

    axios
      .get(`${config.apiUrl}/api/projects`)
      .then((res: AxiosResponse) => {
        setProjects(res.data);
      });
  }, [key]);

  async function handleSubmit(e: any) {
    e.preventDefault();
    console.log(newIssue);
    setIsLoading(true);
    await axios
      .post(`${config.apiUrl}/api/tickets`, {
        project: newIssue.project,
        title: newIssue.title,
        description: newIssue.description,
        status: newIssue.status,
        priority: newIssue.priority,
        // label: project.label,
        // issueType: project.issueType,
        reporter: newIssue.reporter,
        assignee: newIssue.assignee,
      })
      .then((res) => {
        console.log(res);
        setIsLoading(false);
        if (res.data.similarTickets && res.data.similarTickets.length > 0) {
          setSimilarTickets(res.data.similarTickets);
          toast.warning("Similar tickets found");
        }
        mutate("ticket");
        toast.success("Ticket created with id " + res.data.id);
      })
      .catch((err) => {
        setIsLoading(false);
        window.alert(err);
      });
  }

  function clearCreate(e: any) {
    e.preventDefault();
    setNewIssue({
      project: "",
      title: "",
      description: "",
      status: "",
      priority: "",
      label: "",
      issueType: "",
      reporter: "",
      assignee: "",
    });
    setSimilarTickets([]);
  }

  function handleChange(e: any) {
    setNewIssue({ ...newIssue, [e.target.name]: e.target.value });
  }

  function handleDescriptionChange(e: string) {
    setNewIssue({ ...newIssue, description: e });
  }

  return (
    <div>
      <label className="form-control w-full max-w-xs p-3">
        <div className="label">
          <span className="label-text">Project</span>
        </div>
        <select
          name="project"
          key={newIssue.project}
          value={newIssue.project}
          onChange={(e) => handleChange(e)}
          className="select select-bordered"
        >
          <option value="" disabled>
            Pick one
          </option>
          {projects.map((project: Project) => (
            <option value={project.id}>{project.name}</option>
          ))}
        </select>
      </label>
      <label className="form-control w-full max-w-xs p-3">
        <div className="label">
          <span className="label-text">Title</span>
        </div>
        <input
          type="text"
          placeholder="Type here"
          name="title"
          value={newIssue.title}
          onChange={(e) => handleChange(e)}
          className="input input-bordered w-full max-w-xs"
        />
      </label>

      <div className=" inline-flex">
        <label className="form-control w-full max-w-xs p-3">
          <div className="label">
            <span className="label-text">Issue Type</span>
          </div>
          <select
            name="issueType"
            key={newIssue.issueType}
            value={newIssue.issueType}
            onChange={(e) => handleChange(e)}
            className="select select-bordered"
          >
            <option disabled value="">
              Pick one
            </option>
            <option value="Bug">Bug</option>
            <option value="Feature">Feature</option>
            <option value="Task">Task</option>
            <option value="Improvement"> Improvement</option>
          </select>
        </label>

        <label className="form-control w-full max-w-xs p-3">
          <div className="label">
            <span className="label-text">Status</span>
          </div>
          <select
            name="status"
            key={newIssue.status}
            value={newIssue.status}
            onChange={(e) => handleChange(e)}
            className="select select-bordered"
          >
            <option value="" disabled>
              Select
            </option>
            <option value="Open">OPEN</option>
            <option value="ToDo">TO DO</option>
            <option value="InProgress">IN PROGRESS</option>
            <option value="Done">DONE</option>
            <option value="Closed">CLOSED</option>
          </select>
        </label>
        <label className="form-control w-full max-w-xs p-3">
          <div className="label">
            <span className="label-text">Priority</span>
          </div>
          <select
            name="priority"
            key={newIssue.priority}
            value={newIssue.priority}
            onChange={(e) => handleChange(e)}
            className="select select-bordered"
          >
            <option disabled value="">
              Pick one
            </option>
            <option value="High">High</option>
            <option value="Medium">Medium</option>
            <option value="Low">Low</option>
            <option value="Critical">Critical</option>
          </select>
        </label>
        <label className="form-control w-full max-w-xs p-3">
          {/* <div className="label">
            <span className="label-text">Label</span>
          </div>
          <select
            name="label"
            key={newIssue.label}
            value={newIssue.label}
            onChange={(e) => handleChange(e)}
            className="select select-bordered"
          >
            <option selected>Open</option>
            <option>To Do</option>
            <option>In Progress</option>
            <option>Done</option>
            <option>Closed</option>
          </select> */}
        </label>
      </div>
      <label className="form-control p-3 pr-5">
        <div className="label">
          <span className="label-text">Description</span>
        </div>
        {/* <textarea
          name="description"
          value={newIssue.description}
          onChange={(e) => handleChange(e)}
          className="textarea textarea-bordered h-24 "
          placeholder="Explain"
        ></textarea> */}
      </label>

      <ReactQuill
        value={newIssue.description}
        onChange={(e) => handleDescriptionChange(e)}
        modules={{
          toolbar: [
            [{ header: "1" }, { header: "2" }, { font: [] }],
            [{ size: [] }],
            ["bold", "italic", "underline", "strike", "blockquote"],
            [
              { list: "ordered" },
              { list: "bullet" },
              { indent: "-1" },
              { indent: "+1" },
            ],
            ["link", "image", "video"],
            ["clean"],
          ],
        }}
        theme="snow"
      />
      <div className="inline-flex">
        <label className="form-control w-full max-w-xs flex-shrink-0 p-3">
          <div className="label">
            <span className="label-text">Assign To</span>
          </div>
          <select
            value={newIssue.assignee}
            key={newIssue.assignee}
            className="select select-bordered"
            name="assignee"
            onChange={(e) => handleChange(e)}
          >
            <option value="" disabled>
              Select
            </option>
            {users.map((user: User) => (
              <option value={user.email} key={user.email}>
                {user.firstName} {user.lastName}
              </option>
            ))}
          </select>
        </label>

        <label className="form-control w-full max-w-xs flex-shrink-0 p-3">
          <div className="label">
            <span className="label-text">Reporter</span>
          </div>
          <select
            value={newIssue.reporter}
            key={auth.email}
            name="reporter"
            className="select select-bordered"
            onChange={(e) => handleChange(e)}
          >
            <option value="" disabled>
              Select
            </option>
            {users.map((user: User) => (
              <option value={user.email} key={user.email}>
                {user.firstName} {user.lastName}
              </option>
            ))}
          </select>
        </label>
      </div>
      {isLoading && (
        <div className="flex items-center justify-center">
          <div className="p-3">
            <span className="font-bold">Looking for Duplicates</span>
          </div>
          <div>
            <progress className="progress w-56"></progress>
          </div>
        </div>
      )}
      {similarTickets &&
        similarTickets.length > 0 &&
        (console.log(similarTickets),
        (
          <>
            <div className="p-3">
              <span className="font-bold">Possible Duplicates</span>
            </div>

            <div className="mx-6">
              <div className="overflow-x-auto">
                <table className="table">
                  {/* head */}
                  <thead>
                    <tr>
                      <th>Ticket ID</th>
                      <th>Title</th>
                    </tr>
                  </thead>
                  <tbody>
                    {similarTickets?.map((similarTicket) => (
                      <tr key={similarTicket.id} className="hover">
                        <th>
                          <Link
                            to={`/projects/${newIssue.priority}/tickets/${similarTicket.id}`}
                          >
                            <span className=" opacity-60">#</span>
                            {similarTicket.id}
                          </Link>
                        </th>
                        <td>
                          <Link
                            to={`/projects/${newIssue.priority}/tickets/${similarTicket.id}`}
                          >
                            {similarTicket.title}
                          </Link>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        ))}

      <div className="modal-action">
        <form method="dialog">
          <button
            className="btn btn-primary m-1"
            onClick={(e) => {
              // (
              //   document.getElementById("my_modal_5") as HTMLDialogElement
              // )?.close();
              handleSubmit(e);
            }}
          >
            Create
          </button>
        </form>
        <form method="dialog">
          {/* if there is a button in form, it will close the modal */}
          <button
            className="btn btn-error m-1"
            onClick={(e) => {
              (
                document.getElementById("my_modal_5") as HTMLDialogElement
              )?.close();
              clearCreate(e);
            }}
          >
            Close
          </button>
        </form>
      </div>
    </div>
  );
}
