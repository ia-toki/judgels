import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';
import { Course } from '../../../../../../modules/api/jerahmeel/course';
import { Chapter } from '../../../../../../modules/api/jerahmeel/chapter';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

import { selectCourse } from '../../../modules/courseSelectors';
import { courseChapterActions as injectedCourseChapterActions } from '../modules/courseChapterActions';

export interface SingleCourseChapterDataRouteProps
  extends RouteComponentProps<{ courseSlug: string; chapterAlias: string }> {
  course?: Course;

  onClearChapter: () => void;
  onGetChapter: (courseJid: string, courseSlug: string, chapterAlias: string) => Promise<Chapter>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleCourseChapterDataRoute extends React.Component<SingleCourseChapterDataRouteProps> {
  async componentDidMount() {
    await this.refresh();
  }

  async componentDidUpdate(prevProps: SingleCourseChapterDataRouteProps) {
    if ((prevProps.course && prevProps.course.jid) !== (this.props.course && this.props.course.jid)) {
      this.props.onPopBreadcrumb(this.props.match.url);
      await this.refresh();
    }
  }

  componentWillUnmount() {
    this.props.onClearChapter();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }

  refresh = async () => {
    const { course, match } = this.props;
    if (!course || course.slug !== match.params.courseSlug) {
      return;
    }
    const chapter = await this.props.onGetChapter(course.jid, course.slug, match.params.chapterAlias);
    this.props.onPushBreadcrumb(this.props.match.url, `${this.props.match.params.chapterAlias}. ${chapter.name}`);
  };
}

export function createSingleCourseChapterDataRoute(courseChapterActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
  });

  const mapDispatchToProps = {
    onGetChapter: courseChapterActions.getChapter,
    onClearChapter: courseChapterActions.clearChapter,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleCourseChapterDataRoute));
}

export default createSingleCourseChapterDataRoute(injectedCourseChapterActions, injectedBreadcrumbsActions);
