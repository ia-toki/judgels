import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { ProblemSetProblemWorksheet } from '../../../../../../../../modules/api/jerahmeel/problemSetProblemBundle';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { ItemSubmission } from '../../../../../../../../modules/api/sandalphon/submissionBundle';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';

export interface ProblemStatementPageProps extends RouteComponentProps<{ problemAlias: string }> {
  problem: ProblemSetProblem;
  worksheet: ProblemSetProblemWorksheet;
  onCreateSubmission: (chapterJid: string, problemJid: string, itemJid: string, answer: string) => Promise<void>;
  onGetLatestSubmissions: (chapterJid: string, problemAlias: string) => Promise<{ [id: string]: ItemSubmission }>;
}

interface ProblemStatementPageState {
  latestSubmissions?: { [id: string]: ItemSubmission };
}

export class ProblemStatementPage extends React.Component<ProblemStatementPageProps, ProblemStatementPageState> {
  state: ProblemStatementPageState = {};

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

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={{}}
        onAnswerItem={this.createSubmission}
        worksheet={worksheet}
      />
    );
  };

  private createSubmission = async (itemJid: string, answer: string) => {
    return await Promise.resolve();
  };
}

export function createProblemStatementPage() {
  const mapStateToProps = (state: AppState) => ({
    problem: selectProblemSetProblem(state),
  });
  return withRouter<any, any>(connect(mapStateToProps)(ProblemStatementPage));
}

export default createProblemStatementPage();
