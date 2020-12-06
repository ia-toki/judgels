import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { CourseCard } from '../CourseCard/CourseCard';
import * as courseActions from '../modules/courseActions';

class CoursesPage extends React.Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetCourses();
    this.setState({ response });
  }

  render() {
    return <Card title="Courses">{this.renderCourses()}</Card>;
  }

  renderCourses = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courses, curriculumDescription, courseProgressesMap } = response;

    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return (
      <>
        <HtmlText>{curriculumDescription || ''}</HtmlText>
        <hr />
        {courses.map(course => (
          <CourseCard key={course.jid} course={course} progress={courseProgressesMap[course.jid]} />
        ))}
      </>
    );
  };
}

const mapDispatchToProps = {
  onGetCourses: courseActions.getCourses,
};
export default connect(undefined, mapDispatchToProps)(CoursesPage);
