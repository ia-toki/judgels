import { useMutation, useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { LoadingState } from '../../../components/LoadingState/LoadingState';
import PaginationV2 from '../../../components/PaginationV2/PaginationV2';
import SubmissionUserFilter from '../../../components/SubmissionUserFilter/SubmissionUserFilter';
import {
  regradeSubmissionMutationOptions,
  submissionsQueryOptions,
} from '../../../modules/queries/submissionProgramming';
import { useSession } from '../../../modules/session';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

import * as toastActions from '../../../modules/toast/toastActions';

const PAGE_SIZE = 20;

export default function SubmissionsPage() {
  const location = useLocation();
  const { user } = useSession();
  const userJid = user?.jid;
  const username = user?.username;

  const page = +(location.search.page || 1);
  const isUserFilterMine = (location.pathname + '/').includes('/mine/');
  const usernameFilter = isUserFilterMine ? username : undefined;

  const { data: response } = useQuery(submissionsQueryOptions({ username: usernameFilter, page }));

  const regradeMutation = useMutation(regradeSubmissionMutationOptions);

  const onRegrade = async submissionJid => {
    await regradeMutation.mutateAsync(submissionJid, {
      onSuccess: () => {
        toastActions.showSuccessToast('Submission regraded.');
      },
    });
  };

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      profilesMap,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
        onRegrade={onRegrade}
      />
    );
  };

  // return (
  //   <>
  //     {userJid && <SubmissionUserFilter />}
  //     {renderSubmissions()}
  //     {response && <PaginationV2 pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
  //   </>
  // );

  return <small>This page is under maintenance.</small>;
}
