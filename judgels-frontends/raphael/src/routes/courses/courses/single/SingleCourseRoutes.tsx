import * as React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { Course, CourseUpdateData } from '../../../../modules/api/jerahmeel/course';
import { AppState } from '../../../../modules/store';
import { CourseEditDialog } from '../CourseEditDialog/CourseEditDialog';
import CourseChaptersPage from './chapters/CourseChaptersPage/CourseChaptersPage';
import { UserRole } from '../../../../modules/api/jophiel/role';
import { selectRole } from '../../../jophiel/modules/userWebSelectors';
import { JerahmeelRole } from '../../../../modules/api/jerahmeel/role';
import { selectCourse } from '../modules/courseSelectors';
import * as courseActions from '../modules/courseActions';

import './SingleCourseRoutes.css';

interface SingleCourseRoutesProps extends RouteComponentProps<{ courseSlug: string }> {
  course?: Course;
  role?: UserRole;
  onUpdateCourse: (courseJid: string, courseSlug: string, data: CourseUpdateData) => Promise<void>;
}

const SingleCourseRoutes = (props: SingleCourseRoutesProps) => {
  const { course, role, onUpdateCourse } = props;

  // Optimization:
  // We wait until we get the course from the backend only if the current slug is different from the persisted one.
  if (!course || course.slug !== props.match.params.courseSlug) {
    return <LoadingState large />;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'properties',
      title: 'Chapters',
      routeComponent: Route,
      component: CourseChaptersPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Course Menu',
    items: sidebarItems,
    contentHeader: (
      <div className="single-course-routes__header">
        <h2 className="single-course-routes__title">{course.name}</h2>
        {role.jerahmeel === JerahmeelRole.Admin && (
          <div className="single-course-routes__button">
            <CourseEditDialog course={course} onUpdateCourse={onUpdateCourse} />
          </div>
        )}
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

const mapStateToProps = (state: AppState) => ({
  course: selectCourse(state),
  role: selectRole(state),
});

const mapDispatchToProps = {
  onUpdateCourse: courseActions.updateCourse,
};

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleCourseRoutes));
