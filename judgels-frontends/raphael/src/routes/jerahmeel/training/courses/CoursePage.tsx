// import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import { Card } from 'components/Card/Card';
import { withBreadcrumb } from 'components/BreadcrumbWrapper/BreadcrumbWrapper';
import { CourseResponse } from 'modules/api/jerahmeel/course';

import { courseActions as injectedCourseActions } from './modules/courseActions';

export interface CoursePageProps extends RouteComponentProps<{ name: string }> {
  onGetCourse: (name?: string, page?: number) => Promise<CourseResponse>;
}

// interface CourseFilter {
//   name?: string;
// }

export interface CoursePageState {
  response?: Partial<CourseResponse>;
  // filter?: CourseFilter;
  // isFilterLoading?: boolean;
}

class CoursePage extends React.Component<CoursePageProps, CoursePageState> {
  render() {
    return (
      <Card title="Courses">
        {/* {this.renderHeader()}
        {this.renderContests()}
        {this.renderPagination()} */}
      </Card>
    );
  }
}

// export default withBreadcrumb('Courses')(CoursePage);

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetContests: contestActions.getContests,
  };
  return connect(undefined, mapDispatchToProps)(withBreadcrumb('Courses')(CoursePage));
}

export default createContestsPage(injectedCourseActions);
