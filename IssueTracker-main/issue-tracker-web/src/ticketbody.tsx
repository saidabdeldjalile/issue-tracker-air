import { Link } from "react-router-dom";
import { TicketResponse } from "./TicketResponse";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css"; // Import Quill styles
import { useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import Comments from "./components/comment";
import useAuth from "./hooks/useAuth";
import config from "./config";

type TicketBodyProps = {
  ticket: TicketResponse | null | undefined;
};

export function TicketBody({ ticket }: TicketBodyProps) {
  const [editorValue, setEditorValue] = useState(ticket?.description);
  const [isEditing, setIsEditing] = useState(false);
  const [finalDescription, setFinalDescription] = useState(ticket?.description);
  const { auth } = useAuth();
  const [comments, setComments] = useState(ticket?.comments);
  function handleSubmit() {
    axios
      .post(`${config.apiUrl}/api/tickets/${ticket?.id}`, {
        description: editorValue,
      })
      .then(() => {
        setFinalDescription(editorValue);
        setIsEditing(false);
        toast.success("Ticket description updated successfully");
      });
  }
  function handleCancel() {
    setEditorValue(finalDescription);
    setIsEditing(false);
  }

  function addComment(e: string) {
    axios
      .post(`${config.apiUrl}/api/tickets/${ticket?.id}/comments`, {
        comment: e,
        email: auth?.email,
      })
      .then(() => {
        axios
          .get(`${config.apiUrl}/api/tickets/${ticket?.id}/comments`)
          .then((res) => {
            setComments(res.data);
          });
      });
  }
  return (
    <div>
      <div className="breadcrumbs ml-2 mt-1 text-sm">
        <ul>
          <li>
            <Link to={`/projects`}>Projects</Link>{" "}
          </li>
          <li>
            <span className=" opacity-60">#</span>
            {ticket?.project.id} {ticket?.project.name}
          </li>
          <li>
            <Link to={`/projects/${ticket?.project.id}/tickets`}>Issues</Link>
          </li>
          <li>
            <span className=" opacity-60">#</span>
            {ticket?.id} {ticket?.title}
          </li>
        </ul>
      </div>
      <div className=" p-3 text-4xl font-bold ">
        <span className=" opacity-60">#</span>
        {ticket?.id} {ticket?.title}
      </div>
      <div className=" p-2 font-bold ">Description: </div>
      {/* <textarea
        className="textarea textarea-ghost m-3 w-11/12"
        placeholder="Bio"
      >
        {ticket?.description}
      </textarea>*/}
      <>
        {isEditing ? (
          <>
            <ReactQuill
              value={editorValue}
              onChange={(value) => setEditorValue(value)}
              modules={{
                toolbar: [
                  [{ font: [] }],
                  [{ size: [] }],
                  ["bold", "italic", "underline", "strike"],
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
            <div
              className="btn btn-primary mx-1 my-2"
              onClick={() => handleSubmit()}
            >
              Submit
            </div>
            <div
              className="btn btn-error mx-1 my-2"
              onClick={() => handleCancel()}
            >
              Cancel
            </div>
          </>
        ) : (
          <div
            className="p-2 hover:bg-base-200"
            onDoubleClick={() => setIsEditing(true)}
          >
            <ReactQuill
              value={editorValue}
              readOnly
              theme="bubble"
            ></ReactQuill>
          </div>
        )}
      </>
      <div className=" p-2 font-bold ">Disscusion({comments?.length}): </div>
      <Comments addComment={addComment} comments={comments} />
    </div>
  );
}
