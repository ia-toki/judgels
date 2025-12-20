import { HTMLTable } from '@blueprintjs/core';
import { parse } from 'query-string';
import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation } from 'react-router-dom';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';

import * as profileActions from '../../../jophiel/modules/profileActions';

import './RatingsPage.scss';

const PAGE_SIZE = 50;

export default function RatingsPage() {
  const [profiles, setProfiles] = useState();
  const location = useLocation();
  const dispatch = useDispatch();

  const [state, setState] = useState({
    profiles: undefined,
  });

  const render = () => {
    return (
      <Card title="Top ratings">
        {renderRatings()}
        <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} />
      </Card>
    );
  };

  const renderRatings = () => {
    const { profiles } = state;
    if (!profiles) {
      return <LoadingState />;
    }

    const page = +(parse(location.search).page || '1');
    const baseRank = (page - 1) * PAGE_SIZE + 1;
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

  const onChangePage = async nextPage => {
    const profiles = await dispatch(profileActions.getTopRatedProfiles(nextPage, PAGE_SIZE));
    setState({ profiles });
    return profiles.totalCount;
  };

  return render();
}
