import { Classes, Tree } from '@blueprintjs/core';
import { Selection } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { ProgressTag } from '../../../../../../../components/ProgressTag/ProgressTag';
import { selectCourse } from '../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../modules/courseChapterSelectors';
import * as courseChapterActions from '../../modules/courseChapterActions';

import './CourseChapterSidebar.scss';

class CourseChapterSidebar extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetChapters(this.props.course.jid);
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { course, courseChapter } = this.props;
    const { data: chapters, chaptersMap, chapterProgressesMap } = response;

    const contents = chapters.map(chapter => ({
      id: chapter.alias,
      label: (
        <a href={`/courses/${course.slug}/chapters/${chapter.alias}`}>
          {chapter.alias}. {chaptersMap[chapter.chapterJid].name}
        </a>
      ),
      secondaryLabel: this.renderProgress(chapterProgressesMap[chapter.chapterJid]),
      icon: <Selection className={Classes.TREE_NODE_ICON} />,
      isSelected: chapter.chapterJid === courseChapter.chapterJid,
    }));

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
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  courseChapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetChapters: courseChapterActions.getChapters,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(CourseChapterSidebar));
