import { ChevronDown } from '@blueprintjs/icons';
import classNames from 'classnames';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { ProgressTag } from '../../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../../components/ProgressBar/ProgressBar';
import { selectCourse } from '../../modules/courseSelectors';
import { selectCourseChapter } from '../chapters/modules/courseChapterSelectors';
import * as courseChapterActions from '../chapters/modules/courseChapterActions';

import './CourseChaptersSidebar.scss';

class CourseChaptersSidebar extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetChapters(this.props.course.jid);
    this.setState({ response });
  }

  render() {
    const { course, chapter } = this.props;
    const { response } = this.state;
    if (!course || !response) {
      return null;
    }

    const { data: courseChapters, chaptersMap, chapterProgressesMap } = response;
    const activeChapterJid = chapter && chapter.jid;

    return (
      <div className="course-chapters-sidebar">
        <Link
          className={classNames('course-chapters-sidebar__item', 'course-chapters-sidebar__title', {
            'course-chapters-sidebar__item--selected': !activeChapterJid,
          })}
          to={`/courses/${course.slug}`}
        >
          <ChevronDown />
          <h4>{course.name}</h4>
        </Link>
        {courseChapters.map(courseChapter => (
          <Link
            className={classNames('course-chapters-sidebar__item', {
              'course-chapters-sidebar__item--selected': courseChapter.chapterJid === activeChapterJid,
            })}
            to={`/courses/${course.slug}/chapters/${courseChapter.alias}`}
          >
            <div className="course-chapters-sidebar__item-title">
              {courseChapter.alias}. {chaptersMap[courseChapter.chapterJid].name}
              {this.renderProgress(chapterProgressesMap[courseChapter.chapterJid])}
            </div>
            {this.renderProgressBar(chapterProgressesMap[courseChapter.chapterJid])}
          </Link>
        ))}
      </div>
    );
  }

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
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetChapters: courseChapterActions.getChapters,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(CourseChaptersSidebar));
