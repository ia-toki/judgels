import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ChapterLessonCard } from '../ChapterLessonCard/ChapterLessonCard';
import { consolidateLanguages } from '../../../../../../../../modules/api/sandalphon/language';
import { getLessonName } from '../../../../../../../../modules/api/sandalphon/lesson';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import * as chapterLessonActions from '../modules/chapterLessonActions';

export class ChapterLessonsPage extends Component {
  state = {
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  };

  async componentDidMount() {
    const lessonAliases = this.props.chapter.lessonAliases || [];
    if (lessonAliases.length === 1) {
      this.props.onRedirectToLesson(this.props.match.url, lessonAliases[0]);
    }

    const response = await this.props.onGetLessons(this.props.chapter.jid);

    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.lessonsMap,
      this.props.statementLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps) {
    const { response } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && response) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
        response.lessonsMap,
        this.props.statementLanguage
      );

      this.setState({
        defaultLanguage,
        uniqueLanguages,
      });
    }
  }

  renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  render() {
    return (
      <ContentCard>
        <h3>Lessons</h3>
        <hr />
        {this.renderStatementLanguageWidget()}
        {this.renderLessons()}
      </ContentCard>
    );
  }

  renderLessons = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: lessons } = response;

    if (lessons.length === 0) {
      return (
        <p>
          <small>No lessons.</small>
        </p>
      );
    }

    return lessons.map(lesson => {
      const props = {
        course: this.props.course,
        chapter: this.props.chapter,
        lesson,
        lessonName: getLessonName(this.state.response.lessonsMap[lesson.lessonJid], this.state.defaultLanguage),
      };
      return <ChapterLessonCard key={lesson.lessonJid} {...props} />;
    });
  };
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetLessons: chapterLessonActions.getLessons,
  onRedirectToLesson: chapterLessonActions.redirectToLesson,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterLessonsPage));
