import { useTranslation } from 'react-i18next';
import { Languages } from 'lucide-react';

export default function LanguageSelector() {
  console.log('LanguageSelector rendering...');
  const { i18n, t } = useTranslation();
  console.log('t function:', typeof t);

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
  };

  return (
    <div className="dropdown dropdown-end">
      <label tabIndex={0} className="relative h-10 w-10 rounded-xl bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-300 flex items-center justify-center group cursor-pointer">
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500 to-red-600 opacity-0 group-hover:opacity-20 transition-opacity duration-300"></div>
        <Languages className="w-5 h-5 text-gray-700 dark:text-gray-300 group-hover:scale-110 transition-transform duration-300" />
        <span className="absolute -bottom-1 -right-1 flex h-4 w-5 items-center justify-center rounded-md bg-red-600 text-[8px] font-bold text-white shadow-sm border border-white dark:border-gray-900">
          {(i18n.language || 'fr').toUpperCase()}
        </span>
      </label>
      <ul
        tabIndex={0}
        className="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-32 z-[100]"
      >
        <li>
          <button
            type="button"
            className={`flex items-center gap-2 ${i18n.language === 'fr' ? 'active' : ''}`}
            onClick={() => changeLanguage('fr')}
          >
            🇫🇷 {t && t('language.french')}
          </button>
        </li>
        <li>
          <button
            type="button"
            className={`flex items-center gap-2 ${i18n.language === 'en' ? 'active' : ''}`}
            onClick={() => changeLanguage('en')}
          >
            🇬🇧 {t && t('language.english')}
          </button>
        </li>
      </ul>
    </div>
  );
}