import { ChevronRight } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import { ChapterLessonCard } from '../ChapterLessonCard/ChapterLessonCard';
import { ChapterProblemCard } from '../ChapterProblemCard/ChapterProblemCard';
import { getLessonName } from '../../../../../../../../modules/api/sandalphon/lesson';
import { getProblemName } from '../../../../../../../../modules/api/sandalphon/problem';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import * as chapterResourcesActions from '../modules/chapterResourceActions';

import './ChapterResourcesPage.scss';

export class ChapterResourcesPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetResources(this.props.chapter.jid);
    this.setState({
      response,
    });
  }

  render() {
    return (
      <div className="chapter-resources-page">
        {this.renderHeader()}
        <hr />
        {this.renderResources()}
      </div>
    );
  }

  renderHeader = () => {
    const { course, chapter } = this.props;

    return (
      <h3 className="chapter-resources-page__title">
        <Link className="chapter-resources-page__title--link" to={`/courses/${course.slug}`}>
          {course.name}
        </Link>
        &nbsp;
        <ChevronRight className="chapter-resources-page__title--chevron" size={20} />
        &nbsp;
        {chapter.alias}. {chapter.name}
      </h3>
    );
  };

  renderResources = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const [lessonsResponse, problemsResponse] = response;
    const { data: lessons, lessonsMap } = lessonsResponse;
    const { data: problems, problemsMap, problemProgressesMap } = problemsResponse;

    if (lessons.length === 0 && problems.length === 0) {
      return (
        <p>
          <small>No resources.</small>
        </p>
      );
    }

    return (
      <>
        {lessons.map(lesson => {
          const props = {
            course: this.props.course,
            chapter: this.props.chapter,
            lesson,
            lessonName: getLessonName(lessonsMap[lesson.lessonJid], undefined),
          };
          return <ChapterLessonCard key={lesson.lessonJid} {...props} />;
        })}
        {problems.map(problem => {
          const props = {
            course: this.props.course,
            chapter: this.props.chapter,
            problem,
            problemName: getProblemName(problemsMap[problem.problemJid], undefined),
            progress: problemProgressesMap[problem.problemJid],
          };
          return <ChapterProblemCard key={problem.problemJid} {...props} />;
        })}
      </>
    );
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

const mapDispatchToProps = {
  onGetResources: chapterResourcesActions.getResources,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterResourcesPage));
