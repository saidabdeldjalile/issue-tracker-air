import React, { useRef, useState } from "react";
import type { Comment } from "../TicketResponse";
import { toast } from "react-toastify";
import useAuth from "../hooks/useAuth";
import api from "../api/axios";
import config from "../config";
import { Send, Image, Trash2, Clock, MessageCircle, Image as ImageIcon, X } from "lucide-react";

interface CommentProps {
  addComment: (comment: string) => Promise<number>;
  deleteComment: (commentId: number) => Promise<void>;
  comments: Comment[] | undefined;
  ticketId?: number;
}

interface ScreenshotPreview {
  file: File;
  preview: string;
}

interface CommentScreenshot {
  id: number;
  imageUrl: string;
  fileName: string;
  createdAt: string;
}

const getFullImageUrl = (imageUrl: string): string => {
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }
  
  if (imageUrl.startsWith('/api/screenshots/')) {
    return `${config.staticUrl}${imageUrl}`;
  }
  
  if (imageUrl.startsWith('/uploads/screenshots/')) {
    return `${config.staticUrl}${imageUrl}`;
  }
  
  if (!imageUrl.startsWith('/')) {
    return `${config.staticUrl}/api/screenshots/${imageUrl}`;
  }
  
  return `${config.staticUrl}${imageUrl}`;
};

