import { createContext, useContext, useEffect, useState } from 'react';

import { languageDisplayNamesMap } from './api/sandalphon/language';

import * as toastActions from './toast/toastActions';

const STORAGE_KEY = 'webPrefs';

const defaultPrefs = {
  isDarkMode: true,
  hideProblemDifficulty: undefined,
  showProblemTopicTags: undefined,
  statementLanguage: 'id',
  editorialLanguage: 'id',
  gradingLanguage: 'Cpp20',
};

function loadFromStorage() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? { ...defaultPrefs, ...JSON.parse(stored) } : defaultPrefs;
  } catch {
    return defaultPrefs;
  }
}

// Module-level state for non-React access (thunks)
let currentPrefs = loadFromStorage();

const WebPrefsContext = createContext(null);
const WebPrefsSetContext = createContext(null);

export function WebPrefsProvider({ initialPrefs, children }) {
  const [prefs, setPrefs] = useState(() => initialPrefs ?? loadFromStorage());

  useEffect(() => {
    currentPrefs = prefs;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(prefs));
  }, [prefs]);

  return (
    <WebPrefsContext.Provider value={prefs}>
      <WebPrefsSetContext.Provider value={setPrefs}>{children}</WebPrefsSetContext.Provider>
    </WebPrefsContext.Provider>
  );
}

export function useWebPrefs() {
  const prefs = useContext(WebPrefsContext);
  const setPrefs = useContext(WebPrefsSetContext);
  return {
    ...prefs,
    setIsDarkMode: val => setPrefs(old => ({ ...old, isDarkMode: val })),
    setShowProblemDifficulty: val => setPrefs(old => ({ ...old, hideProblemDifficulty: !val })),
    setShowProblemTopicTags: val => setPrefs(old => ({ ...old, showProblemTopicTags: val })),
    setStatementLanguage: lang => {
      setPrefs(old => ({ ...old, statementLanguage: lang }));
      toastActions.showSuccessToast('Switched default statement language to ' + languageDisplayNamesMap[lang] + '.');
    },
    setEditorialLanguage: lang => {
      setPrefs(old => ({ ...old, editorialLanguage: lang }));
      toastActions.showSuccessToast('Switched default editorial language to ' + languageDisplayNamesMap[lang] + '.');
    },
    setGradingLanguage: lang => setPrefs(old => ({ ...old, gradingLanguage: lang })),
  };
}

export function getWebPrefs() {
  return currentPrefs;
}
