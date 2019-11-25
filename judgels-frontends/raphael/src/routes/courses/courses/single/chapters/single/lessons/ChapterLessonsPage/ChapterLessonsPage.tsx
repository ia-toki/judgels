import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { ChapterLessonCard, ChapterLessonCardProps } from '../ChapterLessonCard/ChapterLessonCard';
import { consolidateLanguages } from '../../../../../../../../modules/api/sandalphon/language';
import { getLessonName } from '../../../../../../../../modules/api/sandalphon/lesson';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterLessonsResponse } from '../../../../../../../../modules/api/jerahmeel/chapterLesson';
import { AppState } from '../../../../../../../../modules/store';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import { chapterLessonActions as injectedChapterLessonActions } from '../modules/chapterLessonActions';

export interface ChapterLessonsPageProps {
  course: Course;
  chapter: CourseChapter;
  statementLanguage: string;
  onGetLessons: (chapterJid: string) => Promise<ChapterLessonsResponse>;
}

interface ChapterLessonsPageState {
  response?: ChapterLessonsResponse;
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ChapterLessonsPage extends React.PureComponent<ChapterLessonsPageProps, ChapterLessonsPageState> {
  state: ChapterLessonsPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetLessons(this.props.chapter.chapterJid);
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

  async componentDidUpdate(prevProps: ChapterLessonsPageProps) {
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

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props: StatementLanguageWidgetProps = {
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

  private renderLessons = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
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
      const props: ChapterLessonCardProps = {
        course: this.props.course,
        chapter: this.props.chapter,
        lesson,
        lessonName: getLessonName(this.state.response!.lessonsMap[lesson.lessonJid], this.state.defaultLanguage),
      };
      return <ChapterLessonCard key={lesson.lessonJid} {...props} />;
    });
  };
}

export function createChapterLessonsPage(chapterLessonActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
    chapter: selectCourseChapter(state),
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetLessons: chapterLessonActions.getLessons,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterLessonsPage));
}

export default createChapterLessonsPage(injectedChapterLessonActions);
