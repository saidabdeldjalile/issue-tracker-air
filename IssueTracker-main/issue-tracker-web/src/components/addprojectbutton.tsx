import axios from "axios";
import { useState } from "react";
import { mutate } from "swr";
import { toast } from "react-toastify";
import useAuth from "../hooks/useAuth";
import { useNavigate } from "react-router-dom";
import config from "../config";

// interface MutateProps {
//   mutate: (key: string, data?: any, shouldRevalidate?: boolean) => Promise<any>;
//   swrKey: string;
// }

export default function AddProjectButton() {
  const [projectName, setProjectName] = useState<string>("");
  const { auth } = useAuth();
  const navigate = useNavigate();

  function handleSubmit(e: any) {
    e.preventDefault();
    axios
      .post(`${config.apiUrl}/api/projects`, {
        name: projectName,
      })
      .then((res) => {
        console.log(res);
        mutate("project");
        toast.success("Project added successfully");
      })
      .catch((err) => {
        console.log(err);

        toast.error("Project was not added");
      });
    setProjectName("");
  }

  return (
    <>
      {/* Open the modal using document.getElementById('ID').showModal() method */}
      <button
        className="btn btn-primary tooltip tooltip-right"
        data-tip="Only Managers can add projects"
        onClick={() => {
          console.log(auth);
          if (!auth || !auth.email) {
            navigate("/login", { replace: true });
          } else if (auth.role === "ADMIN") {
            (
              document.getElementById("my_modal_6") as HTMLDialogElement
            )?.showModal();
          }
        }}
        // disabled={auth.role !== "ADMIN"}
      >
        Add Project
      </button>

      <dialog id="my_modal_6" className="modal modal-bottom sm:modal-middle">
        <div className="modal-box">
          <h3 className="text-lg font-bold">Project</h3>
          <p className="py-4">
            <input
              type="text"
              placeholder="Type here"
              onChange={(e) => setProjectName(e.target.value)}
              className="input input-bordered w-full max-w-md"
            />
          </p>
          <span>
            <div className="modal-action">
              <form method="dialog">
                {/* if there is a button in form, it will close the modal */}
                <button
                  className="btn btn-success mx-2"
                  onClick={(e) => {
                    handleSubmit(e);
                    (
                      document.getElementById("my_modal_6") as HTMLDialogElement
                    )?.close();
                  }}
                >
                  Add
                </button>
                <button className=" btn ml-2 mr-2">Close</button>
              </form>
            </div>
          </span>
        </div>
      </dialog>
    </>
  );
}
