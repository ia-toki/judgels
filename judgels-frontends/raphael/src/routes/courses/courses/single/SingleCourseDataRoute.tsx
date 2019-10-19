import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../modules/store';
import { Course } from '../../../../modules/api/jerahmeel/course';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../modules/breadcrumbs/breadcrumbsActions';

import { selectCourse } from '../modules/courseSelectors';
import { courseActions as injectedCourseActions } from '../modules/courseActions';

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

export function createSingleCourseDataRoute(courseActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      course: selectCourse(state),
    } as Partial<SingleCourseDataRouteProps>);

  const mapDispatchToProps = {
    onGetCourseBySlug: courseActions.getCourseBySlug,
    onClearCourse: courseActions.clearCourse,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleCourseDataRoute));
}

export default createSingleCourseDataRoute(injectedCourseActions, injectedBreadcrumbsActions);
