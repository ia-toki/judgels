import { Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { UserRef } from 'components/UserRef/UserRef';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ContestContestantsResponse } from 'modules/api/uriel/contestContestant';
import { Contest } from 'modules/api/uriel/contest';
import { AppState } from 'modules/store';

import { selectContest } from '../../../modules/contestSelectors';
import { contestContestantActions as injectedContestContestantActions } from '../../modules/contestContestantActions';

import './ContestRegistrantsDialog.css';

export interface ContestRegistrantsDialogProps {
  onClose: () => void;
}

export interface ContestRegistrantsDialogConnectedProps {
  contest: Contest;
  onGetContestants: (contestJid: string) => Promise<ContestContestantsResponse>;
}

interface ContestRegistrantsDialogState {
  contestants?: string[];
  profilesMap?: ProfilesMap;
}

class ContestRegistrantsDialog extends React.PureComponent<
  ContestRegistrantsDialogProps & ContestRegistrantsDialogConnectedProps,
  ContestRegistrantsDialogState
> {
  state: ContestRegistrantsDialogState = {};

  async componentDidMount() {
    const { data, profilesMap } = await this.props.onGetContestants(this.props.contest.jid);
    this.setState({ contestants: data, profilesMap });
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
    const { contestants, profilesMap } = this.state;
    if (!contestants || !profilesMap) {
      return <LoadingState />;
    }

    const sortedContestants = contestants.slice().sort((jid1, jid2) => {
      const country1 = (profilesMap[jid1] && profilesMap[jid1].nationality) || 'ZZ';
      const country2 = (profilesMap[jid2] && profilesMap[jid2].nationality) || 'ZZ';
      if (country1 !== country2) {
        return country1.localeCompare(country2);
      }

      const username1 = (profilesMap[jid1] && profilesMap[jid1].username) || 'ZZ';
      const username2 = (profilesMap[jid2] && profilesMap[jid2].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map(jid => (
      <tr key={jid}>
        <td>{profilesMap[jid].nationality}</td>
        <td>
          <UserRef profile={profilesMap[jid]} />
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
    onGetContestants: contestContestantActions.getContestants,
  };
  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestRegistrantsDialog));
}

export default createContestRegistrantsDialog(injectedContestContestantActions);
