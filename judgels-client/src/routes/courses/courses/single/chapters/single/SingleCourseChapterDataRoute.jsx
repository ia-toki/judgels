import { Component } from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import { selectCourse } from '../../../modules/courseSelectors';
import { selectCourseChapter } from '../modules/courseChapterSelectors';
import * as courseChapterActions from '../modules/courseChapterActions';
import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

class SingleCourseChapterDataRoute extends Component {
  async componentDidMount() {
    await this.refresh();
  }

  async componentDidUpdate(prevProps) {
    if ((prevProps.course && prevProps.course.jid) !== (this.props.course && this.props.course.jid)) {
      this.props.onPopBreadcrumb(this.props.match.url);
      await this.refresh();
    } else if (prevProps.match.url !== this.props.match.url) {
      this.props.onPopBreadcrumb(prevProps.match.url);
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

const mapStateToProps = state => ({
  course: selectCourse(state),
  courseChapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetChapter: courseChapterActions.getChapter,
  onClearChapter: courseChapterActions.clearChapter,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleCourseChapterDataRoute));
