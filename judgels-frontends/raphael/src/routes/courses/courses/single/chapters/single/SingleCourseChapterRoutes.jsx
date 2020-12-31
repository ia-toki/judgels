import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectCourseChapter, selectCourseChapterName, selectCourseSlug } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterSubmissionRoutes from './submissions/ChapterSubmissionRoutes';
import ChapterItemSubmissionRoutes from './results/ChapterItemSubmissionRoutes';

import './SingleCourseChapterRoutes.css';

function SingleCourseChapterRoutes({ chapter, chapterName, courseSlug, match }) {
  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || courseSlug !== match.params.courseSlug || chapter.alias !== match.params.chapterAlias) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      id: 'lessons',
      titleIcon: 'presentation',
      title: 'Lessons',
      routeComponent: Route,
      component: ChapterLessonRoutes,
    },
    {
      id: 'problems',
      titleIcon: 'manual',
      title: 'Problems',
      routeComponent: Route,
      component: ChapterProblemRoutes,
    },
    {
      id: 'results',
      titleIcon: 'manually-entered-data',
      title: 'Quiz Results',
      routeComponent: Route,
      component: ChapterItemSubmissionRoutes,
    },
    {
      id: 'submissions',
      titleIcon: 'layers',
      title: 'Submissions',
      routeComponent: Route,
      component: ChapterSubmissionRoutes,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Chapter Menu',
    items: sidebarItems,
    contentHeader: (
      <div className="single-course-chapter-routes__header">
        <h2 className="single-course-chapter-routes__title">
          {chapter.alias}. {chapterName}
        </h2>
        <div className="clearfix" />
      </div>
    ),
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

const mapStateToProps = state => ({
  chapter: selectCourseChapter(state),
  chapterName: selectCourseChapterName(state),
  courseSlug: selectCourseSlug(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseChapterRoutes));
