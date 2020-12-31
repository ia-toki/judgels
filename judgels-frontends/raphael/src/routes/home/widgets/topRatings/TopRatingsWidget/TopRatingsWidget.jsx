import { HTMLTable } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import * as widgetActions from '../../modules/widgetActions';

import './TopRatingsWidget.css';

class TopRatingsWidget extends Component {
  state = {
    profiles: undefined,
  };

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

  renderTable = profiles => {
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

const mapDispatchToProps = {
  onGetTopRatedProfiles: widgetActions.getTopRatedProfiles,
};
export default connect(undefined, mapDispatchToProps)(TopRatingsWidget);
