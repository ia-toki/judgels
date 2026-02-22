import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import PaginationV2 from '../../../../components/PaginationV2/PaginationV2';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { topRatedProfilesQueryOptions } from '../../../../modules/queries/profile';

import './RatingsPage.scss';

const PAGE_SIZE = 50;

export default function RatingsPage() {
  const location = useLocation();
  const page = +(location.search.page || 1);

  const { data: profiles } = useQuery(topRatedProfilesQueryOptions({ page, pageSize: PAGE_SIZE }));

  const renderRatings = () => {
    if (!profiles) {
      return <LoadingState />;
    }

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

  return (
    <Card title="Top ratings">
      {renderRatings()}
      {profiles && <PaginationV2 pageSize={PAGE_SIZE} totalCount={profiles.totalCount} />}
    </Card>
  );
}
