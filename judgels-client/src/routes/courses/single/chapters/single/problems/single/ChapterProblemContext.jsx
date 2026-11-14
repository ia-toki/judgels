import { createContext, useContext } from 'react';

export const ChapterProblemContext = createContext(null);

export function useChapterProblemContext() {
  const context = useContext(ChapterProblemContext);
  if (!context) {
    throw new Error('useChapterProblemContext must be used within a ChapterProblemProvider');
  }
  return context;
}
