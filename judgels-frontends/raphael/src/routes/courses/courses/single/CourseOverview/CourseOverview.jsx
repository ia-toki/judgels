import { connect } from 'react-redux';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { selectCourse } from '../../modules/courseSelectors';

import './CourseOverview.scss';

function CourseOverview({ course }) {
  return (
    <div className="course-overview">
      <h2>{course.name}</h2>
      <hr />
      <ContentCard>
        <HtmlText>{course.description}</HtmlText>
      </ContentCard>
    </div>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

export default connect(mapStateToProps)(CourseOverview);
