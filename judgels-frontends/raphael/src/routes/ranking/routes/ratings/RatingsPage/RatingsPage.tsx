import { parse } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from 'components/Card/Card';
import Pagination from 'components/Pagination/Pagination';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { UserRef } from 'components/UserRef/UserRef';
import { Page } from 'modules/api/pagination';
import { Profile } from 'modules/api/jophiel/profile';
import { profileActions as injectedProfileActions } from 'routes/jophiel/modules/profileActions';

import './RatingsPage.css';

interface RatingsPageProps extends RouteComponentProps<{}> {
  onGetTopRatedProfiles: (page?: number, pageSize?: number) => Promise<Page<Profile>>;
  onAppendRoute: (queries: any) => any;
}

interface RatingsPageState {
  profiles?: Page<Profile>;
}

class RatingsPage extends React.Component<RatingsPageProps, RatingsPageState> {
  private static PAGE_SIZE = 100;

  state: RatingsPageState = {};

  render() {
    return (
      <Card title="Top ratings">
        {this.renderRatings()}
        <Pagination currentPage={1} pageSize={RatingsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private renderRatings = () => {
    const { profiles } = this.state;
    if (!profiles) {
      return <LoadingState />;
    }

    const page = +(parse(this.props.location.search).page || '1');
    const baseRank = (page - 1) * RatingsPage.PAGE_SIZE + 1;
    const rows = profiles.data.map((profile, idx) => (
      <tr key={profile.username}>
        <td className="col-rank">{baseRank + idx}</td>
        <td>
          <UserRef profile={profile} showFlag />
        </td>
        <td>{profile.rating}</td>
      </tr>
    ));

    return (
      <table className="bp3-html-table bp3-html-table-striped table-list ratings-page-table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th>Rating</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  };

  private onChangePage = async (nextPage: number) => {
    const profiles = await this.props.onGetTopRatedProfiles(nextPage, RatingsPage.PAGE_SIZE);
    this.setState({ profiles });
    return profiles.totalData;
  };
}

function createRatingsPage(profileActions) {
  const mapDispatchToProps = {
    onGetTopRatedProfiles: profileActions.getTopRatedProfiles,
  };
  return connect(undefined, mapDispatchToProps)(RatingsPage);
}

export default withRouter<any>(createRatingsPage(injectedProfileActions));
