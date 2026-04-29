import React, { useState } from "react";
import { Screenshot } from "../TicketResponse";
import { toast } from "react-toastify";
import useAuth from "../hooks/useAuth";
import config from "../config";

interface ScreenshotGalleryProps {
  screenshots: Screenshot[];
  ticketId: number;
  onDelete: (screenshotId: number) => Promise<void>;
}

const ScreenshotGallery: React.FC<ScreenshotGalleryProps> = ({
  screenshots,
  onDelete,
}) => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const { auth } = useAuth();

  const handleDelete = async (screenshot: Screenshot) => {
    // Only allow the uploader to delete their own screenshots
    if (auth?.email !== screenshot.uploadedByEmail) {
      toast.error("Vous ne pouvez supprimer que vos propres captures");
      return;
    }

    if (!window.confirm("Supprimer cette capture d'écran ?")) {
      return;
    }

    try {
      await onDelete(screenshot.id);
      toast.success("Capture supprimée");
    } catch (error) {
      console.error("Error deleting screenshot:", error);
      toast.error("Erreur lors de la suppression");
    }
  };

  const formatFileSize = (bytes?: number): string => {
    if (!bytes) return "";
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
    return (bytes / (1024 * 1024)).toFixed(1) + " MB";
  };

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleString("fr-FR", {
      day: "numeric",
      month: "short",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // Build full URL for screenshot images (backend URL + imageUrl path)
  const getFullImageUrl = (imageUrl: string): string => {
    // If the imageUrl already starts with http:// or https://, return as is
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }
    
    // Handle different URL patterns - use staticUrl (without /api/v1) for static resources
    if (imageUrl.startsWith('/api/screenshots/')) {
      return `${config.staticUrl}${imageUrl}`;
    }
    
    if (imageUrl.startsWith('/uploads/screenshots/')) {
      return `${config.staticUrl}${imageUrl}`;
    }
    
    // For relative paths, prepend the static URL
    if (!imageUrl.startsWith('/')) {
      return `${config.staticUrl}/api/screenshots/${imageUrl}`;
    }
    
    // Default case: prepend static URL
    return `${config.staticUrl}${imageUrl}`;
  };

  return (
    <>
      <div className="space-y-3">
        <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-200">📸 Captures d'écran</h4>
        
        {screenshots.length === 0 ? (
          <div className="text-center py-6 text-gray-400 text-sm bg-gray-50 rounded-lg dark:bg-gray-700 dark:text-gray-300">
            Aucune capture d'écran
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {screenshots.map((screenshot) => {
              const fullImageUrl = getFullImageUrl(screenshot.imageUrl);
              return (
              <div
                key={screenshot.id}
                className="relative group bg-white border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow dark:bg-gray-800 dark:border-gray-700"
              >
                {/* Thumbnail */}
                <div
                  className="aspect-square cursor-pointer overflow-hidden"
                  onClick={() => setSelectedImage(fullImageUrl)}
                >
                <img
                  src={fullImageUrl}
                  alt={screenshot.fileName}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                  loading="lazy"
                  onError={(e) => {
                    console.error('Failed to load image:', fullImageUrl);
                    (e.target as HTMLImageElement).src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2VlZSIvPjx0ZXh0IHg9IjUwIiB5PSI1MCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iIGZpbGw9IiM5OTkiPkltYWdlPC90ZXh0Pjwvc3ZnPg==';
                  }}
                />
                </div>

                {/* Info overlay */}
                <div className="p-2 bg-gray-50 border-t border-gray-200 dark:bg-gray-700 dark:border-gray-600">
                  <div className="flex items-center justify-between">
                    <div className="flex-1 min-w-0">
                      <p
                        className="text-xs text-gray-600 truncate dark:text-gray-200"
                        title={screenshot.fileName}
                      >
                        {screenshot.fileName}
                      </p>
                      <p className="text-xs text-gray-400 dark:text-gray-300">
                        {formatFileSize(screenshot.fileSize)} •{" "}
                        {formatDate(screenshot.createdAt)}
                      </p>
                    </div>

                    {/* Delete button */}
                    {auth?.email === screenshot.uploadedByEmail && (
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(screenshot);
                        }}
                        className="text-gray-400 hover:text-red-500 transition-colors ml-2 flex-shrink-0"
                        title="Supprimer"
                      >
                        🗑️
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
            })}
          </div>
        )}
      </div>

      {/* Image Modal */}
      {selectedImage && (
        <div
          className="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center p-4"
          onClick={() => setSelectedImage(null)}
        >
          <div className="relative max-w-5xl max-h-[90vh]">
            <button
              className="absolute -top-10 right-0 text-white text-2xl hover:text-gray-300"
              onClick={() => setSelectedImage(null)}
            >
              ✕
            </button>
            <img
              src={selectedImage}
              alt="Screenshot"
              className="max-w-full max-h-[85vh] object-contain rounded"
            />
          </div>
        </div>
      )}
    </>
  );
};

export default ScreenshotGallery;