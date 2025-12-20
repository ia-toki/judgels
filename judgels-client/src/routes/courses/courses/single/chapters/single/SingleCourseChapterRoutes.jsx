import { useSelector } from 'react-redux';
import { Route, Switch } from 'react-router';
import { useParams, useRouteMatch } from 'react-router-dom';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterResourcesPage from './resources/ChapterResourcesPage/ChapterResourcesPage';

export default function SingleCourseChapterRoutes() {
  const { courseSlug, chapterAlias } = useParams();
  const match = useRouteMatch();
  const chapter = useSelector(selectCourseChapter);

  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== courseSlug || chapter.alias !== chapterAlias) {
    return <LoadingState large />;
  }

  return (
    <Switch>
      <Route exact path={match.url} component={ChapterResourcesPage} />
      <Route path={`${match.url}/lessons`} component={ChapterLessonRoutes} />
      <Route path={`${match.url}/problems`} component={ChapterProblemRoutes} />
    </Switch>
  );
}
