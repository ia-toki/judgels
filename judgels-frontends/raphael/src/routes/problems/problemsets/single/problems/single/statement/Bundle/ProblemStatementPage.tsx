import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblemWorksheet } from '../../../../../../../../modules/api/jerahmeel/problemSetProblemBundle';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { ItemSubmission } from '../../../../../../../../modules/api/sandalphon/submissionBundle';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { problemSetSubmissionActions as injectedProblemSetSubmissionActions } from '../../results/modules/problemSetSubmissionActions';

export interface ProblemStatementPageProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet: ProblemSet;
  worksheet: ProblemSetProblemWorksheet;
  onCreateSubmission: (problemSetJid: string, problemJid: string, itemJid: string, answer: string) => Promise<void>;
  onGetLatestSubmissions: (problemSetJid: string, problemAlias: string) => Promise<{ [id: string]: ItemSubmission }>;
}

interface ProblemStatementPageState {
  latestSubmissions?: { [id: string]: ItemSubmission };
}

export class ProblemStatementPage extends React.Component<ProblemStatementPageProps, ProblemStatementPageState> {
  state: ProblemStatementPageState = {};

  async componentDidMount() {
    const latestSubmissions = await this.props.onGetLatestSubmissions(
      this.props.problemSet.jid,
      this.props.worksheet.problem.alias
    );
    this.setState({
      latestSubmissions,
    });
  }

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
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    const { latestSubmissions } = this.state;
    if (!latestSubmissions) {
      return <LoadingState />;
    }

    return (
      <ProblemWorksheetCard
        latestSubmissions={latestSubmissions}
        onAnswerItem={this.createSubmission}
        worksheet={worksheet}
      />
    );
  };

  private createSubmission = async (itemJid: string, answer: string) => {
    const { problem } = this.props.worksheet;
    return await this.props.onCreateSubmission(this.props.problemSet.jid, problem.problemJid, itemJid, answer);
  };
}

export function createProblemStatementPage(problemSetSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
  });
  const mapDispatchToProps = {
    onCreateSubmission: problemSetSubmissionActions.createItemSubmission,
    onGetLatestSubmissions: problemSetSubmissionActions.getLatestSubmissions,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage));
}

export default createProblemStatementPage(injectedProblemSetSubmissionActions);
