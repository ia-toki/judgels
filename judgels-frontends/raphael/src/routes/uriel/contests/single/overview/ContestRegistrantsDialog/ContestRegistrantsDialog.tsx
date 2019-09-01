import { Classes, Button, Dialog, HTMLTable } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ApprovedContestContestantsResponse } from '../../../../../../modules/api/uriel/contestContestant';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../modules/store';
import { getCountryName } from '../../../../../../assets/data/countries';

import { selectContest } from '../../../modules/contestSelectors';
import { contestContestantActions as injectedContestContestantActions } from '../../modules/contestContestantActions';

import './ContestRegistrantsDialog.css';

export interface ContestRegistrantsDialogProps {
  onClose: () => void;
}

export interface ContestRegistrantsDialogConnectedProps {
  contest: Contest;
  onGetApprovedContestants: (contestJid: string) => Promise<ApprovedContestContestantsResponse>;
}

interface ContestRegistrantsDialogState {
  response?: ApprovedContestContestantsResponse;
}

class ContestRegistrantsDialog extends React.PureComponent<
  ContestRegistrantsDialogProps & ContestRegistrantsDialogConnectedProps,
  ContestRegistrantsDialogState
> {
  state: ContestRegistrantsDialogState = {};

  async componentDidMount() {
    const response = await this.props.onGetApprovedContestants(this.props.contest.jid);
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    const contestantsCount = response ? ` (${response.data.length})` : '';

    return (
      <Dialog isOpen onClose={this.props.onClose} title={`Registrants${contestantsCount}`} canOutsideClickClose={false}>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-registrants-dialog__body')}>
          {this.renderRegistrants()}
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Close" onClick={this.props.onClose} />
          </div>
        </div>
      </Dialog>
    );
  }

  private renderRegistrants = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, profilesMap } = response;
    const sortedContestants = contestants.slice().sort((jid1, jid2) => {
      const rating1 = (profilesMap[jid1] && profilesMap[jid1].rating) || 0;
      const rating2 = (profilesMap[jid2] && profilesMap[jid2].rating) || 0;
      if (rating1 !== rating2) {
        return rating2 - rating1;
      }

      const country1 = (profilesMap[jid1] && getCountryName(profilesMap[jid1].country)) || 'ZZ';
      const country2 = (profilesMap[jid2] && getCountryName(profilesMap[jid2].country)) || 'ZZ';
      if (country1 !== country2) {
        return country1.localeCompare(country2);
      }

      const username1 = (profilesMap[jid1] && profilesMap[jid1].username) || 'ZZ';
      const username2 = (profilesMap[jid2] && profilesMap[jid2].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map(jid => (
      <tr key={jid}>
        <td>{profilesMap[jid] && getCountryName(profilesMap[jid].country)}</td>
        <td>
          <UserRef profile={profilesMap[jid]} showFlag />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>Country</th>
            <th>User</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };
}

function createContestRegistrantsDialog(contestContestantActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });
  const mapDispatchToProps = {
    onGetApprovedContestants: contestContestantActions.getApprovedContestants,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ContestRegistrantsDialog));
}

export default createContestRegistrantsDialog(injectedContestContestantActions);
