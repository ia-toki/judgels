import { Route, Routes } from 'react-router-dom';

import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';
import ChapterProblemSubmissionPage from './single/ChapterProblemSubmissionPage/ChapterProblemSubmissionPage';

export default function ChapterProblemSubmissionRoutes() {
  return (
    <Routes>
      <Route index element={<ChapterProblemSubmissionsPage />} />
      <Route path="all" element={<ChapterProblemSubmissionsPage />} />
      <Route path=":submissionId" element={<ChapterProblemSubmissionPage />} />
    </Routes>
  );
}