const Comment: React.FC<CommentProps> = ({ addComment, deleteComment, comments, ticketId }) => {
  const [comment, setComment] = useState("");
  const [isAdding, setIsAdding] = useState(false);
  const [selectedScreenshots, setSelectedScreenshots] = useState<ScreenshotPreview[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [commentScreenshots, setCommentScreenshots] = useState<Record<number, CommentScreenshot[]>>({});
  const { auth } = useAuth();

  React.useEffect(() => {
    if (ticketId && comments) {
      comments.forEach((comm) => {
        if (comm.id && !commentScreenshots[comm.id]) {
          loadCommentScreenshots(ticketId, comm.id);
        }
      });
    }
  }, [comments, ticketId]);

  const loadCommentScreenshots = async (ticketId: number, commentId: number) => {
    try {
      const res = await api.get(`/tickets/${ticketId}/comments/${commentId}/screenshots`);
      setCommentScreenshots((prev) => ({
        ...prev,
        [commentId]: Array.isArray(res.data) ? res.data : [],
      }));
    } catch (error) {
      console.error(`Error loading screenshots for comment ${commentId}:`, error);
    }
  };

  const handleAddComment = async () => {
    if (!comment.trim()) {
      toast.warning("Veuillez saisir un commentaire");
      return;
    }

    setIsAdding(true);
    try {
      const newCommentId = await addComment(comment);
      setComment("");

      if (selectedScreenshots.length > 0 && ticketId) {
        await uploadScreenshots(ticketId, newCommentId);
      }

      toast.success("Commentaire ajouté avec succès !");
    } catch (error) {
      console.error("Error adding comment:", error);
      toast.error("Erreur lors de l'ajout du commentaire");
    } finally {
      setIsAdding(false);
    }
  };

  const uploadScreenshots = async (ticketId: number, commentId: number) => {
    if (selectedScreenshots.length === 0) return;

    try {
      for (const screenshot of selectedScreenshots) {
        const formData = new FormData();
        formData.append("file", screenshot.file);

        await api.post(`/tickets/${ticketId}/comments/${commentId}/screenshots`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
      }

      toast.success(`${selectedScreenshots.length} capture(s) ajoutée(s)`);
      setSelectedScreenshots([]);
    } catch (error) {
      console.error("Error uploading screenshots:", error);
      toast.error("Erreur lors de l'ajout des captures");
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    if (!window.confirm("Supprimer ce commentaire ?")) {
      return;
    }

    try {
      await deleteComment(commentId);
      toast.success("Commentaire supprimé");
    } catch (error) {
      console.error("Error deleting comment:", error);
      toast.error("Erreur lors de la suppression");
    }
  };

  const handleScreenshotSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files) return;

    Array.from(files).forEach(file => {
      if (!file.type.startsWith("image/")) {
        toast.error("Seuls les fichiers image sont acceptés");
        return;
      }

      if (file.size > 10 * 1024 * 1024) {
        toast.error("Le fichier est trop volumineux (max 10MB)");
        return;
      }

      const preview = URL.createObjectURL(file);
      setSelectedScreenshots(prev => [...prev, { file, preview }]);
    });

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const removeScreenshot = (index: number) => {
    setSelectedScreenshots(prev => {
      const newScreenshots = [...prev];
      URL.revokeObjectURL(newScreenshots[index].preview);
      newScreenshots.splice(index, 1);
      return newScreenshots;
    });
  };

  const getUserInitials = (username?: string) => {
    if (!username) return "?";
    return username
      .split(" ")
      .map(n => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <div className="space-y-6">
      {/* Add Comment Form */}
      <div className="bg-gradient-to-r from-gray-50 to-white dark:from-gray-800/50 dark:to-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-700">
        <div className="flex items-start gap-3">
          <div className="flex-shrink-0">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm shadow-md">
              {getUserInitials(auth?.firstName + " " + auth?.lastName)}
            </div>
          </div>
          <div className="flex-1 space-y-3">
            <textarea
              placeholder="Ajouter un commentaire..."
              className="w-full px-4 py-3 text-sm border border-gray-200 dark:border-gray-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 resize-none transition-all"
              rows={3}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              disabled={isAdding}
            />
            
            {/* Screenshot Previews */}
            {selectedScreenshots.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {selectedScreenshots.map((screenshot, index) => (
                  <div key={index} className="relative group">
                    <img
                      src={screenshot.preview}
                      alt={`Preview ${index + 1}`}
                      className="w-20 h-20 object-cover rounded-xl border-2 border-gray-200 dark:border-gray-600 shadow-sm hover:shadow-md transition-all"
                    />
                    <button
                      type="button"
                      onClick={() => removeScreenshot(index)}
                      className="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-white rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-md"
                    >
                      <X className="w-3 h-3" />
                    </button>
                  </div>
                ))}
              </div>
            )}
            
            <div className="flex items-center justify-between gap-3">
              <button
                type="button"
                onClick={() => fileInputRef.current?.click()}
                disabled={isAdding}
                className="flex items-center gap-2 px-4 py-2 text-sm text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-xl transition-all"
              >
                <Image className="w-4 h-4" />
                Joindre une image
              </button>
              <input
                type="file"
                ref={fileInputRef}
                onChange={handleScreenshotSelect}
                accept="image/*"
                multiple
                className="hidden"
                disabled={isAdding}
              />
              <button
                onClick={handleAddComment}
                disabled={isAdding || (!comment.trim() && selectedScreenshots.length === 0)}
                className="flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white font-semibold rounded-xl transition-all shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isAdding ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                    Envoi...
                  </>
                ) : (
                  <>
                    <Send className="w-4 h-4" />
                    Publier
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
      
      {/* Comments List */}
      <div className="space-y-4">
        {comments?.length === 0 ? (
          <div className="text-center py-12">
            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center">
              <MessageCircle className="w-8 h-8 text-gray-400" />
            </div>
            <p className="text-gray-500 dark:text-gray-400">Aucun commentaire pour le moment</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">Soyez le premier à commenter</p>
          </div>
        ) : (
          comments?.map((comm, index) => (
            <div key={comm.id ?? `comment-${index}`} className="group bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-700 p-5 hover:shadow-md transition-all">
              <div className="flex gap-3">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm shadow-md">
                    {getUserInitials(comm.username)}
                  </div>
                </div>
                <div className="flex-1">
                  <div className="flex items-center justify-between flex-wrap gap-2 mb-2">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="font-semibold text-gray-900 dark:text-white">
                        {comm.username || "Anonyme"}
                      </span>
                      <span className="flex items-center gap-1 text-xs text-gray-400">
                        <Clock className="w-3 h-3" />
                        {comm?.created
                          ? new Date(comm.created).toLocaleString("fr-FR", {
                              day: "numeric",
                              month: "short",
                              hour: "2-digit",
                              minute: "2-digit",
                            })
                          : "N/A"}
                      </span>
                    </div>
                    {auth?.email === comm.email && (
                      <button
                        onClick={() => handleDeleteComment(comm.id!)}
                        className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500 transition-all"
                        title="Supprimer"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                  <p className="text-gray-700 dark:text-gray-300 leading-relaxed whitespace-pre-wrap">
                    {comm.comment}
                  </p>
                  
                  {/* Screenshots Gallery */}
                  {commentScreenshots[comm.id] && commentScreenshots[comm.id].length > 0 && (
                    <div className="flex flex-wrap gap-2 mt-3 pt-2 border-t border-gray-100 dark:border-gray-800">
                      {commentScreenshots[comm.id].map((screenshot) => {
                        const fullImageUrl = getFullImageUrl(screenshot.imageUrl);
                        return (
                          <a
                            key={screenshot.id}
                            href={fullImageUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="relative group/img block"
                          >
                            <img
                              src={fullImageUrl}
                              alt={screenshot.fileName}
                              className="w-20 h-20 rounded-lg border border-gray-200 dark:border-gray-700 object-cover hover:opacity-90 transition-all shadow-sm hover:shadow-md"
                              onError={(e) => {
                                console.error('Failed to load screenshot:', fullImageUrl);
                                (e.target as HTMLImageElement).src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2UyZThmMCIvPjx0ZXh0IHg9IjUwIiB5PSI1MCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iIGZpbGw9IiM5Y2EzYWYiIGZvbnQtc2l6ZT0iMTIiPkltYWdlPC90ZXh0Pjwvc3ZnPg==';
                              }}
                            />
                            <div className="absolute inset-0 bg-black/50 rounded-lg flex items-center justify-center opacity-0 group-hover/img:opacity-100 transition-opacity">
                              <ImageIcon className="w-8 h-8 text-white" />
                            </div>
                          </a>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Comment;
