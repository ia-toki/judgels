import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../components/LoadingState/LoadingState';
import Pagination from '../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { AppState } from '../../../modules/store';
import { SubmissionsResponse } from '../../../modules/api/jerahmeel/submissionProgramming';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';
import { selectMaybeUserJid } from '../../../modules/session/sessionSelectors';
import { submissionActions as injectedSubmissionActions } from '../modules/submissionActions';

export interface SubmissionsPageProps extends RouteComponentProps<{}> {
  userJid?: string;
  onGetProgrammingSubmissions: (
    containerJid?: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<SubmissionsResponse>;
  onRegrade: (submissionJid: string) => Promise<void>;
  onAppendRoute: (queries) => any;
}

interface SubmissionsPageState {
  response?: SubmissionsResponse;
}

export class SubmissionsPage extends React.PureComponent<SubmissionsPageProps, SubmissionsPageState> {
  private static PAGE_SIZE = 20;

  state: SubmissionsPageState = {};

  render() {
    return (
      <>
        {this.renderUserFilter()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </>
    );
  }

  private renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  private isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  private renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      profilesMap,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        userJid={this.props.userJid}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  private renderPagination = () => {
    return (
      <Pagination
        key={'' + this.isUserFilterMine()}
        currentPage={1}
        pageSize={SubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (page?: number) => {
    const userJid = this.isUserFilterMine() ? this.props.userJid : undefined;
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

export function createSubmissionsPage(submissionActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
  });

  const mapDispatchToProps = {
    onGetProgrammingSubmissions: submissionActions.getSubmissions,
    onRegrade: submissionActions.regradeSubmission,
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionsPage));
}

export default createSubmissionsPage(injectedSubmissionActions);
