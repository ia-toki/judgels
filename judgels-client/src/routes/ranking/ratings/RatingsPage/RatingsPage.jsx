import { HTMLTable } from '@blueprintjs/core';
import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';

import * as profileActions from '../../../jophiel/modules/profileActions';

import './RatingsPage.scss';

class RatingsPage extends Component {
  static PAGE_SIZE = 50;

  state = {
    profiles: undefined,
  };

  render() {
    return (
      <Card title="Top ratings">
        {this.renderRatings()}
        <Pagination pageSize={RatingsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  renderRatings = () => {
    const { profiles } = this.state;
    if (!profiles) {
      return <LoadingState />;
    }

    const page = +(parse(this.props.location.search).page || '1');
    const baseRank = (page - 1) * RatingsPage.PAGE_SIZE + 1;
    const rows = profiles.page.map((profile, idx) => (
      <tr key={profile.username}>
        <td className="col-rank">{baseRank + idx}</td>
        <td>
          <UserRef profile={profile} showFlag />
        </td>
        <td>{profile.rating && profile.rating.publicRating}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list ratings-page-table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th>Rating</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  onChangePage = async nextPage => {
    const profiles = await this.props.onGetTopRatedProfiles(nextPage, RatingsPage.PAGE_SIZE);
    this.setState({ profiles });
    return profiles.totalCount;
  };
}

const mapDispatchToProps = {
  onGetTopRatedProfiles: profileActions.getTopRatedProfiles,
};
export default withRouter(connect(undefined, mapDispatchToProps)(RatingsPage));
