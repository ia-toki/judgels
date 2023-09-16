import { Intent } from '@blueprintjs/core';
import { SendMessage } from '@blueprintjs/icons';
import { connect } from 'react-redux';

import { ButtonLink } from '../../../../../components/ButtonLink/ButtonLink';
import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { selectCourse } from '../../modules/courseSelectors';
import { selectCourseChapters } from '../chapters/modules/courseChaptersSelectors';

import './CourseOverview.scss';

function CourseOverview({ course, chapters }) {
  const renderStartButton = () => {
    if (!chapters || chapters.length === 0) {
      return null;
    }
    return (
      <ButtonLink intent={Intent.WARNING} to={`/courses/${course.slug}/chapters/${chapters[0].alias}`}>
        Start first chapter&nbsp;&nbsp;
        <SendMessage />
      </ButtonLink>
    );
  };

  return (
    <div className="course-overview">
      <h2>{course.name}</h2>
      <hr />
      <HtmlText>{course.description}</HtmlText>
      {renderStartButton()}
    </div>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapters: selectCourseChapters(state),
});

export default connect(mapStateToProps)(CourseOverview);
