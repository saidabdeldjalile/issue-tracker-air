import axios from "axios";
import { useState, type FormEvent } from "react";
import { Link, useLocation } from "react-router-dom";
import { toast } from "react-toastify";
import config from "./config";
import { Lock, Eye, EyeOff, ArrowLeft, CheckCircle } from "lucide-react";

interface ResetPasswordFormData {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

function ResetPassword() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const tokenFromUrl = searchParams.get("token") || "";

  const [formData, setFormData] = useState<ResetPasswordFormData>({
    token: tokenFromUrl,
    newPassword: "",
    confirmPassword: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitting(true);

    if (formData.newPassword.length < 8) {
      toast.error("Le mot de passe doit contenir au moins 8 caractères");
      setIsSubmitting(false);
      return;
    }

    if (formData.newPassword !== formData.confirmPassword) {
      toast.error("Les mots de passe ne correspondent pas");
      setIsSubmitting(false);
      return;
    }

    try {
      const res = await axios.post(
        `${config.authUrl}/reset-password`,
        {
          token: formData.token,
          newPassword: formData.newPassword,
          confirmPassword: formData.confirmPassword,
        },
        {
          headers: { "Content-Type": "application/json" },
          timeout: 10000,
        }
      );

      if (res.data?.message) {
        setIsSuccess(true);
        toast.success(res.data.message);
      }
    } catch (err: any) {
      console.error("❌ Reset password error:", err);
      const errorMessage = err.response?.data?.error || "Échec de la réinitialisation";
      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="min-h-screen relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-red-950 to-gray-900">
          <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff05_1px,transparent_1px),linear-gradient(to_bottom,#ffffff05_1px,transparent_1px)] bg-[size:32px_32px]"></div>
        </div>

        <div className="relative min-h-screen flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-white/5 backdrop-blur-xl rounded-3xl p-8 border border-white/20 text-center">
            <div className="mb-6">
              <div className="w-16 h-16 bg-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="w-8 h-8 text-white" />
              </div>
              <h2 className="text-2xl font-bold text-white">Mot de passe modifié</h2>
              <p className="text-white/70 mt-2">
                Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.
              </p>
            </div>
            <Link
              to="/login"
              className="inline-flex items-center gap-2 text-red-400 hover:text-red-300 font-semibold"
            >
              <ArrowLeft className="w-4 h-4" />
              Aller à la connexion
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen relative overflow-hidden">
      <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-red-950 to-gray-900">
        <div className="absolute inset-0 bg-[url('/images/GFC-login-background.jpg')] bg-cover bg-center opacity-10"></div>
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff05_1px,transparent_1px),linear-gradient(to_bottom,#ffffff05_1px,transparent_1px)] bg-[size:32px_32px]"></div>
      </div>

      <div className="relative min-h-screen flex items-center justify-center p-4">
        <div className="w-full max-w-md bg-white/5 backdrop-blur-xl rounded-3xl p-8 border border-white/20">
          <div className="mb-8 text-center">
            <div className="w-16 h-16 bg-red-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <Lock className="w-8 h-8 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-white">Nouveau mot de passe</h2>
            <p className="text-white/70 mt-2">
              Entrez votre nouveau mot de passe
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <Lock className="w-5 h-5" />
              </div>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Nouveau mot de passe"
                className="w-full h-12 pl-11 pr-12 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-700 focus:border-red-600 focus:ring-2 focus:ring-red-600/10 transition-all"
                value={formData.newPassword}
                onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>

            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <Lock className="w-5 h-5" />
              </div>
              <input
                type={showConfirmPassword ? "text" : "password"}
                placeholder="Confirmer le mot de passe"
                className="w-full h-12 pl-11 pr-12 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-700 focus:border-red-600 focus:ring-2 focus:ring-red-600/10 transition-all"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                required
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>

            <div className="text-xs text-gray-500 dark:text-gray-400">
              Le mot de passe doit contenir au moins 8 caractères
            </div>

            <button
              type="submit"
              className="w-full h-12 bg-red-600 hover:bg-red-700 text-white font-bold rounded-xl transition-all duration-300 transform hover:scale-[1.01] shadow-lg shadow-red-600/20 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  Modification...
                </>
              ) : (
                <>
                  Modifier le mot de passe
                </>
              )}
            </button>

            <div className="text-center">
              <Link
                to="/login"
                className="inline-flex items-center gap-2 text-red-400 hover:text-red-300 font-semibold"
              >
                <ArrowLeft className="w-4 h-4" />
                Retour à la connexion
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ResetPassword;