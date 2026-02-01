import { HTMLTable } from '@blueprintjs/core';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';

import * as widgetActions from '../../modules/widgetActions';

import './TopRatingsWidget.scss';

export default function TopRatingsWidget() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    profiles: undefined,
  });

  const refreshTopRatedProfiles = async () => {
    const profiles = await dispatch(widgetActions.getTopRatedProfiles(1, 10));
    setState({ profiles });
  };

  useEffect(() => {
    refreshTopRatedProfiles();
  }, []);

  const render = () => {
    const { profiles } = state;
    if (!profiles) {
      return <LoadingState />;
    }

    return (
      <Card className="top-ratings-widget" title="Top ratings">
        {renderTable(profiles.page)}
      </Card>
    );
  };

  const renderTable = profiles => {
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

  return render();
}
