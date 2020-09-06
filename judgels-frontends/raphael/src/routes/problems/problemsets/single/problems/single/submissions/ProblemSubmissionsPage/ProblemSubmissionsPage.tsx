import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { SubmissionsResponse } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

export interface ProblemSubmissionsPageProps extends RouteComponentProps<{}> {
  userJid?: string;
  username?: string;
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  onGetProgrammingSubmissions: (
    problemSetJid: string,
    username?: string,
    problemJid?: string,
    page?: number
  ) => Promise<SubmissionsResponse>;
  onRegrade: (submissionJid: string) => Promise<void>;
  onRegradeAll: (problemSetJid: string, username?: string, problemJid?: string) => Promise<void>;
  onAppendRoute: (queries) => any;
}

interface ProblemSubmissionsPageState {
  response?: SubmissionsResponse;
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
        {this.renderUserFilter()}
        {this.renderRegradeAllButton()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  private isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  private renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} />;
  };

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
        userJid={this.props.userJid}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  private renderPagination = () => {
    return (
      <Pagination
        key={'' + this.isUserFilterMine()}
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
    const username = this.isUserFilterMine() ? this.props.username : undefined;
    const response = await this.props.onGetProgrammingSubmissions(
      undefined,
      username,
      this.props.problem.problemJid,
      page
    );
    this.setState({ response });
    return response.data;
  };

  private onRegrade = async (submissionJid: string) => {
    await this.props.onRegrade(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
  };

  private onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await this.props.onRegradeAll(this.props.problemSet.jid, undefined, this.props.problem.problemJid);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(queries.page);
    }
  };
}

const mapStateToProps = (state: AppState) => ({
  userJid: selectMaybeUserJid(state),
  username: selectMaybeUsername(state),
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: problemSetSubmissionActions.getSubmissions,
  onRegrade: problemSetSubmissionActions.regradeSubmission,
  onRegradeAll: problemSetSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionsPage));
