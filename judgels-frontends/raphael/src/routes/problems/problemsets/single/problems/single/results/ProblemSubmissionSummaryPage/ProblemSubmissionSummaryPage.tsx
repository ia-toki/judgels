import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../modules/store';
import { Profile } from '../../../../../../../../modules/api/jophiel/profile';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { SubmissionSummaryResponse } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { SubmissionConfig } from '../../../../../../../../modules/api/jerahmeel/submission';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import {
  ProblemSubmissionCard,
  ProblemSubmissionCardProps,
} from '../../../../../../../../components/SubmissionDetails/Bundle/ProblemSubmissionsCard/ProblemSubmissionCard';
import { problemSetSubmissionActions as injectedProblemSetSubmissionActions } from '../modules/problemSetSubmissionActions';

interface ProblemSubmissionSummaryPageRoute {
  username?: string;
}

export interface ProblemSubmissionSummaryPageProps extends RouteComponentProps<ProblemSubmissionSummaryPageRoute> {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  language?: string;
  onGetSubmissionSummary: (
    problemSetJid: string,
    problemJid: string,
    username?: string,
    language?: string
  ) => Promise<SubmissionSummaryResponse>;
}

export interface ProblemSubmissionSummaryPageState {
  config?: SubmissionConfig;
  profile?: Profile;
  problemSummaries: ProblemSubmissionCardProps[];
}

class ProblemSubmissionSummaryPage extends React.Component<
  ProblemSubmissionSummaryPageProps,
  ProblemSubmissionSummaryPageState
> {
  state: ProblemSubmissionSummaryPageState = {
    config: undefined,
    problemSummaries: undefined,
  };

  async refreshSubmissions() {
    const { problemSet, problem, onGetSubmissionSummary } = this.props;
    const response = await onGetSubmissionSummary(
      problemSet.jid,
      problem.problemJid,
      this.props.match.params.username,
      this.props.language
    );

    const problemSummaries: ProblemSubmissionCardProps[] = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
      canManage: response.config.canManage,
      itemTypesMap: response.itemTypesMap,
    }));

    this.setState({ config: response.config, problemSummaries });
  }

  async componentDidMount() {
    await this.refreshSubmissions();
  }

  render() {
    return (
      <ContentCard>
        <h3>Results</h3>
        <hr />
        {this.renderResults()}
      </ContentCard>
    );
  }

  private renderResults = () => {
    const { problemSummaries } = this.state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No problems.</small>;
    }
    return this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />);
  };
}

export function createProblemSubmissionSummaryPage(problemSetSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    problem: selectProblemSetProblem(state),
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionSummary: problemSetSubmissionActions.getSubmissionSummary,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionSummaryPage));
}

export default createProblemSubmissionSummaryPage(injectedProblemSetSubmissionActions);
