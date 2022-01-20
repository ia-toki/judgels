import { Classes, Tree } from '@blueprintjs/core';
import { InfoSign, Selection } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
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

    let contents = [
      {
        id: 0,
        label: <Link to={`/courses/${course.slug}`}>Overview</Link>,
        icon: <InfoSign className={Classes.TREE_NODE_ICON} />,
        isSelected: !activeChapterJid,
      },
    ];

    contents = contents.concat(
      courseChapters.map(courseChapter => ({
        id: courseChapter.alias,
        label: (
          <Link to={`/courses/${course.slug}/chapters/${courseChapter.alias}`}>
            {courseChapter.alias}. {chaptersMap[courseChapter.chapterJid].name}
            {this.renderProgressBar(chapterProgressesMap[courseChapter.chapterJid])}
          </Link>
        ),
        secondaryLabel: this.renderProgress(chapterProgressesMap[courseChapter.chapterJid]),
        icon: <Selection className={Classes.TREE_NODE_ICON} />,
        isSelected: courseChapter.chapterJid === activeChapterJid,
      }))
    );

    return (
      <ContentCard>
        <h4 className="course-chapter-sidebar__title">{course.name}</h4>
        <hr />
        <Tree className="course-chapter-sidebar__chapters" contents={contents} />
      </ContentCard>
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
