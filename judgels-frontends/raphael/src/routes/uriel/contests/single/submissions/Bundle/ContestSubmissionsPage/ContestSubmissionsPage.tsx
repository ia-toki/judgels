import React, { Component } from 'react';
import { Card, HTMLTable, H3 } from '@blueprintjs/core';
import { withRouter } from 'react-router-dom';
import Pagination from 'components/Pagination/Pagination';
import { ContestItemSubmissionsResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { connect } from 'react-redux';
import { Contest } from 'modules/api/uriel/contest';
import { FormattedDate } from 'components/FormattedDate/FormattedDate';

import './ContestSubmissionsPage.css';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { push } from 'react-router-redux';

export interface ContestSubmissionsPageProps {
  getSubmissions: (
    contestJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<ContestItemSubmissionsResponse>;
  gotoSummary: (contest: Contest, item: ItemSubmission) => any;
  contest: Contest;
}

interface ContestSubmissionsPageState {
  response?: ContestItemSubmissionsResponse;
}

export class ContestSubmissionsPage extends Component<ContestSubmissionsPageProps, ContestSubmissionsPageState> {
  private static PAGE_SIZE = 20;

  constructor(props: ContestSubmissionsPageProps) {
    super(props);
    this.state = { response: undefined };
  }

  componentDidMount() {
    this.loadSubmissions();
  }

  loadSubmissions = async (page?: number) => {
    const { contest, getSubmissions } = this.props;
    const response = await getSubmissions(contest.jid, undefined, undefined, page);
    this.setState({ response });
    return response;
  };

  onChangePage = async (nextPage: number) => {
    const response = await this.loadSubmissions(nextPage);
    return response.data.totalCount;
  };

  render() {
    const response = this.state.response;
    if (!response) {
      return <Card className="bp3-skeleton">{'fake'.repeat(100)}</Card>;
    }

    const { data, profilesMap, problemAliasesMap } = response;
    const { contest, gotoSummary } = this.props;

    return (
      <Card className="contest-bundle-submissions-page">
        <H3>Submissions</H3>
        <HTMLTable className="submissions-table" bordered striped interactive>
          <thead>
            <tr>
              <th>Soal</th>
              <th>Nomor Soal</th>
              <th>Jawaban</th>
              <th>Issuer</th>
              <th>Time</th>
            </tr>
          </thead>
          <tbody>
            {data.page.map(item => (
              <tr key={item.jid} onClick={gotoSummary.bind(this, contest, item)}>
                <td>{problemAliasesMap[item.problemJid] || '-'}</td>
                {/* TODO: Add item number, dont know how to do this yet. */}
                <td>{Math.round(Math.random() * 50 + 1)}</td>
                <td>{item.answer}</td>
                <td>{profilesMap[item.userJid] ? profilesMap[item.userJid].username : '-'}</td>
                <td>
                  <FormattedDate value={item.time} showSeconds />
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
}

export function createContestSubmissionsPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    getSubmissions: contestSubmissionActions.getSubmissions,
    gotoSummary: (contest: Contest, item: ItemSubmission) =>
      push(`/contests/${contest.slug}/submissions/users/${item.userJid}`),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
