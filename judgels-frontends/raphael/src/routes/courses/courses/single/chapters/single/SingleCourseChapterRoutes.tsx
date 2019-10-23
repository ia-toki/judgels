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
import { Chapter } from '../../../../../../modules/api/jerahmeel/chapter';
import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';
import { AppState } from '../../../../../../modules/store';

import ChapterLessonRoutes from './lessons/ChapterLessonRoutes';

import { selectCourseChapter } from '../modules/courseChapterSelectors';

import './SingleCourseChapterRoutes.css';

interface SingleCourseChapterRoutesProps extends RouteComponentProps<{ chapterAlias: string }> {
  courseChapter?: CourseChapter;
  chapter?: Chapter;
}

const SingleCourseChapterRoutes = (props: SingleCourseChapterRoutesProps) => {
  const { courseChapter, chapter } = props;

  // Optimization:
  // We wait until we get the chapter from the backend only if the current alias is different from the persisted one.
  if (!chapter || courseChapter.alias !== props.match.params.chapterAlias) {
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
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Chapter Menu',
    items: sidebarItems,
    contentHeader: (
      <div className="single-course-chapter-routes__header">
        <h2 className="single-course-chapter-routes__title">
          {courseChapter.alias}. {chapter.name}
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
  const mapStateToProps = (state: AppState) => selectCourseChapter(state);

  return withRouter<any, any>(connect(mapStateToProps)(SingleCourseChapterRoutes));
}

export default createSingleCourseChapterRoutes();
