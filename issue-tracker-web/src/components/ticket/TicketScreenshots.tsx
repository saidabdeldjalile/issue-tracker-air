import { useEffect, useState } from "react";
import api from "../../api/axios";
import { toast } from "react-toastify";
import ScreenshotGallery from "../ScreenshotGallery";
import ScreenshotUploader from "../ScreenshotUploader";
import { Screenshot } from "../../TicketResponse";

type TicketScreenshotsProps = {
  ticketId: number | undefined;
};

export default function TicketScreenshots({ ticketId }: TicketScreenshotsProps) {
  const [screenshots, setScreenshots] = useState<Screenshot[]>([]);
  const [loadingScreenshots, setLoadingScreenshots] = useState(false);

  useEffect(() => {
    if (ticketId) {
      void loadScreenshots(ticketId);
    } else {
      setScreenshots([]);
    }
  }, [ticketId]);

  const loadScreenshots = async (id: number) => {
    setLoadingScreenshots(true);
    try {
      const res = await api.get(`/tickets/${id}/screenshots`);
      setScreenshots(Array.isArray(res.data) ? res.data : []);
    } catch (error) {
      console.error("Error loading screenshots:", error);
      setScreenshots([]);
    } finally {
      setLoadingScreenshots(false);
    }
  };

  const handleUploadScreenshot = async (file: File) => {
    if (!ticketId) return;

    const formData = new FormData();
    formData.append("file", file);

    const res = await api.post(`/tickets/${ticketId}/screenshots`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    setScreenshots((prev) => [...prev, res.data]);
  };

  const handleDeleteScreenshot = async (screenshotId: number) => {
    if (!ticketId) return;

    try {
      await api.delete(`/tickets/${ticketId}/screenshots/${screenshotId}`);
      setScreenshots((prev) => prev.filter((s) => s.id !== screenshotId));
      toast.success("Capture d'écran supprimée avec succès");
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message ||
        error.message ||
        "Erreur lors de la suppression de la capture";
      toast.error(errorMessage);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="text-sm font-semibold text-base-content">Captures d'écran</div>
        <ScreenshotUploader
          ticketId={ticketId || 0}
          onUpload={handleUploadScreenshot}
          disabled={!ticketId}
        />
      </div>

      <div className="overflow-hidden rounded-xl border border-base-300/60 bg-base-100/80 shadow-sm">
        <div className="p-4">
          {loadingScreenshots ? (
            <div className="flex items-center justify-center py-10">
              <div className="h-8 w-8 rounded-full border-4 border-blue-500 border-t-transparent animate-spin"></div>
            </div>
          ) : (
            <ScreenshotGallery
              screenshots={screenshots}
              ticketId={ticketId || 0}
              onDelete={handleDeleteScreenshot}
            />
          )}
        </div>
      </div>
    </div>
  );
}

