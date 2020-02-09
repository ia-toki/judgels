import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../modules/store';
import { Course } from '../../../../modules/api/jerahmeel/course';
import { selectCourse } from '../modules/courseSelectors';
import * as courseActions from '../modules/courseActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleCourseDataRouteProps extends RouteComponentProps<{ courseSlug: string }> {
  course?: Course;

  onClearCourse: () => void;
  onGetCourseBySlug: (courseJid: string) => Promise<Course>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleCourseDataRoute extends React.Component<SingleCourseDataRouteProps> {
  async componentDidMount() {
    const course = await this.props.onGetCourseBySlug(this.props.match.params.courseSlug);
    this.props.onPushBreadcrumb(this.props.match.url, course.name);
  }

  componentWillUnmount() {
    this.props.onClearCourse();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }
}

const mapStateToProps = (state: AppState) => ({
  course: selectCourse(state),
});

const mapDispatchToProps = {
  onGetCourseBySlug: courseActions.getCourseBySlug,
  onClearCourse: courseActions.clearCourse,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleCourseDataRoute));
