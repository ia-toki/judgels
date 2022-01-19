import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import { CourseChapterCard } from '../CourseChapterCard/CourseChapterCard';
import { selectCourse } from '../../../modules/courseSelectors';
import * as courseChapterActions from '../modules/courseChapterActions';

export class CourseChaptersPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetChapters(this.props.course.jid);
    this.setState({ response });
  }

  render() {
    return (
      <ContentCard>
        <h3>Chapters</h3>
        <hr />
        {this.renderChapters()}
      </ContentCard>
    );
  }

  renderChapters = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courseChapters, chaptersMap, chapterProgressesMap } = response;

    if (courseChapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    return courseChapters.map(courseChapter => {
      const props = {
        course: this.props.course,
        chapter: courseChapter,
        chapterName: chaptersMap[courseChapter.chapterJid].name,
        progress: chapterProgressesMap[courseChapter.chapterJid],
      };
      return <CourseChapterCard key={courseChapter.chapterJid} {...props} />;
    });
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
});

const mapDispatchToProps = {
  onGetChapters: courseChapterActions.getChapters,
};

export default connect(mapStateToProps, mapDispatchToProps)(CourseChaptersPage);
