import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import CourseChapterSidebar from './CourseChapterSidebar/CourseChapterSidebar';
import SingleCourseContentRoutes from './SingleCourseContentRoutes';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { selectCourse } from '../modules/courseSelectors';

import './SingleCourseRoutes.scss';

function SingleCourseRoutes({ match, course }) {
  // Optimization:
  // We wait until we get the course from the backend only if the current slug is different from the persisted one.
  if (!course || course.slug !== match.params.courseSlug) {
    return <LoadingState large />;
  }

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <div className="single-course-routes">
        <div className="single-course-routes__sidebar">
          <CourseChapterSidebar />
        </div>
        <div className="single-course-routes__content">
          <SingleCourseContentRoutes />
        </div>
      </div>
    </FullPageLayout>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseRoutes));
