import { AxiosResponse } from "axios";
import axios from "./api/axios";
import useSWR, { mutate } from "swr";
import { Project } from "./TicketResponse";
import { Link } from "react-router-dom";
import AddProjectButton from "./components/addprojectbutton";
import { toast } from "react-toastify";
import config from "./config";

export default function ProjectList() {
  const url = `/projects`;
  const key = `project`;

  const fetcher = (url: string) => {
    return axios.get<Project[]>(url).then((res: AxiosResponse<Project[]>) => {
      return res.data;
    });
  };

  const {
    data: projects,
    error,
    isLoading,
  } = useSWR<Project[] | null>(key, () => fetcher(url));

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

  //   function deleteproject(id: number) {
  //     axios.delete(`${config.apiUrl}/api/projects/${id}`).then((res) => {
  //       console.log(res);
  //     });
  //   }
  function handledelete(id: number) {
    if (window.confirm("Are you sure you want to delete this project?")) {
      axios
        .delete(`${config.apiUrl}/api/projects/${id}`)
        .then(() => {
          mutate("project");
          toast.success("Project deleted successfully");
        })
        .catch(() => {
          toast.error("Project was not deleted");
        });
    }
  }
  return (
    <>
      {/* <NavBar /> */}
      <div className="ml-2 mt-1">
        <div className="breadcrumbs text-sm">
          <ul>
            <li>
              <a>Projects</a>
            </li>
          </ul>
        </div>
        <div className="mx-6">
          <div className="overflow-x-auto">
            <table className="table">
              {/* head */}
              <thead>
                <tr>
                  <th>Project ID</th>
                  <th>Name</th>
                </tr>
              </thead>
              <tbody>
                {projects?.map((project) => (
                  <tr key={project.id} className="hover">
                    <th>
                      <Link to={`/projects/${project.id}/tickets`}>
                        <span className=" opacity-60">#</span>
                        {project.id}
                      </Link>
                    </th>
                    <td>
                      <Link to={`/projects/${project.id}/tickets`}>
                        {project.name}
                      </Link>
                    </td>
                    <td>
                      {/* <FontAwesomeIcon
                        icon={faTrash}
                        className="cursor-pointer text-red-500"
                        onClick={() => handledelete(project.id)}
                      /> */}
                      <svg
                        style={{ color: "red", width: "16px", height: "16px" }}
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 448 512"
                        onClick={() => handledelete(project.id)}
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
        </div>
      </div>
      <div className="p-2 pl-4">
        <AddProjectButton />
      </div>
    </>
  );
}
