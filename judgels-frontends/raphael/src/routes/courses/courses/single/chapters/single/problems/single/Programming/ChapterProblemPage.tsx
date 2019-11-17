import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../../modules/store';
import { Course } from '../../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblemProgramming';
import { ProblemSubmissionFormData } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet } from '../../../../../../../../../modules/api/sandalphon/problemProgramming';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectGradingLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { chapterSubmissionActions as injectedChapterSubmissionActions } from '../../../submissions/modules/chapterSubmissionActions';

import './ChapterProblemPage.css';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  course: Course;
  chapter: CourseChapter;
  worksheet: ChapterProblemWorksheet;
  gradingLanguage: string;
  onCreateSubmission: (
    courseSlug: string,
    chapterJid: string,
    chapterAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => Promise<void>;
}

export class ChapterProblemPage extends React.Component<ChapterProblemPageProps> {
  render() {
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props: StatementLanguageWidgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="chapter-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
    const { problem, worksheet } = this.props.worksheet;

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet as ProblemWorksheet}
        onSubmit={this.createSubmission}
        gradingLanguage={this.props.gradingLanguage}
      />
    );
  };

  private createSubmission = async (data: ProblemSubmissionFormData) => {
    const { problem } = this.props.worksheet;

    // this.props.onUpdateGradingLanguage(data.gradingLanguage);
    return await this.props.onCreateSubmission(
      this.props.course.slug,
      this.props.chapter.chapterJid,
      this.props.chapter.alias,
      problem.problemJid,
      data
    );
  };
}

export function createChapterProblemPage(chapterSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
    chapter: selectCourseChapter(state).courseChapter,
    gradingLanguage: selectGradingLanguage(state),
  });
  const mapDispatchToProps = {
    onCreateSubmission: chapterSubmissionActions.createSubmission,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
}

export default createChapterProblemPage(injectedChapterSubmissionActions);
