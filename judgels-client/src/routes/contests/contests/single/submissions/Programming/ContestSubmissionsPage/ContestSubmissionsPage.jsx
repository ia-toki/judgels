import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../components/RegradeAllButton/RegradeAllButton';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { reallyConfirm } from '../../../../../../../utils/confirmation';
import { selectContest } from '../../../../modules/contestSelectors';
import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

export class ContestSubmissionsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const username = queries.username;
    const problemAlias = queries.problemAlias;

    this.state = {
      response: undefined,
      filter: { username, problemAlias },
      isFilterLoading: false,
      isRegradingAll: false,
    };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const username = queries.username;
    const problemAlias = queries.problemAlias;

    if (username !== this.state.filter.username || problemAlias !== this.state.filter.problemAlias) {
      this.setState({ filter: { username, problemAlias }, isFilterLoading: true });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderHeader()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderHeader = () => {
    return (
      <div className="content-card__header">
        <div className="float-left">{this.renderRegradeAllButton()}</div>
        <div className="float-right">{this.renderFilterWidget()}</div>
        <div className="clearfix" />
      </div>
    );
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} isRegradingAll={this.state.isRegradingAll} />;
  };

  renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids, canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    const { username, problemAlias } = filter;
    return (
      <SubmissionFilterWidget
        usernames={userJids.map(jid => profilesMap[jid] && profilesMap[jid].username)}
        problemAliases={problemJids.map(jid => problemAliasesMap[jid])}
        username={username}
        problemAlias={problemAlias}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={this.props.contest}
        submissions={submissions.page}
        canSupervise={config.canSupervise}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  renderPagination = () => {
    const { filter } = this.state;

    const key = '' + filter.username + filter.problemAlias;
    return <Pagination key={key} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  onChangePage = async nextPage => {
    const { username, problemAlias } = this.state.filter;
    const data = await this.refreshSubmissions(username, problemAlias, nextPage);
    return data.totalCount;
  };

  refreshSubmissions = async (username, problemAlias, page) => {
    const response = await this.props.onGetProgrammingSubmissions(this.props.contest.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  onRegrade = async submissionJid => {
    await this.props.onRegrade(submissionJid);
    const { username, problemAlias } = this.state.filter;
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(username, problemAlias, queries.page);
  };

  onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      const { username, problemAlias } = this.state.filter;

      this.setState({ isRegradingAll: true });
      await this.props.onRegradeAll(this.props.contest.jid, username, problemAlias);
      this.setState({ isRegradingAll: false });
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(username, problemAlias, queries.page);
    }
  };

  onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: contestSubmissionActions.getSubmissions,
  onRegrade: contestSubmissionActions.regradeSubmission,
  onRegradeAll: contestSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Submissions')(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
