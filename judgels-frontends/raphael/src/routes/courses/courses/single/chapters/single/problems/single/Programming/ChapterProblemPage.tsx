import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../../modules/store';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblemProgramming';
import { ProblemSubmissionFormData } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet } from '../../../../../../../../../modules/api/sandalphon/problemProgramming';
import { selectGradingLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';

import './ChapterProblemPage.css';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  worksheet: ChapterProblemWorksheet;
  gradingLanguage: string;
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
      <div className="chapter-lesson-page__widget">
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
    return await null;
  };
}

export function createChapterProblemPage() {
  const mapStateToProps = (state: AppState) => ({
    statementLanguage: selectStatementLanguage(state),
    gradingLanguage: selectGradingLanguage(state),
  });

  return withRouter<any, any>(connect(mapStateToProps)(ChapterProblemPage));
}

export default createChapterProblemPage();
