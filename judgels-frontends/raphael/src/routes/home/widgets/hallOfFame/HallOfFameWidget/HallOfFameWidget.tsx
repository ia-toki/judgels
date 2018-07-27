import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { Page } from '../../../../../modules/api/pagination';
import { Profile } from '../../../../../modules/api/jophiel/profile';
import { profileActions as injectedProfileActions } from '../../../../jophiel/modules/profileActions';

import './HallOfFameWidget.css';

interface HallOfFameWidgetProps {
  onGetTopRatedProfiles: (page?: number, pageSize?: number) => Promise<Page<Profile>>;
}

interface HallOfFameWidgetState {
  profiles?: Page<Profile>;
}

class HallOfFameWidget extends React.PureComponent<HallOfFameWidgetProps, HallOfFameWidgetState> {
  state: HallOfFameWidgetState = {};

  async componentDidMount() {
    const profiles = await this.props.onGetTopRatedProfiles(1, 10);
    this.setState({ profiles });
  }

  render() {
    const { profiles } = this.state;
    if (!profiles) {
      return <LoadingState />;
    }

    return (
      <Card className="hall-of-fame-widget" title="Hall of fame">
        {this.renderTable(profiles.data)}
      </Card>
    );
  }

  private renderTable = (profiles: Profile[]) => {
    if (profiles.length === 0) {
      return (
        <div className="hall-of-fame-widget__empty">
          <small>No data yet.</small>
        </div>
      );
    }

    const rows = profiles.map((profile, idx) => (
      <tr key={profile.username}>
        <td className="col-rank">{idx + 1}</td>
        <td>
          <UserRef profile={profile} />
        </td>
        <td className="col-rating">{profile.rating}</td>
      </tr>
    ));

    return (
      <table className="pt-html-table pt-html-table-striped table-list hall-of-fame-widget__table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th className="col-rating">Rating</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  };
}

function createHallOfFameWidget(profileActions) {
  const mapDispatchToProps = {
    onGetTopRatedProfiles: profileActions.getTopRatedProfiles,
  };
  return connect(undefined, mapDispatchToProps)(HallOfFameWidget);
}

export default createHallOfFameWidget(injectedProfileActions);
