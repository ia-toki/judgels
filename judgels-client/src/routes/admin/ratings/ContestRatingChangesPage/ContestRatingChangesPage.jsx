import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { NotFoundError } from '../../../../modules/api/error';
import { getRatingClass } from '../../../../modules/api/userRating';
import { contestsPendingRatingQueryOptions } from '../../../../modules/queries/contestRating';
import { updateUserRatingsMutationOptions } from '../../../../modules/queries/userRating';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ContestRatingChangesPage() {
  const { contestSlug } = useParams({ strict: false });
  const navigate = useNavigate();

  const {
    data: { data: contests, ratingChangesMap },
  } = useSuspenseQuery(contestsPendingRatingQueryOptions());
  const contest = contests.find(c => c.slug === contestSlug);
  if (!contest) {
    throw new NotFoundError();
  }
  const ratingChanges = ratingChangesMap[contest.jid];

  const updateRatingsMutation = useMutation(updateUserRatingsMutationOptions);

  const goToRatings = () => navigate({ to: '/admin/ratings' });

  const handleApply = () => {
    updateRatingsMutation.mutate(
      {
        eventJid: contest.jid,
        time: contest.beginTime + contest.duration,
        ratingsMap: ratingChanges.ratingsMap,
      },
      {
        onSuccess: () => {
          goToRatings();
          toastActions.showSuccessToast('Ratings updated.');
        },
      }
    );
  };

  const renderTable = () => {
    const { ratingsMap, profilesMap } = ratingChanges;
    const userJids = Object.keys(ratingsMap);
    userJids.sort((jid1, jid2) => {
      const rating1 = ratingsMap[jid1].publicRating;
      const rating2 = ratingsMap[jid2].publicRating;
      return rating2 - rating1;
    });

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>Username</th>
            <th>New rating</th>
          </tr>
        </thead>
        <tbody>
          {userJids.map(userJid => (
            <tr key={userJid}>
              <td>
                <UserRef profile={profilesMap[userJid]} />
              </td>
              <td>
                <span className={getRatingClass(ratingsMap[userJid])}>{ratingsMap[userJid].publicRating}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Ratings" current={contest.name} />}>
      <Flex flexDirection="column" gap={2}>
        {renderTable()}
        <ActionButtons justifyContent="end">
          <Button text="Cancel" disabled={updateRatingsMutation.isPending} onClick={goToRatings} />
          <Button
            intent={Intent.PRIMARY}
            text="Apply rating changes"
            onClick={handleApply}
            loading={updateRatingsMutation.isPending}
          />
        </ActionButtons>
      </Flex>
    </ContentCard>
  );
}
