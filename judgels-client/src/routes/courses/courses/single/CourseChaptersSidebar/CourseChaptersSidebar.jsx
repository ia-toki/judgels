import { Popover, Position } from '@blueprintjs/core';
import { ChevronDown, ChevronRight, Menu } from '@blueprintjs/icons';
import classNames from 'classnames';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { ProgressTag } from '../../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../../components/ProgressBar/ProgressBar';
import { selectCourse } from '../../modules/courseSelectors';
import { PutCourseChapter } from '../chapters/modules/courseChapterReducer';
import * as courseChapterActions from '../chapters/modules/courseChapterActions';

import './CourseChaptersSidebar.scss';

class CourseChaptersSidebar extends Component {
  state = {
    response: undefined,
    isResponsivePopoverOpen: false,
  };

  async componentDidMount() {
    const response = await this.props.onGetChapters(this.props.course.jid);
    this.setState({ response });
  }

  componentDidUpdate() {}

  render() {
    return (
      <>
        <div
          className={classNames('course-chapters-sidebar', 'course-chapters-sidebar__full', {
            'course-chapters-sidebar--compact': this.isInProblemPath(),
            'course-chapters-sidebar--wide': !this.isInChaptersPath(),
          })}
        >
          {this.renderChapters({ showName: !this.isInProblemPath() })}
        </div>

        <div
          className={classNames('course-chapters-sidebar', 'course-chapters-sidebar__responsive', {
            'course-chapters-sidebar--wide': !this.isInChaptersPath(),
          })}
        >
          <Popover
            content={this.renderChapters({ showName: true })}
            position={Position.BOTTOM_LEFT}
            isOpen={this.state.isResponsivePopoverOpen}
            onInteraction={this.onResponsivePopoverInteraction}
            usePortal={false}
          >
            <p>
              <Menu />
              &nbsp;<small>Chapters Menu</small>
            </p>
          </Popover>
        </div>
      </>
    );
  }

  renderChapters = ({ showName }) => {
    const { course, match, onPutCourseChapter } = this.props;
    const { response } = this.state;
    if (!course || !response) {
      return null;
    }

    const { data: courseChapters, chaptersMap, chapterProgressesMap } = response;

    return courseChapters.map(courseChapter => (
      <Link
        className={classNames('course-chapters-sidebar__item', {
          'course-chapters-sidebar__item--selected': this.isInChapterPath(courseChapter.alias),
        })}
        to={`${match.url}/chapters/${courseChapter.alias}`}
        onClick={() => {
          onPutCourseChapter({
            jid: courseChapter.chapterJid,
            name: chaptersMap[courseChapter.chapterJid].name,
            alias: courseChapter.alias,
            courseSlug: course.slug,
          });

          if (this.state.isResponsivePopoverOpen) {
            this.onResponsiveItemClick();
          }
        }}
      >
        <div className="course-chapters-sidebar__item-title">
          {courseChapter.alias} {showName && <>. {chaptersMap[courseChapter.chapterJid].name}</>}
          &nbsp;&nbsp;
          {this.renderProgress(chapterProgressesMap[courseChapter.chapterJid])}
        </div>
        {!this.isInProblemPath() && this.renderProgressBar(chapterProgressesMap[courseChapter.chapterJid])}
      </Link>
    ));
  };

  isInChaptersPath = () => {
    return this.props.location.pathname.includes('/chapters/');
  };

  isInChapterPath = chapterAlias => {
    return (this.props.location.pathname + '/')
      .replace('//', '/')
      .startsWith(this.props.match.url + '/chapters/' + chapterAlias);
  };

  isInProblemPath = () => {
    return this.props.location.pathname.includes('/problems/');
  };

  renderProgress = progress => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;
    return (
      <ProgressTag num={solvedProblems} denom={totalProblems}>
        {solvedProblems} / {totalProblems}
      </ProgressTag>
    );
  };

  renderProgressBar = progress => {
    if (!progress) {
      return null;
    }
    return <ProgressBar num={progress.solvedProblems} denom={progress.totalProblems} />;
  };

  onResponsivePopoverInteraction = state => {
    this.setState({ isResponsivePopoverOpen: state });
  };

  onResponsiveItemClick = () => {
    setTimeout(() => {
      this.setState({ isResponsivePopoverOpen: false });
    }, 200);
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

const mapDispatchToProps = {
  onGetChapters: courseChapterActions.getChapters,
  onPutCourseChapter: PutCourseChapter,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(CourseChaptersSidebar));
