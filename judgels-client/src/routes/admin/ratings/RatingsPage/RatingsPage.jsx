import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { contestsPendingRatingQueryOptions } from '../../../../modules/queries/contestRating';
import { ContestRatingChangesDialog } from '../ContestRatingChangesDialog/ContestRatingChangesDialog';
import { ContestsPendingRatingTable } from '../ContestsPendingRatingTable/ContestsPendingRatingTable';

export default function RatingsPage() {
  const [selectedContest, setSelectedContest] = useState(undefined);

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

    return (
      <ContestsPendingRatingTable
        contests={contests}
        onClickView={setSelectedContest}
        isContestViewed={!!selectedContest}
      />
    );
  };

  const renderContestRatingChangesDialog = () => {
    if (!response || !selectedContest) {
      return null;
    }

    return (
      <ContestRatingChangesDialog
        contest={selectedContest}
        ratingChanges={response.ratingChangesMap[selectedContest.jid]}
        onClose={() => setSelectedContest(undefined)}
      />
    );
  };

  return (
    <Card title="Ratings">
      <ContentCard>
        <h4>Contests pending rating changes</h4>
        <hr />
        {renderContestsPendingRatingTable()}
        {renderContestRatingChangesDialog()}
      </ContentCard>
    </Card>
  );
}
