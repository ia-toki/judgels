import { useEffect, useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { callAction } from '../../../../modules/callAction';
import { ContestRatingChangesDialog } from '../ContestRatingChangesDialog/ContestRatingChangesDialog';
import { ContestsPendingRatingTable } from '../ContestsPendingRatingTable/ContestsPendingRatingTable';

import * as ratingActions from '../modules/ratingActions';

export default function RatingsPage() {
  const [state, setState] = useState({
    response: undefined,
    selectedContest: undefined,
    isApplyingRatingChanges: false,
  });

  const refreshContestsPendingRating = async () => {
    const response = await callAction(ratingActions.getContestsPendingRating());
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshContestsPendingRating();
  }, []);

  const render = () => {
    return <Card title="Ratings">{renderContestsPendingRating()}</Card>;
  };

  const renderContestsPendingRating = () => {
    return (
      <ContentCard>
        <h4>Contests pending rating changes</h4>
        <hr />
        {renderContestsPendingRatingTable()}
        {renderContestRatingChangesDialog()}
      </ContentCard>
    );
  };

  const renderContestsPendingRatingTable = () => {
    const { response } = state;
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
        onClickView={selectContest}
        isContestViewed={!!state.selectedContest}
      />
    );
  };

  const renderContestRatingChangesDialog = () => {
    const { response, selectedContest } = state;
    if (!response || !selectedContest) {
      return null;
    }

    return (
      <ContestRatingChangesDialog
        contest={selectedContest}
        ratingChanges={response.ratingChangesMap[selectedContest.jid]}
        onClose={closeContestRatingChangesDialog}
        onApply={applyRatingChanges}
        isApplying={state.isApplyingRatingChanges}
      />
    );
  };

  const closeContestRatingChangesDialog = () => {
    selectContest(undefined);
  };

  const selectContest = contest => {
    setState(prevState => ({ ...prevState, selectedContest: contest }));
  };

  const applyRatingChanges = async () => {
    setState(prevState => ({ ...prevState, isApplyingRatingChanges: true }));

    const { response, selectedContest } = state;
    await callAction(
      ratingActions.updateRatings({
        eventJid: selectedContest.jid,
        time: selectedContest.beginTime + selectedContest.duration,
        ratingsMap: response.ratingChangesMap[selectedContest.jid].ratingsMap,
      })
    );

    setState(prevState => ({ ...prevState, selectedContest: undefined, isApplyingRatingChanges: false }));
    await refreshContestsPendingRating();
  };

  return render();
}
