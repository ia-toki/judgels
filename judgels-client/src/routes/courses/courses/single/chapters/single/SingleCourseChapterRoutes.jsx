import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterResourcesPage from './resources/ChapterResourcesPage/ChapterResourcesPage';

function SingleCourseChapterRoutes({ chapter, match }) {
  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== match.params.courseSlug || chapter.alias !== match.params.chapterAlias) {
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

const mapStateToProps = state => ({
  chapter: selectCourseChapter(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseChapterRoutes));
