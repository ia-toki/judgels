import * as React from 'react';
import { Card, HTMLTable, H3, Icon } from '@blueprintjs/core';
import { withRouter, Link } from 'react-router-dom';
import Pagination from 'components/Pagination/Pagination';
import { ContestItemSubmissionsResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { connect } from 'react-redux';
import { Contest } from 'modules/api/uriel/contest';
import { VerdictTag } from '../VerdictTag/VerdictTag';
import { FormattedRelative } from 'react-intl';

import './ContestSubmissionsPage.css';

export interface ContestSubmissionsPageProps {
  onGetSubmissions: (
    contestJid: string,
    userJid?: string,
    problemJid?: string,
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

    const { data, profilesMap, problemAliasesMap } = response;
    const { contest } = this.props;
    const canManage = response.config.canManage;

    return (
      <Card className="contest-bundle-submissions-page">
        <H3>Submissions</H3>
        <HTMLTable striped className="table-list-condensed submissions-table">
          <thead>
            <tr>
              <th>User</th>
              <th className="col-prob">Problem</th>
              <th className="col-item-num">Item Number</th>
              <th>Answer</th>
              {canManage && <th className="col-verdict">Verdict</th>}
              <th>Time</th>
              <th className="col-action" />
            </tr>
          </thead>
          <tbody>
            {data.page.map(item => (
              <tr key={item.jid}>
                <td>{profilesMap[item.userJid] ? profilesMap[item.userJid].username : '-'}</td>
                <td className="col-prob">{problemAliasesMap[item.problemJid] || '-'}</td>
                {/* TODO: Add item number, dont know how to do this yet. */}
                <td className="col-item-num">{Math.round(Math.random() * 50 + 1)}</td>
                <td>{item.answer || '-'}</td>
                {canManage && (
                  <td className="col-verdict">{item.grading ? <VerdictTag verdict={item.grading.verdict} /> : '-'}</td>
                )}
                <td>
                  <FormattedRelative value={item.time} />
                </td>
                <td className="col-action">
                  <Link to={`/contests/${contest.slug}/submissions/users/${item.userJid}`}>
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
      </Card>
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
