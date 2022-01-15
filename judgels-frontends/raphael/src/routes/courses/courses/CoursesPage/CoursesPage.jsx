import { Callout, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { CourseCard } from '../CourseCard/CourseCard';
import * as courseActions from '../modules/courseActions';

import './CoursesPage.scss';

class CoursesPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetCourses();
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    const curriculumName = response ? response.curriculum.name : '';

    return (
      <Card className="courses-card" title={curriculumName}>
        {this.renderCourses()}
      </Card>
    );
  }

  renderCourses = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courses, curriculum, courseProgressesMap } = response;

    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return (
      <>
        <Callout intent={Intent.PRIMARY} icon={null}>
          <HtmlText>{curriculum.description}</HtmlText>
        </Callout>
        <hr />
        <div className="courses">
          {courses.map(course => (
            <CourseCard key={course.jid} course={course} progress={courseProgressesMap[course.jid]} />
          ))}
        </div>
      </>
    );
  };
}

const mapDispatchToProps = {
  onGetCourses: courseActions.getCourses,
};
export default connect(undefined, mapDispatchToProps)(CoursesPage);
