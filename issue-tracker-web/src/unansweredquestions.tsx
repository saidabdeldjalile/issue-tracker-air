import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import api from "./api/axios";

interface UnansweredQuestion {
  id: number;
  question: string;
  context: string;
  userEmail: string;
  suggestedCategory: string;
  suggestedDepartment: string;
  relatedTicketId: number;
  status: string;
  createdAt: string;
  resolvedAt: string;
}

const categories = [
  { value: "informatique", label: "Informatique" },
  { value: "materiel", label: "Matériel" },
  { value: "administratif", label: "Administratif" },
  { value: "maintenance", label: "Maintenance" },
  { value: "achat", label: "Achat" },
  { value: "formation", label: "Formation" },
  { value: "autres", label: "Autres" },
];

export default function UnansweredQuestionsPage() {
  const [questions, setQuestions] = useState<UnansweredQuestion[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedQuestion, setSelectedQuestion] = useState<UnansweredQuestion | null>(null);
  const [answer, setAnswer] = useState("");
  const [category, setCategory] = useState("");
  const [keywords, setKeywords] = useState("");

  useEffect(() => {
    fetchQuestions();
  }, []);

  const fetchQuestions = async () => {
    try {
      const response = await api.get("/unanswered-questions/pending");
      setQuestions(response.data);
    } catch (error) {
      console.error("Error fetching questions:", error);
      toast.error("Erreur lors du chargement des questions");
    } finally {
      setLoading(false);
    }
  };

  const handleAddToFAQ = async (id: number) => {
    if (!answer.trim()) {
      toast.error("La réponse est obligatoire");
      return;
    }
    try {
      await api.post(`/unanswered-questions/${id}/add-to-faq`, {
        answer,
        category: category || "autres",
        keywords,
      });
      toast.success("Question ajoutée à la FAQ!");
      fetchQuestions();
      setSelectedQuestion(null);
      setAnswer("");
      setCategory("");
      setKeywords("");
    } catch (error) {
      console.error("Error adding to FAQ:", error);
      toast.error("Erreur lors de l'ajout à la FAQ");
    }
  };

  const handleReject = async (id: number) => {
    try {
      await api.post(`/unanswered-questions/${id}/reject`, {});
      toast.success("Question rejetée");
      fetchQuestions();
    } catch (error) {
      console.error("Error rejecting question:", error);
      toast.error("Erreur lors du rejet");
    }
  };

  const getStatusBadge = (status: string) => {
    const badges: Record<string, string> = {
      PENDING: "badge-warning",
      REVIEWED: "badge-info",
      ADDED_TO_FAQ: "badge-success",
      REJECTED: "badge-error",
    };
    return badges[status] || "badge-ghost";
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Questions en attente de réponse</h1>
        <div className="badge badge-lg badge-neutral">
          {questions.length} question{questions.length !== 1 ? "s" : ""} en attente
        </div>
      </div>

      {questions.length === 0 ? (
        <div className="card bg-base-200 shadow-xl">
          <div className="card-body items-center text-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-16 w-16 text-success"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <h2 className="card-title text-success mt-4">Aucune question en attente!</h2>
            <p className="text-base-content/70">
              Toutes les questions ont été traitées.
            </p>
          </div>
        </div>
      ) : (
        <div className="grid gap-4">
          {questions.map((q) => (
            <div key={q.id} className="card bg-base-200 shadow-md">
              <div className="card-body">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <span className={`badge ${getStatusBadge(q.status)}`}>
                        {q.status}
                      </span>
                      {q.suggestedCategory && (
                        <span className="badge badge-outline badge-sm">
                          {q.suggestedCategory}
                        </span>
                      )}
                      {q.relatedTicketId && (
                        <span className="badge badge-outline badge-sm">
                          Ticket #{q.relatedTicketId}
                        </span>
                      )}
                    </div>
                    <h3 className="font-semibold text-lg">{q.question}</h3>
                    {q.context && (
                      <p className="text-sm text-base-content/70 mt-1">
                        Contexte: {q.context}
                      </p>
                    )}
                    <div className="text-xs text-base-content/50 mt-2">
                      Demandé par: {q.userEmail} | Date:{" "}
                      {new Date(q.createdAt).toLocaleDateString("fr-FR")}
                    </div>
                  </div>
                  <div className="flex flex-col gap-2">
                    <button
                      onClick={() => setSelectedQuestion(q)}
                      className="btn btn-primary btn-sm"
                    >
                      Ajouter à la FAQ
                    </button>
                    <button
                      onClick={() => handleReject(q.id)}
                      className="btn btn-outline btn-error btn-sm"
                    >
                      Rejeter
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedQuestion && (
        <dialog className="modal modal-open">
          <div className="modal-box max-w-2xl">
            <h3 className="font-bold text-lg mb-4">Ajouter à la FAQ</h3>
            <div className="bg-base-100 p-4 rounded-lg mb-4">
              <p className="font-medium">Question:</p>
              <p>{selectedQuestion.question}</p>
            </div>
            <div className="form-control mb-4">
              <label className="label">
                <span className="label-text">Réponse *</span>
              </label>
              <textarea
                className="textarea textarea-bordered"
                placeholder="Entrez la réponse qui sera affichée aux utilisateurs..."
                value={answer}
                onChange={(e) => setAnswer(e.target.value)}
                rows={4}
              ></textarea>
            </div>
            <div className="grid grid-cols-2 gap-4 mb-4">
              <div className="form-control">
                <label className="label">
                  <span className="label-text">Catégorie</span>
                </label>
                <select
                  className="select select-bordered"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <option value="">Sélectionner...</option>
                  {categories.map((cat) => (
                    <option key={cat.value} value={cat.value}>
                      {cat.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-control">
                <label className="label">
                  <span className="label-text">Mots-clés (séparés par virgule)</span>
                </label>
                <input
                  type="text"
                  className="input input-bordered"
                  placeholder="conge, attestation, rh"
                  value={keywords}
                  onChange={(e) => setKeywords(e.target.value)}
                />
              </div>
            </div>
            <div className="modal-action">
              <button
                onClick={() => handleAddToFAQ(selectedQuestion.id)}
                className="btn btn-primary"
              >
                Ajouter à la FAQ
              </button>
              <button
                onClick={() => setSelectedQuestion(null)}
                className="btn"
              >
                Annuler
              </button>
            </div>
          </div>
          <form method="dialog" className="modal-backdrop">
            <button onClick={() => setSelectedQuestion(null)}>close</button>
          </form>
        </dialog>
      )}
    </div>
  );
}