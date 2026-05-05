import axios from "axios";
import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import config from "./config";
import { useTranslation } from "react-i18next";
import { Mail, ArrowLeft, Send } from "lucide-react";

function ForgotPassword() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitting(true);

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      toast.error(t("forgotPassword.errors.validEmail"));
      setIsSubmitting(false);
      return;
    }

    try {
      const res = await axios.post(
        `${config.authUrl}/forgot-password`,
        { email },
        {
          headers: { "Content-Type": "application/json" },
          timeout: 10000,
        }
      );

      if (res.data?.resetLink) {
        // Email not configured - redirect to reset page with token
        toast.success("Lien de réinitialisation prêt");
        // Extract token from the reset link and redirect
        const token = res.data.resetLink.includes("token=")
          ? res.data.resetLink.split("token=")[1].split("&")[0]
          : res.data.resetLink;
        navigate(`/reset-password?token=${token}`);
        return;
      }

      if (res.data?.message) {
        toast.success(res.data.message);
      }
      setEmailSent(true);
    } catch (err: any) {
      console.error("❌ Forgot password error:", err);
      toast.success("Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.");
      setEmailSent(true);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (emailSent) {
    return (
      <div className="min-h-screen relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-red-950 to-gray-900">
          <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff05_1px,transparent_1px),linear-gradient(to_bottom,#ffffff05_1px,transparent_1px)] bg-[size:32px_32px]"></div>
        </div>

        <div className="relative min-h-screen flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-white/5 backdrop-blur-xl rounded-3xl p-8 border border-white/20 text-center">
            <div className="mb-6">
              <div className="w-16 h-16 bg-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <Mail className="w-8 h-8 text-white" />
              </div>
              <h2 className="text-2xl font-bold text-white">Email envoyé</h2>
              <p className="text-white/70 mt-2">
                Veuillez vérifier votre boîte email pour le lien de réinitialisation.
              </p>
            </div>
            <Link
              to="/login"
              className="inline-flex items-center gap-2 text-red-400 hover:text-red-300 font-semibold"
            >
              <ArrowLeft className="w-4 h-4" />
              Retour à la connexion
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
              <Mail className="w-8 h-8 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-white">Mot de passe oublié</h2>
            <p className="text-white/70 mt-2">
              Entrez votre email pour recevoir un lien de réinitialisation
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <Mail className="w-5 h-5" />
              </div>
              <input
                type="email"
                placeholder="Adresse email"
                className="w-full h-12 pl-11 pr-4 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-700 focus:border-red-600 focus:ring-2 focus:ring-red-600/10 transition-all"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="w-full h-12 bg-red-600 hover:bg-red-700 text-white font-bold rounded-xl transition-all duration-300 transform hover:scale-[1.01] shadow-lg shadow-red-600/20 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  Envoi en cours...
                </>
              ) : (
                <>
                  Envoyer le lien
                  <Send className="w-4 h-4" />
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

export default ForgotPassword;