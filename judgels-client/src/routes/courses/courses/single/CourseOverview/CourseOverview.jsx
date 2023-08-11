import { connect } from 'react-redux';

import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { selectCourse } from '../../modules/courseSelectors';

import './CourseOverview.scss';

function CourseOverview({ course }) {
  return (
    <div className="course-overview">
      <h2>{course.name}</h2>
      <hr />
      <HtmlText>{course.description}</HtmlText>
    </div>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

export default connect(mapStateToProps)(CourseOverview);
