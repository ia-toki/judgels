import * as React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';
import { AppState } from '../../../../../../modules/store';
import { selectCourseChapter, selectCourseChapterName } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterSubmissionRoutes from './submissions/ChapterSubmissionRoutes';
import ChapterSubmissionSummaryPage from './results/ChapterSubmissionSummaryPage/ChapterSubmissionSummaryPage';

import './SingleCourseChapterRoutes.css';

interface SingleCourseChapterRoutesProps extends RouteComponentProps<{ chapterAlias: string }> {
  chapter?: CourseChapter;
  chapterName?: string;
}

const SingleCourseChapterRoutes = (props: SingleCourseChapterRoutesProps) => {
  const { chapter, chapterName } = props;

  // Optimization:
  // We wait until we get the chapter from the backend only if the current alias is different from the persisted one.
  if (!chapter || chapter.alias !== props.match.params.chapterAlias) {
    return <LoadingState large />;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
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
      component: ChapterSubmissionSummaryPage,
    },
    {
      id: 'submissions',
      titleIcon: 'layers',
      title: 'Submissions',
      routeComponent: Route,
      component: ChapterSubmissionRoutes,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
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
};

function createSingleCourseChapterRoutes() {
  const mapStateToProps = (state: AppState) => ({
    chapter: selectCourseChapter(state),
    chapterName: selectCourseChapterName(state),
  });

  return withRouter<any, any>(connect(mapStateToProps)(SingleCourseChapterRoutes));
}

export default createSingleCourseChapterRoutes();
