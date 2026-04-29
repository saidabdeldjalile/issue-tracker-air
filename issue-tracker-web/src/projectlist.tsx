import { AxiosResponse } from "axios";
import api from "./api/axios";
import useSWR, { mutate } from "swr";
import { Project } from "./TicketResponse";
import { Link } from "react-router-dom";
import AddProjectButton from "./components/addprojectbutton";
import CreateButton from "./components/createbutton";
import { toast } from "react-toastify";
import useAuth from "./hooks/useAuth";
import { useState, useMemo } from "react";
import { useTranslation } from "react-i18next";

interface PaginatedProjects {
  content: Project[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export default function ProjectList() {
  const { auth } = useAuth();
  const { t } = useTranslation();
  const userRole = auth?.role;
  const userDepartmentId = auth?.departmentId;
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState("");

  if (!auth?.token && !localStorage.getItem("token")) {
    return (
      <div className="flex h-96 flex-col items-center justify-center gap-4">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        <p className="text-sm font-medium animate-pulse">Chargement...</p>
      </div>
    );
  }

  const getProjectsUrl = () => {
    if (userRole === "ADMIN") {
      return `/projects`;
    }
    if (userDepartmentId) {
      return `/projects/by-department/${userDepartmentId}`;
    }
    return `/projects`;
  };

  const url = getProjectsUrl();
  const key = `project-${userRole}-${userDepartmentId}-${page}-${searchTerm}`;

  const fetcher = (fetchUrl: string) => {
    return api
      .get<PaginatedProjects>(fetchUrl, {
        params: { page, size: pageSize, search: searchTerm || undefined },
      })
      .then((res: AxiosResponse<PaginatedProjects>) => res.data);
  };

  const {
    data: paginatedProjects,
    error,
    isLoading,
  } = useSWR<PaginatedProjects | null>(key, () => fetcher(url));

  const projects = useMemo(() => paginatedProjects?.content || [], [paginatedProjects]);

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] gap-4">
        <div className="text-center">
          <svg className="w-16 h-16 mx-auto text-error mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p className="text-error text-lg mb-4">{t('common.errors.generic')}</p>
          <p className="text-base-content/60 mb-4">{t('projectList.noProjectsDesc')}</p>
          <button className="btn btn-primary" onClick={() => mutate(key)}>
            Réessayer
          </button>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex h-96 flex-col items-center justify-center gap-4">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        <p className="text-sm font-medium animate-pulse">Chargement des projets...</p>
      </div>
    );
  }

  function handledelete(id: number) {
    if (window.confirm(t('projectList.deleteConfirm'))) {
      api
        .delete(`/projects/${id}`)
        .then(() => {
          mutate(key);
          toast.success(t('projectList.deleteSuccess'));
        })
        .catch(() => {
          toast.error(t('projectList.deleteError'));
        });
    }
  }

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && (!paginatedProjects || newPage < paginatedProjects.totalPages)) {
      setPage(newPage);
    }
  };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setPage(0);
  };

  const totalProjects = paginatedProjects?.totalElements ?? projects.length;

  return (
    <div className="max-w-[1600px] mx-auto space-y-6 pb-20">
      {/* Header */}
      <div className="card bg-base-100 shadow-xl border border-base-200 overflow-hidden">
        <div className="bg-gradient-to-r from-red-600/10 via-red-800/5 to-gray-500/5 p-8">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <div className="inline-flex items-center gap-2 rounded-full border border-base-300/70 bg-base-100/80 px-4 py-2 text-xs font-bold uppercase tracking-[0.2em] text-base-content/60 shadow-sm mb-3">
                {t('projectList.workspace')}
              </div>
              <h1 className="text-3xl font-black tracking-tight">{t('projectList.title')}</h1>
              <p className="text-base-content/60 mt-1">{t('projectList.subtitle')}</p>
            </div>
            <div className="flex flex-wrap gap-3">
              <AddProjectButton />
              <CreateButton />
            </div>
          </div>
        </div>
      </div>

      {/* Search Bar */}
      <div className="bg-base-100 rounded-2xl shadow-sm border border-base-200 p-6">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div className="relative w-full lg:w-96">
            <svg
              className="pointer-events-none absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-base-content/40"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            <input
              type="text"
              placeholder="Rechercher un projet par nom ou département..."
              value={searchTerm}
              onChange={handleSearch}
              className="input input-bordered w-full pl-12 focus:ring-2 ring-primary/20"
            />
          </div>
          <div className="flex items-center gap-3">
            <span className="rounded-full border border-base-300/70 bg-base-100/80 px-4 py-2 text-sm shadow-sm">
              Total: {totalProjects} projet(s)
            </span>
          </div>
        </div>
      </div>

      {/* Projects Table */}
      <div className="bg-base-100 rounded-3xl shadow-sm border border-base-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="table table-zebra w-full">
            <thead className="bg-base-200/50">
              <tr>
                <th className="py-4">ID</th>
                <th>Nom du projet</th>
                <th>Département</th>
                <th className="text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {projects.map((project) => (
                <tr key={project.id} className="hover:bg-primary/5 transition-colors">
                  <td className="font-semibold">
                    <Link
                      to={`/projects/${project.id}/tickets`}
                      className="hover:text-primary transition-colors"
                    >
                      <span className="opacity-60">#</span>{project.id}
                    </Link>
                  </td>
                  <td className="font-medium">
                    <Link
                      to={`/projects/${project.id}/tickets`}
                      className="hover:text-primary transition-colors"
                    >
                      {project.name}
                    </Link>
                  </td>
                  <td>
                    <span className="badge badge-ghost font-medium">
                      {project.departmentName || "-"}
                    </span>
                  </td>
                  <td className="text-center">
                    <button 
                      className="btn btn-error btn-sm" 
                      onClick={() => handledelete(project.id)}
                    >
                      Supprimer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Empty State */}
          {projects.length === 0 && (
            <div className="flex flex-col items-center justify-center py-20 text-base-content/40">
              <svg
                className="w-24 h-24 mb-4 stroke-1"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1}
                  d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
                />
              </svg>
              <p className="text-xl font-medium">{t('projectList.noProjects')}</p>
              <p className="text-sm mt-2">
                {searchTerm ? "Aucun résultat ne correspond à votre recherche" : t('projectList.noProjectsDesc')}
              </p>
              <button 
                className="btn btn-primary btn-sm mt-6"
                onClick={() => {
                  // Ouvrir le modal d'ajout de projet
                  const addButton = document.querySelector('[data-add-project]') as HTMLButtonElement;
                  if (addButton) addButton.click();
                }}
              >
                Créer un projet
              </button>
            </div>
          )}
        </div>

        {/* Pagination */}
        {paginatedProjects && paginatedProjects.totalPages > 1 && (
          <div className="p-4 border-t border-base-200 flex items-center justify-between bg-base-50">
            <span className="text-sm opacity-60">
              Page {paginatedProjects.number + 1} / {paginatedProjects.totalPages}
            </span>
            <div className="join shadow-sm">
              <button
                className="join-item btn btn-sm"
                disabled={paginatedProjects.first}
                onClick={() => handlePageChange(page - 1)}
              >
                ← Précédent
              </button>
              <button
                className="join-item btn btn-sm"
                disabled={paginatedProjects.last}
                onClick={() => handlePageChange(page + 1)}
              >
                Suivant →
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}