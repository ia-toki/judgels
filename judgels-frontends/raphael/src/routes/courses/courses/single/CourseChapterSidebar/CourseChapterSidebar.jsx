import { Classes, Tree } from '@blueprintjs/core';
import { InfoSign, Selection } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { ProgressTag } from '../../../../../components/ProgressTag/ProgressTag';
import { selectCourse } from '../../modules/courseSelectors';
import { selectCourseChapter } from '../chapters/modules/courseChapterSelectors';
import * as courseChapterActions from '../chapters/modules/courseChapterActions';

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
    const { course, courseChapter } = this.props;
    const { response } = this.state;
    if (!course || !response) {
      return null;
    }

    const { data: chapters, chaptersMap, chapterProgressesMap } = response;
    const activeChapterJid = courseChapter && courseChapter.chapterJid;

    let contents = [
      {
        id: 0,
        label: <Link to={`/courses/${course.slug}`}>Overview</Link>,
        icon: <InfoSign className={Classes.TREE_NODE_ICON} />,
        isSelected: !activeChapterJid,
      },
    ];

    contents = contents.concat(
      chapters.map(chapter => ({
        id: chapter.alias,
        label: (
          <Link to={`/courses/${course.slug}/chapters/${chapter.alias}`}>
            {chapter.alias}. {chaptersMap[chapter.chapterJid].name}
          </Link>
        ),
        secondaryLabel: this.renderProgress(chapterProgressesMap[chapter.chapterJid]),
        icon: <Selection className={Classes.TREE_NODE_ICON} />,
        isSelected: chapter.chapterJid === activeChapterJid,
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
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  courseChapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetChapters: courseChapterActions.getChapters,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(CourseChapterSidebar));
