import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { SubmissionsResponse } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { problemSetSubmissionActions as injectedProblemSubmissionActions } from '../modules/problemSetSubmissionActions';

export interface ProblemSubmissionsPageProps extends RouteComponentProps<{}> {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  onGetProgrammingSubmissions: (
    problemSetJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<SubmissionsResponse>;
  onAppendRoute: (queries) => any;
}

interface ProblemSubmissionsPageState {
  response?: SubmissionsResponse;
  isFilterLoading?: boolean;
}

export class ProblemSubmissionsPage extends React.PureComponent<
  ProblemSubmissionsPageProps,
  ProblemSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ProblemSubmissionsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ProblemSubmissionsTable
        problemSet={this.props.problemSet}
        problem={this.props.problem}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
      />
    );
  };

  private renderPagination = () => {
    return (
      <Pagination
        key={1}
        currentPage={1}
        pageSize={ProblemSubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (page?: number) => {
    const response = await this.props.onGetProgrammingSubmissions(
      this.props.problemSet.jid,
      undefined,
      this.props.problem.problemJid,
      page
    );
    this.setState({ response });
    return response.data;
  };
}

export function createProblemSubmissionsPage(problemSetSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    problem: selectProblemSetProblem(state),
  });

  const mapDispatchToProps = {
    onGetProgrammingSubmissions: problemSetSubmissionActions.getSubmissions,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionsPage));
}

export default createProblemSubmissionsPage(injectedProblemSubmissionActions);
