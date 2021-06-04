import { Button, HTMLTable, Intent, ButtonGroup } from '@blueprintjs/core';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { push } from 'connected-react-router';

import { reallyConfirm } from '../../../../../../../utils/confirmation';
import { FormattedRelative } from '../../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { selectContest } from '../../../../modules/contestSelectors';
import { VerdictTag } from '../../../../../../../components/SubmissionDetails/Bundle/VerdictTag/VerdictTag';
import { FormattedAnswer } from '../../../../../../../components/SubmissionDetails/Bundle/FormattedAnswer/FormattedAnswer';
import * as contestSubmissionActions from '../modules/contestSubmissionActions';

import '../../../../../../../components/SubmissionsTable/Bundle/ItemSubmissionsTable.scss';

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
        {this.renderRegradeAllButton()}
        {this.renderFilterWidget()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderSubmissions = () => {
    const response = this.state.response;
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap, problemAliasesMap, itemNumbersMap, itemTypesMap } = response;
    const { contest } = this.props;
    const canManage = response.config.canManage;

    return (
      <HTMLTable striped className="table-list-condensed item-submissions-table">
        <thead>
          <tr>
            <th>User</th>
            <th className="col-prob">Prob</th>
            <th className="col-item-num">No</th>
            <th>Answer</th>
            {canManage && <th className="col-verdict">Verdict</th>}
            <th>Time</th>
            <th className="col-action" />
          </tr>
        </thead>
        <tbody>
          {data.page.map(item => (
            <tr key={item.jid}>
              <td>
                <UserRef profile={profilesMap[item.userJid]} />
              </td>
              <td className="col-prob">{problemAliasesMap[item.problemJid] || '-'}</td>
              <td className="col-item-num">{itemNumbersMap[item.itemJid] || '-'}</td>
              <td>
                <FormattedAnswer answer={item.answer} type={itemTypesMap[item.itemJid]} />
              </td>
              {canManage && (
                <td className="col-verdict">{item.grading ? <VerdictTag verdict={item.grading.verdict} /> : '-'}</td>
              )}
              <td>
                <FormattedRelative value={item.time} />
              </td>
              <td className="col-action">
                <ButtonGroup minimal className="action-button-group">
                  <Link to={`/contests/${contest.slug}/submissions/users/${profilesMap[item.userJid].username}`}>
                    <Button icon="search" intent={Intent.NONE} small />
                  </Link>
                  {canManage && (
                    <Button icon="refresh" intent={Intent.NONE} small onClick={this.onClickRegrade(item.jid)} />
                  )}
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  renderPagination = () => {
    const { filter } = this.state;

    const key = '' + filter.username + filter.problemAlias;
    return <Pagination key={key} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  refreshSubmissions = async (username, problemAlias, page) => {
    const { contest, onGetSubmissions } = this.props;
    const response = await onGetSubmissions(contest.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response;
  };

  onChangePage = async nextPage => {
    const { username, problemAlias } = this.state.filter;
    const response = await this.refreshSubmissions(username, problemAlias, nextPage);
    return response.data.totalCount;
  };

  onClickRegrade = submissionJid => {
    return () => this.onRegrade(submissionJid);
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
      await this.props.onRegradeAll(this.props.contest.jid, username, problemAlias);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(username, problemAlias, queries.page);
    }
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }

    return (
      <Button
        className="item-submissions-table__regrade-button"
        intent="primary"
        icon="refresh"
        onClick={this.onRegradeAll}
      >
        Regrade all pages
      </Button>
    );
  };

  renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response || !filter) {
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

  onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetSubmissions: contestSubmissionActions.getSubmissions,
  onRegrade: contestSubmissionActions.regradeSubmission,
  onRegradeAll: contestSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Submissions')(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
