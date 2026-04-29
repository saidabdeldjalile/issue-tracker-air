import React from "react";
import type { Comment } from "../TicketResponse";

interface CommentProps {
  addComment: (comment: string) => void;
  comments: Comment[] | undefined;
}

const Comment: React.FC<CommentProps> = ({ addComment, comments }) => {
  const [comment, setComment] = React.useState("");

  return (
    <>
      <input
        type="text"
        placeholder="Type here"
        className="input input-bordered mx-2 w-full max-w-xs"
        onChange={(e) => setComment(e.target.value)}
      />
      <div className="btn mx-2 bg-primary" onClick={() => addComment(comment)}>
        Comment
      </div>
      <ul className=" list-inside list-disc p-2">
        {comments?.map((comm) => (
          <>
            <article className="rounded-lg bg-base-100 p-6 text-base hover:bg-base-200">
              <footer className="mb-2 flex items-center justify-between">
                <div className="flex items-center">
                  <p className="mr-3 inline-flex items-center text-sm font-semibold text-base-content">
                    <div className="avatar placeholder">
                      <div className="mr-2 w-6 rounded-full bg-neutral text-neutral-content">
                        <span className="text-xs">
                          {comm.username
                            .split(" ")
                            .map((name) => name.slice(0, 1))}
                        </span>
                      </div>
                    </div>
                    {comm.username ? comm.username : "Anonymous"}
                  </p>
                  <p className="text-sm text-base-content">
                    <div>
                      {comm?.created
                        ? new Date(comm.created).toLocaleDateString() +
                          ", " +
                          new Date(comm.created).toLocaleTimeString()
                        : "N/A"}
                    </div>
                  </p>
                </div>
              </footer>
              <p className="text-base-content">{comm.comment}</p>
              <div className="mt-4 flex items-center space-x-4">
                <button
                  type="button"
                  className="flex items-center text-sm font-medium text-base-content hover:underline"
                >
                  <svg
                    className="mr-1.5 h-3.5 w-3.5"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 20 18"
                  >
                    <path
                      stroke="currentColor"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M5 5h5M5 8h2m6-3h2m-5 3h6m2-7H2a1 1 0 0 0-1 1v9a1 1 0 0 0 1 1h3v5l5-5h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1Z"
                    />
                  </svg>
                  Reply
                </button>
              </div>
            </article>
          </>
        ))}
      </ul>
    </>
  );
};

export default Comment;
