import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { topRatedProfilesQueryOptions } from '../../../../../modules/queries/profile';

import './TopRatingsWidget.scss';

export default function TopRatingsWidget() {
  const { data: response } = useQuery(topRatedProfilesQueryOptions({ page: 1, pageSize: 10 }));

  if (!response) {
    return <LoadingState />;
  }

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

  return (
    <Card className="top-ratings-widget" title="Top ratings">
      {renderTable(response.page)}
    </Card>
  );
}
