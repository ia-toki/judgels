import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { ProblemSetProblemCard, ProblemSetProblemCardProps } from '../ProblemSetProblemCard/ProblemSetProblemCard';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblemsResponse } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { AppState } from '../../../../../../modules/store';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { problemSetProblemActions as injectedProblemSetProblemActions } from '../modules/problemSetProblemActions';

export interface ProblemSetProblemsPageProps {
  problemSet: ProblemSet;
  statementLanguage: string;
  onGetProblems: (problemSetJid: string) => Promise<ProblemSetProblemsResponse>;
}

interface ProblemSetProblemsPageState {
  response?: ProblemSetProblemsResponse;
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ProblemSetProblemsPage extends React.PureComponent<
  ProblemSetProblemsPageProps,
  ProblemSetProblemsPageState
> {
  state: ProblemSetProblemsPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.problemSet.jid);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.problemsMap,
      this.props.statementLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps: ProblemSetProblemsPageProps) {
    const { response } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && response) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
        response.problemsMap,
        this.props.statementLanguage
      );

      this.setState({
        defaultLanguage,
        uniqueLanguages,
      });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {this.renderStatementLanguageWidget()}
        {this.renderProblems()}
      </ContentCard>
    );
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

  private renderProblems = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemProgressesMap, problemStatsMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const props: ProblemSetProblemCardProps = {
        problemSet: this.props.problemSet,
        problem,
        problemName: getProblemName(this.state.response!.problemsMap[problem.problemJid], this.state.defaultLanguage),
        progress: problemProgressesMap[problem.problemJid],
        stats: problemStatsMap[problem.problemJid],
      };
      return <ProblemSetProblemCard key={problem.problemJid} {...props} />;
    });
  };
}

export function createProblemSetProblemsPage(problemSetProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetProblems: problemSetProblemActions.getProblems,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemSetProblemsPage));
}

export default createProblemSetProblemsPage(injectedProblemSetProblemActions);
