import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import CourseChaptersPage from './chapters/CourseChaptersPage/CourseChaptersPage';
import { selectCourse } from '../modules/courseSelectors';

import './SingleCourseRoutes.scss';

function SingleCourseRoutes({ match, course }) {
  // Optimization:
  // We wait until we get the course from the backend only if the current slug is different from the persisted one.
  if (!course || course.slug !== match.params.courseSlug) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'properties',
      title: 'Chapters',
      routeComponent: Route,
      component: CourseChaptersPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Course Menu',
    items: sidebarItems,
    contentHeader: (
      <div className="single-course-routes__header">
        <h2 className="single-course-routes__title">{course.name}</h2>
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
  course: selectCourse(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseRoutes));
