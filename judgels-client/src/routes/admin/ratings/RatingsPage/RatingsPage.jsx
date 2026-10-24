import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { contestsPendingRatingQueryOptions } from '../../../../modules/queries/contestRating';
import { ContestsPendingRatingTable } from '../ContestsPendingRatingTable/ContestsPendingRatingTable';

export default function RatingsPage() {
  const { data: response } = useQuery(contestsPendingRatingQueryOptions());

  const renderContestsPendingRatingTable = () => {
    if (!response) {
      return null;
    }

    const { data: contests } = response;
    if (contests.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    return <ContestsPendingRatingTable contests={contests} />;
  };

  return (
    <ContentCard title="Ratings">
      <div>
        <h4>Contests pending rating changes</h4>
        {renderContestsPendingRatingTable()}
      </div>
    </ContentCard>
  );
}
