import { useSelector } from 'react-redux';
import { Outlet, useParams } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import SingleCourseChapterDataLayout from './SingleCourseChapterDataLayout';
import ChapterLessonLayout, { chapterLessonRoutes } from './lessons/ChapterLessonRoutes';
import ChapterProblemLayout, { chapterProblemRoutes } from './problems/ChapterProblemRoutes';
import ChapterResourcesPage from './resources/ChapterResourcesPage/ChapterResourcesPage';

export const singleCourseChapterRoutes = [
  {
    index: true,
    element: <ChapterResourcesPage />,
  },
  {
    path: 'lessons',
    element: <ChapterLessonLayout />,
    children: chapterLessonRoutes,
  },
  {
    path: 'problems',
    element: <ChapterProblemLayout />,
    children: chapterProblemRoutes,
  },
];

export function SingleCourseChapterLayout() {
  return (
    <>
      <SingleCourseChapterDataLayout />
      <MainSingleCourseChapterLayout />
    </>
  );
}

function MainSingleCourseChapterLayout() {
  const { courseSlug, chapterAlias } = useParams();
  const chapter = useSelector(selectCourseChapter);

  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== courseSlug || chapter.alias !== chapterAlias) {
    return <LoadingState large />;
  }

  return <Outlet />;
}
