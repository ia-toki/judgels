import { useSelector } from 'react-redux';
import { Route, Routes } from 'react-router';
import { useParams } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterResourcesPage from './resources/ChapterResourcesPage/ChapterResourcesPage';

export default function SingleCourseChapterRoutes() {
  const { courseSlug, chapterAlias } = useParams();
  const chapter = useSelector(selectCourseChapter);

  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== courseSlug || chapter.alias !== chapterAlias) {
    return <LoadingState large />;
  }

  return (
    <Routes>
      <Route index element={<ChapterResourcesPage />} />
      <Route path="lessons/*" element={<ChapterLessonRoutes />} />
      <Route path="problems/*" element={<ChapterProblemRoutes />} />
    </Routes>
  );
}
