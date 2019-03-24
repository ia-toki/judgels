import { Card, HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';
import { connect } from 'react-redux';
import { withRouter, Link } from 'react-router-dom';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { UserRef } from 'components/UserRef/UserRef';
import Pagination from 'components/Pagination/Pagination';
import { ContestItemSubmissionsResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';

import { VerdictTag } from '../VerdictTag/VerdictTag';
import { FormattedAnswer } from '../FormattedAnswer/FormattedAnswer';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

import './ContestSubmissionsPage.css';

export interface ContestSubmissionsPageProps {
  onGetSubmissions: (
    contestJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ) => Promise<ContestItemSubmissionsResponse>;
  contest: Contest;
}

interface ContestSubmissionsPageState {
  response?: ContestItemSubmissionsResponse;
}

export class ContestSubmissionsPage extends React.Component<ContestSubmissionsPageProps, ContestSubmissionsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestSubmissionsPageState = {};

  componentDidMount() {
    this.refreshSubmissions();
  }

  refreshSubmissions = async (page?: number) => {
    const { contest, onGetSubmissions } = this.props;
    const response = await onGetSubmissions(contest.jid, undefined, undefined, page);
    this.setState({ response });
    return response;
  };

  render() {
    const response = this.state.response;
    if (!response) {
      return <Card className="bp3-skeleton">{'fake'.repeat(100)}</Card>;
    }

    const { data, profilesMap, problemAliasesMap, itemNumbersMap, itemTypesMap } = response;
    const { contest } = this.props;
    const canManage = response.config.canManage;

    return (
      <ContentCard className="contest-bundle-submissions-page">
        <h3>Submissions</h3>
        <hr />
        <HTMLTable striped className="table-list-condensed submissions-table">
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
                  <Link to={`/contests/${contest.slug}/submissions/users/${profilesMap[item.userJid].username}`}>
                    <Icon icon="search" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </HTMLTable>
        <div className="submission-pagination">
          <Pagination currentPage={1} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
        </div>
      </ContentCard>
    );
  }

  private onChangePage = async (nextPage: number) => {
    const response = await this.refreshSubmissions(nextPage);
    return response.data.totalCount;
  };
}

export function createContestSubmissionsPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSubmissions: contestSubmissionActions.getSubmissions,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
