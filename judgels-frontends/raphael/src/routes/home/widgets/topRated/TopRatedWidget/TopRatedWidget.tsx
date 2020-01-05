import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { Page } from '../../../../../modules/api/pagination';
import { Profile } from '../../../../../modules/api/jophiel/profile';
import { widgetActions as injectedWidgetActions } from '../../modules/widgetActions';

import './TopRatedWidget.css';

interface TopRatedWidgetProps {
  onGetTopRatedProfiles: (page?: number, pageSize?: number) => Promise<Page<Profile>>;
}

interface TopRatedWidgetState {
  profiles?: Page<Profile>;
}

class TopRatedWidget extends React.PureComponent<TopRatedWidgetProps, TopRatedWidgetState> {
  state: TopRatedWidgetState = {};

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
      <Card className="top-rated-widget" title="Top rated">
        {this.renderTable(profiles.page)}
      </Card>
    );
  }

  private renderTable = (profiles: Profile[]) => {
    if (profiles.length === 0) {
      return (
        <div className="top-rated-widget__empty">
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
      <HTMLTable striped className="table-list top-rated-widget__table">
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

function createTopRatedWidget(widgetActions) {
  const mapDispatchToProps = {
    onGetTopRatedProfiles: widgetActions.getTopRatedProfiles,
  };
  return connect(undefined, mapDispatchToProps)(TopRatedWidget);
}

export default createTopRatedWidget(injectedWidgetActions);
