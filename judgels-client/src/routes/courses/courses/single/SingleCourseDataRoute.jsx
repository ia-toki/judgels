import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { selectCourse } from '../modules/courseSelectors';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseActions from '../modules/courseActions';

class SingleCourseDataRoute extends Component {
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

const mapStateToProps = state => ({
  course: selectCourse(state),
});

const mapDispatchToProps = {
  onGetCourseBySlug: courseActions.getCourseBySlug,
  onClearCourse: courseActions.clearCourse,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleCourseDataRoute));
