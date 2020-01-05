import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { Page } from '../../../../../modules/api/pagination';
import { Profile } from '../../../../../modules/api/jophiel/profile';
import { widgetActions as injectedWidgetActions } from '../../modules/widgetActions';

import './TopRatingsWidget.css';

interface TopRatingsWidgetProps {
  onGetTopRatedProfiles: (page?: number, pageSize?: number) => Promise<Page<Profile>>;
}

interface TopRatingsWidgetState {
  profiles?: Page<Profile>;
}

class TopRatingsWidget extends React.PureComponent<TopRatingsWidgetProps, TopRatingsWidgetState> {
  state: TopRatingsWidgetState = {};

  async componentDidMount() {
    const profiles = await this.props.onGetTopRatedProfiles(1, 5);
    this.setState({ profiles });
  }

  render() {
    const { profiles } = this.state;
    if (!profiles) {
      return <LoadingState />;
    }

    return (
      <Card className="top-ratings-widget" title="Top ratings">
        {this.renderTable(profiles.page)}
      </Card>
    );
  }

  private renderTable = (profiles: Profile[]) => {
    if (profiles.length === 0) {
      return (
        <div className="top-ratings-widget__empty">
          <small>No data yet.</small>
        </div>
      );
    }

    const rows = profiles.map((profile, idx) => (
      <tr key={profile.username}>
        <td className="col-rank">{idx + 1}</td>
        <td>
          <UserRef profile={profile} showFlag />
        </td>
        <td className="col-rating">{profile.rating && profile.rating.publicRating}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list top-ratings-widget__table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th className="col-rating">Rating</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };
}

function createTopRatingsWidget(widgetActions) {
  const mapDispatchToProps = {
    onGetTopRatedProfiles: widgetActions.getTopRatedProfiles,
  };
  return connect(undefined, mapDispatchToProps)(TopRatingsWidget);
}

export default createTopRatingsWidget(injectedWidgetActions);
