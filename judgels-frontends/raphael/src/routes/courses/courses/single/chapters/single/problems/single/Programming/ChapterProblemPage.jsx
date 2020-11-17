import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { sendGAEvent } from '../../../../../../../../../ga';
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
import { getGradingLanguageFamily } from '../../../../../../../../../modules/api/gabriel/language';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter, selectCourseChapterName } from '../../../../modules/courseChapterSelectors';
import { selectGradingLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import * as chapterSubmissionActions from '../../../submissions/modules/chapterSubmissionActions';
import * as webPrefsActions from '../../../../../../../../../modules/webPrefs/webPrefsActions';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  course: Course;
  chapter: CourseChapter;
  chapterName: string;
  worksheet: ChapterProblemWorksheet;
  gradingLanguage: string;
  onCreateSubmission: (
    courseSlug: string,
    chapterJid: string,
    chapterAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => Promise<void>;
  onUpdateGradingLanguage: (language: string) => void;
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
      <div className="statement-language-widget-wrapper">
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

    this.props.onUpdateGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Courses', action: 'Submit course problem', label: this.props.course.name });
    sendGAEvent({ category: 'Courses', action: 'Submit chapter problem', label: this.props.chapterName });
    sendGAEvent({
      category: 'Courses',
      action: 'Submit problem',
      label: this.props.chapterName + ': ' + this.props.match.params.problemAlias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Courses',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await this.props.onCreateSubmission(
      this.props.course.slug,
      this.props.chapter.chapterJid,
      this.props.chapter.alias,
      problem.problemJid,
      data
    );
  };
}

const mapStateToProps = (state: AppState) => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  chapterName: selectCourseChapterName(state),
  gradingLanguage: selectGradingLanguage(state),
});
const mapDispatchToProps = {
  onCreateSubmission: chapterSubmissionActions.createSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
