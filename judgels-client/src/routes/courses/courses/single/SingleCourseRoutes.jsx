import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import CourseChaptersSidebar from './CourseChaptersSidebar/CourseChaptersSidebar';
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
    <FullWidthPageLayout>
      <ScrollToTopOnMount />
      <div className="single-course-routes">
        <CourseChaptersSidebar />
        <SingleCourseContentRoutes />
      </div>
    </FullWidthPageLayout>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

export default withRouter(connect(mapStateToProps)(SingleCourseRoutes));
