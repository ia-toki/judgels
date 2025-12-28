import { Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterLessonPage from './single/ChapterLessonPage/ChapterLessonPage.jsx';

function ChapterLessonRoutes() {
  return (
    <Routes>
      <Route path=":lessonAlias" element={<ChapterLessonPage />} />
    </Routes>
  );
}

export default withBreadcrumb('Lessons')(ChapterLessonRoutes);
