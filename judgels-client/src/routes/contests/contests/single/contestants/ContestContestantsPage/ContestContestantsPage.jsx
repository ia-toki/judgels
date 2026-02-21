import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import PaginationV2 from '../../../../../../components/PaginationV2/PaginationV2';
import {
  contestBySlugQueryOptions,
  resetVirtualContestMutationOptions,
} from '../../../../../../modules/queries/contest';
import { contestContestantsQueryOptions } from '../../../../../../modules/queries/contestContestant';
import { reallyConfirm } from '../../../../../../utils/confirmation';
import { ContestContestantAddDialog } from '../ContestContestantAddDialog/ContestContestantAddDialog';
import { ContestContestantRemoveDialog } from '../ContestContestantRemoveDialog/ContestContestantRemoveDialog';
import { ContestContestantsTable } from '../ContestContestantsTable/ContestContestantsTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestContestantsPage.scss';

const PAGE_SIZE = 1000;

export default function ContestContestantsPage() {
  const location = useLocation();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const page = +(location.search.page || 1);

  const { data: response } = useQuery(contestContestantsQueryOptions(contest.jid, { page }));

  const resetVirtualContestMutation = useMutation(resetVirtualContestMutationOptions(contest.jid));

  const onResetVirtualContest = async () => {
    if (reallyConfirm('Are you sure to reset all contestant virtual start times?')) {
      await resetVirtualContestMutation.mutateAsync(undefined, {
        onSuccess: () => {
          toastActions.showSuccessToast('All contestant virtual start time has been reset.');
        },
      });
    }
  };

  const renderContestants = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, virtualModuleConfig, profilesMap } = response;
    if (contestants.page.length === 0) {
      return (
        <p>
          <small>No contestants.</small>
        </p>
      );
    }

    return (
      <ContestContestantsTable
        contest={contest}
        virtualModuleConfig={virtualModuleConfig}
        contestants={contestants.page}
        profilesMap={profilesMap}
        now={new Date().getTime()}
      />
    );
  };

  const renderAddRemoveDialogs = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }
    const { data: contestants } = response;
    const isVirtualContest = contestants.page.some(contestant => contestant.contestStartTime !== null);
    return (
      <div className="content-card__header">
        <ContestContestantAddDialog contest={contest} />
        <ContestContestantRemoveDialog contest={contest} />
        {isVirtualContest && (
          <Button
            className="contest-contestant-dialog-button"
            intent={Intent.DANGER}
            icon={<Refresh />}
            onClick={onResetVirtualContest}
          >
            Reset all contestant virtual start times
          </Button>
        )}
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <ContentCard>
      <h3>Contestants</h3>
      <hr />
      {renderAddRemoveDialogs()}
      {renderContestants()}
      {response && <PaginationV2 pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
