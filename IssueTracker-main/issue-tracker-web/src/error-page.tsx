import { useRouteError } from "react-router-dom";

// This interface represents the structure we expect from our errors
// You might need to adjust it based on what your errors actually look like
interface ErrorWithStatusText {
  statusText?: string;
  message?: string;
}

export default function ErrorPage() {
  const error = useRouteError();
  console.error(error);

  // Here we are type-guarding the error object
  const isErrorWithStatusText = (error: any): error is ErrorWithStatusText => {
    return typeof error === "object" && (error.statusText || error.message);
  };

  return (
    <div id="error-page">
      <h1>Oops!</h1>
      <p>Sorry, an unexpected error has occurred.</p>
      {isErrorWithStatusText(error) ? (
        <p>
          <i>{error.statusText || error.message}</i>
        </p>
      ) : (
        <p>
          <i>An unknown error occurred.</i>
        </p>
      )}
    </div>
  );
}
