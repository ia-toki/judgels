import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { CoursesResponse } from '../../../../modules/api/jerahmeel/course';
import { CourseCard } from '../CourseCard/CourseCard';
import { LoadingCourseCard } from '../CourseCard/LoadingCourseCard';
import { courseActions as injectedCourseActions } from '../modules/courseActions';

export interface CoursePageProps {
  onGetCourses: () => Promise<CoursesResponse>;
}

export interface CoursesPageState {
  response?: Partial<CoursesResponse>;
}

class CoursesPage extends React.Component<CoursePageProps, CoursesPageState> {
  state: CoursesPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetCourses();
    this.setState({ response });
  }

  render() {
    return <Card title="Courses">{this.renderCourses()}</Card>;
  }

  private renderCourses = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingCourseCard />;
    }

    const courses = response.data;
    if (!courses) {
      return <LoadingCourseCard />;
    }

    return courses.map(course => <CourseCard key={course.jid} course={course} />);
  };
}

export function createCoursesPage(courseActions) {
  const mapDispatchToProps = {
    onGetCourses: courseActions.getCourses,
  };
  return connect(undefined, mapDispatchToProps)(CoursesPage);
}

export default createCoursesPage(injectedCourseActions);
