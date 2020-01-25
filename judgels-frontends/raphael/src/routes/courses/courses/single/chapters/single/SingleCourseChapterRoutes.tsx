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
import { selectCourseChapter, selectCourseChapterName, selectCourseSlug } from '../modules/courseChapterSelectors';
import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';
import ChapterProblemRoutes from './problems/ChapterProblemRoutes';
import ChapterSubmissionRoutes from './submissions/ChapterSubmissionRoutes';
import ChapterItemSubmissionRoutes from './results/ChapterItemSubmissionRoutes';

import './SingleCourseChapterRoutes.css';

interface SingleCourseChapterRoutesProps extends RouteComponentProps<{ courseSlug: string; chapterAlias: string }> {
  chapter?: CourseChapter;
  chapterName?: string;
  courseSlug?: string;
}

const SingleCourseChapterRoutes = (props: SingleCourseChapterRoutesProps) => {
  const { chapter, chapterName, courseSlug, match } = props;

  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || courseSlug !== match.params.courseSlug || chapter.alias !== match.params.chapterAlias) {
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
    courseSlug: selectCourseSlug(state),
  });

  return withRouter<any, any>(connect(mapStateToProps)(SingleCourseChapterRoutes));
}

export default createSingleCourseChapterRoutes();
