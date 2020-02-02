import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { sendGAEvent } from '../../../../../../../../ga';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { ProblemSetProblemWorksheet } from '../../../../../../../../modules/api/jerahmeel/problemSetProblemProgramming';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet } from '../../../../../../../../modules/api/sandalphon/problemProgramming';
import { getGradingLanguageFamily } from '../../../../../../../../modules/api/gabriel/language';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { selectGradingLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { problemSetSubmissionActions as injectedProblemSetSubmissionActions } from '../../submissions/modules/problemSetSubmissionActions';
import { webPrefsActions as injectedWebPrefsActions } from '../../../../../../../../modules/webPrefs/webPrefsActions';

export interface ProblemStatementPageProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  worksheet: ProblemSetProblemWorksheet;
  gradingLanguage: string;
  onCreateSubmission: (
    problemSetSlug: string,
    problemSetJid: string,
    problemAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => Promise<void>;
  onUpdateGradingLanguage: (language: string) => void;
}

export class ProblemStatementPage extends React.Component<ProblemStatementPageProps> {
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
    const { worksheet } = this.props.worksheet;

    return (
      <ProblemWorksheetCard
        worksheet={worksheet as ProblemWorksheet}
        onSubmit={this.createSubmission}
        gradingLanguage={this.props.gradingLanguage}
      />
    );
  };

  private createSubmission = async (data: ProblemSubmissionFormData) => {
    const { problem } = this.props.worksheet;

    this.props.onUpdateGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Problems', action: 'Submit problemset problem', label: this.props.problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'Submit problem',
      label: this.props.problemSet.name + ': ' + this.props.problem.alias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Problems',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await this.props.onCreateSubmission(
      this.props.problemSet.slug,
      this.props.problemSet.jid,
      this.props.problem.alias,
      problem.problemJid,
      data
    );
  };
}

export function createProblemStatementPage(problemSetSubmissionActions, webPrefsActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    problem: selectProblemSetProblem(state),
    gradingLanguage: selectGradingLanguage(state),
  });
  const mapDispatchToProps = {
    onCreateSubmission: problemSetSubmissionActions.createSubmission,
    onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage));
}

export default createProblemStatementPage(injectedProblemSetSubmissionActions, injectedWebPrefsActions);
