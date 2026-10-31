import { HTMLTable } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { topRatedProfilesQueryOptions } from '../../../../../modules/queries/profile';
import { WidgetHeader } from '../../WidgetHeader/WidgetHeader';

import './TopRatingsWidget.scss';

export default function TopRatingsWidget() {
  const { data: response } = useQuery(topRatedProfilesQueryOptions({ page: 1, pageSize: 10 }));

  const renderTable = () => {
    if (!response) {
      return <LoadingState />;
    }

    const profiles = response.page;
    if (profiles.length === 0) {
      return (
        <Box padding={5}>
          <small>No data yet.</small>
        </Box>
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
    <div>
      <WidgetHeader title="Top ratings" to="/ranking" />
      <ContentCard className="top-ratings-widget">{renderTable()}</ContentCard>
    </div>
  );
}
