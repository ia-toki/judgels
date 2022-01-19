import { Layers, Manual, ManuallyEnteredData, Presentation } from '@blueprintjs/icons';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import ContentWithTopbar from '../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterSubmissionRoutes from './submissions/ChapterSubmissionRoutes';
import ChapterItemSubmissionRoutes from './results/ChapterItemSubmissionRoutes';

import './SingleCourseChapterRoutes.scss';

function SingleCourseChapterRoutes({ chapter, match }) {
  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== match.params.courseSlug || chapter.alias !== match.params.chapterAlias) {
    return <LoadingState large />;
  }

  const topbarItems = [
    {
      id: 'lessons',
      titleIcon: <Presentation />,
      title: 'Lessons',
      routeComponent: Route,
      component: ChapterLessonRoutes,
    },
    {
      id: 'problems',
      titleIcon: <Manual />,
      title: 'Problems',
      routeComponent: Route,
      component: ChapterProblemRoutes,
    },
    {
      id: 'results',
      titleIcon: <ManuallyEnteredData />,
      title: 'Quiz Results',
      routeComponent: Route,
      component: ChapterItemSubmissionRoutes,
    },
    {
      id: 'submissions',
      titleIcon: <Layers />,
      title: 'Submissions',
      routeComponent: Route,
      component: ChapterSubmissionRoutes,
    },
  ];

  const contentWithTopbarProps = {
    className: 'single-course-chapter-routes',
    contentHeader: (
      <h2>
        {chapter.alias}. {chapter.name}
      </h2>
    ),
    items: topbarItems,
  };

  return <ContentWithTopbar {...contentWithTopbarProps} />;
}

const mapStateToProps = state => ({
  chapter: selectCourseChapter(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseChapterRoutes));
