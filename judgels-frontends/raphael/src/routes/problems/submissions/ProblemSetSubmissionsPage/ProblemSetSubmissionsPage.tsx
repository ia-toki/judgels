import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { AppState } from '../../../../modules/store';
import { SubmissionsResponse } from '../../../../modules/api/jerahmeel/submissionProgramming';
import { ProblemSetSubmissionsTable } from '../ProblemSetSubmissionsTable/ProblemSetSubmissionsTable';
import { selectUserJid } from '../../../../modules/session/sessionSelectors';
import { problemSetSubmissionActions as injectedProblemSubmissionActions } from '../modules/problemSetSubmissionActions';

export interface ProblemSetSubmissionsPageProps extends RouteComponentProps<{}> {
  userJid: string;
  onGetProgrammingSubmissions: (
    problemSetJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<SubmissionsResponse>;
  onRegrade: (submissionJid: string) => Promise<void>;
  onRegradeAll: (problemSetJid: string, userJid?: string, problemJid?: string) => Promise<void>;
  onAppendRoute: (queries) => any;
}

interface ProblemSetSubmissionsPageState {
  response?: SubmissionsResponse;
}

export class ProblemSetSubmissionsPage extends React.PureComponent<
  ProblemSetSubmissionsPageProps,
  ProblemSetSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ProblemSetSubmissionsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderUserFilter()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderUserFilter = () => {
    return <SubmissionUserFilter />;
  };

  private isUserFilterAll = () => {
    return (this.props.location.pathname + '/').includes('/all/');
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
      <ProblemSetSubmissionsTable
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
        key={'' + this.isUserFilterAll()}
        currentPage={1}
        pageSize={ProblemSetSubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (page?: number) => {
    const userJid = this.isUserFilterAll() ? undefined : this.props.userJid;
    const response = await this.props.onGetProgrammingSubmissions(undefined, userJid, undefined, page);
    this.setState({ response });
    return response.data;
  };

  private onRegrade = async (submissionJid: string) => {
    await this.props.onRegrade(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
  };
}

export function createProblemSetSubmissionsPage(problemSetSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
  });

  const mapDispatchToProps = {
    onGetProgrammingSubmissions: problemSetSubmissionActions.getSubmissions,
    onRegrade: problemSetSubmissionActions.regradeSubmission,
    onRegradeAll: problemSetSubmissionActions.regradeSubmissions,
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemSetSubmissionsPage));
}

export default createProblemSetSubmissionsPage(injectedProblemSubmissionActions);
