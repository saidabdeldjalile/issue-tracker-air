import { useParams } from "react-router-dom";
import TicketList from "./ticketlist";

import { useEffect, useState } from "react";
import api from "./api/axios";
import { Project } from "./TicketResponse";

export default function Testing() {
  const { pid } = useParams<Record<string, string | undefined>>();
  const [project, setProject] = useState<Project>();

  useEffect(() => {
    api.get(`/projects/${pid}`).then((res) => {
      setProject(res.data);
    }).catch((err) => {
      console.error("Error fetching project:", err);
    });
  }, [pid]); // Include pid in dependency array to refetch when project ID changes

  return (
    <>
      <div className="drawer">
        <input id="my-drawer" type="checkbox" className="drawer-toggle" />
        <div className="drawer-content">
          {/* Page content here */}
          {/* <NavBar /> */}
          {project && <TicketList project={project} />}
          {/* TODO: not valid project display */}
        </div>
        <div className="drawer-side">
          <label
            htmlFor="my-drawer"
            aria-label="close sidebar"
            className="drawer-overlay"
          ></label>
          <ul className="menu min-h-full w-80 bg-base-200 p-4 text-base-content">
            {/* Sidebar content here */}
            <li>
              <a>Sidebar Item 1</a>
            </li>
            <li>
              <a>Sidebar Item 2</a>
            </li>
          </ul>
        </div>
      </div>
    </>
  );
}
