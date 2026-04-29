import React, { useRef, useState } from "react";
import { toast } from "react-toastify";

interface ScreenshotUploaderProps {
  ticketId: number;
  onUpload: (file: File) => Promise<void>;
  disabled?: boolean;
}

const ScreenshotUploader: React.FC<ScreenshotUploaderProps> = ({
  onUpload,
  disabled = false,
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate file type (accept all image types)
    if (!file.type.startsWith("image/")) {
      toast.error("Veuillez sélectionner un fichier image valide");
      return;
    }

    // Validate file size (10MB max)
    if (file.size > 10 * 1024 * 1024) {
      toast.error("Le fichier est trop volumineux (max 10MB)");
      return;
    }

    setIsUploading(true);
    try {
      await onUpload(file);
      toast.success("Capture ajoutée avec succès");
    } catch (error) {
      console.error("Error uploading screenshot:", error);
      toast.error("Erreur lors de l'ajout de la capture");
    } finally {
      setIsUploading(false);
      // Reset input so same file can be selected again
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
    }
  };

  return (
    <div className="flex items-center gap-2">
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        accept="image/*"
        className="hidden"
        disabled={disabled || isUploading}
      />
      <button
        onClick={handleClick}
        disabled={disabled || isUploading}
        className={`inline-flex items-center gap-2 px-4 py-2 text-sm rounded-lg transition-colors ${
          disabled || isUploading
            ? "bg-gray-300 text-gray-500 cursor-not-allowed dark:bg-gray-600 dark:text-gray-400"
            : "bg-green-600 text-white hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600"
        }`}
      >
        {isUploading ? (
          <>
            <span className="loading loading-spinner loading-sm"></span>
            Upload...
          </>
        ) : (
          <>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-4 w-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            Ajouter une capture
          </>
        )}
      </button>
      <span className="text-xs text-gray-400 dark:text-gray-300">
        Formats: JPG, PNG, GIF, WebP (max 10MB)
      </span>
    </div>
  );
};

export default ScreenshotUploader;