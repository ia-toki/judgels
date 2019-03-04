import React, { Component } from 'react';
import { Card, HTMLTable, H3 } from '@blueprintjs/core';
import { withRouter } from 'react-router-dom';
import { ContestItemSubmissionsResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { connect } from 'react-redux';
import { Contest } from 'modules/api/uriel/contest';
import { FormattedDate } from 'components/FormattedDate/FormattedDate';

import './ContestSubmissionsPage.css';

export interface ContestSubmissionsPageProps {
  getSubmissions: (
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

export class ContestSubmissionsPage extends Component<ContestSubmissionsPageProps, ContestSubmissionsPageState> {
  constructor(props: ContestSubmissionsPageProps) {
    super(props);
    this.state = { response: undefined };
  }

  async componentDidMount() {
    const { contest, getSubmissions } = this.props;
    const response = await getSubmissions(contest.jid);
    this.setState({ response });
  }

  render() {
    const response = this.state.response;
    if (!response) {
      return <Card className="bp3-skeleton">{'fake'.repeat(100)}</Card>;
    }

    const { data, profilesMap, problemAliasesMap } = response;

    return (
      <Card className="contest-bundle-submissions-page">
        <H3>Submissions</H3>
        <HTMLTable className="submissions-table" bordered striped>
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
              <tr>
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
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
