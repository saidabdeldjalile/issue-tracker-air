import { useEffect, useState } from "react";
import CreateTicketBody from "../createticket";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

export default function CreateButton() {
  const [key, setKey] = useState(0);
  const navigate = useNavigate();
  const { auth } = useAuth();

  const openCreateModal = () => {
    setKey((prevKey) => prevKey + 1);
    (document.getElementById("my_modal_5") as HTMLDialogElement)?.showModal();
  };

  useEffect(() => {
    const draft = localStorage.getItem("chatbotTicketDraft");
    if (!draft) {
      return;
    }

    if (!auth || !auth.email) {
      navigate("/login", { replace: true });
      return;
    }

    const timer = window.setTimeout(() => {
      openCreateModal();
    }, 150);

    return () => window.clearTimeout(timer);
  }, [auth, navigate]);

  return (
    <>
      <button
        className="btn btn-primary"
        onClick={() => {
          if (!auth || !auth.email) {
            navigate("/login", { replace: true });
          } else {
            openCreateModal();
          }
        }}
      >
        Create your ticket
      </button>
      <dialog id="my_modal_5" className="modal ">
        <div className="modal-box w-11/12 max-w-5xl  ">
          <h3 className="text-lg font-bold">Create</h3>
          <CreateTicketBody refreshKey={key} />
        </div>
      </dialog>
    </>
  );
}
