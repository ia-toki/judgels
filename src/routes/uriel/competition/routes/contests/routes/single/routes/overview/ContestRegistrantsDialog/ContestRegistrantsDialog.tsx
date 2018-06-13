import { Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { UserRef } from '../../../../../../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { ContestContestantsResponse } from '../../../../../../../../../../modules/api/uriel/contestContestant';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { contestContestantActions as injectedContestContestantActions } from '../../../modules/contestContestantActions';

import './ContestRegistrantsDialog.css';

export interface ContestRegistrantsDialogProps {
  onClose: () => void;
}

export interface ContestRegistrantsDialogConnectedProps {
  contest: Contest;
  onFetchContestants: (contestJid: string) => Promise<ContestContestantsResponse>;
}

interface ContestRegistrantsDialogState {
  contestants?: string[];
  usersMap?: UsersMap;
  userCountriesMap?: { [userJid: string]: string };
}

class ContestRegistrantsDialog extends React.PureComponent<
  ContestRegistrantsDialogProps & ContestRegistrantsDialogConnectedProps,
  ContestRegistrantsDialogState
> {
  state: ContestRegistrantsDialogState = {};

  async componentDidMount() {
    const { data, usersMap, userCountriesMap } = await this.props.onFetchContestants(this.props.contest.jid);
    this.setState({ contestants: data, usersMap, userCountriesMap });
  }

  render() {
    const contestantsCount = this.state.contestants !== undefined ? ` (${this.state.contestants.length})` : '';

    return (
      <Dialog isOpen onClose={this.props.onClose} title={`Registrants${contestantsCount}`} canOutsideClickClose={false}>
        <div className="pt-dialog-body contest-registrants-dialog__body">{this.renderRegistrants()}</div>
        <div className="pt-dialog-footer">
          <div className="pt-dialog-footer-actions">
            <Button text="Close" onClick={this.props.onClose} />
          </div>
        </div>
      </Dialog>
    );
  }

  private renderRegistrants = () => {
    const { contestants, usersMap, userCountriesMap } = this.state;
    if (!contestants || !usersMap || !userCountriesMap) {
      return <LoadingState />;
    }

    const sortedContestants = contestants.slice().sort((jid1, jid2) => {
      const country1 = userCountriesMap[jid1] || '~';
      const country2 = userCountriesMap[jid2] || '~';
      if (country1 !== country2) {
        return country1.localeCompare(country2);
      }

      const username1 = (usersMap[jid1] && usersMap[jid1].username) || '~';
      const username2 = (usersMap[jid2] && usersMap[jid2].username) || '~';
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map(jid => (
      <tr key={jid}>
        <td>{userCountriesMap[jid]}</td>
        <td>
          <UserRef user={usersMap[jid]} />
        </td>
      </tr>
    ));

    return (
      <table className="pt-html-table pt-html-table-striped table-list">
        <thead>
          <tr>
            <th>Country</th>
            <th>User</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  };
}

function createContestRegistrantsDialog(contestContestantActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });
  const mapDispatchToProps = {
    onFetchContestants: contestContestantActions.fetchContestants,
  };
  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestRegistrantsDialog));
}

export default createContestRegistrantsDialog(injectedContestContestantActions);
