import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../components/RegradeAllButton/RegradeAllButton';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import {
  contestProgrammingSubmissionsQueryOptions,
  regradeProgrammingSubmissionMutationOptions,
  regradeProgrammingSubmissionsMutationOptions,
} from '../../../../../../../modules/queries/contestSubmissionProgramming';
import { reallyConfirm } from '../../../../../../../utils/confirmation';
import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

const PAGE_SIZE = 20;

function ContestSubmissionsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const username = location.search.username;
  const problemAlias = location.search.problemAlias;
  const page = location.search.page;

  const { data: response, isLoading } = useQuery(
    contestProgrammingSubmissionsQueryOptions(contest.jid, { username, problemAlias, page })
  );

  const regradeSubmissionMutation = useMutation(regradeProgrammingSubmissionMutationOptions(contest.jid));
  const regradeSubmissionsMutation = useMutation(regradeProgrammingSubmissionsMutationOptions(contest.jid));

  const renderHeader = () => {
    return (
      <div className="content-card__header">
        <div className="float-left">{renderRegradeAllButton()}</div>
        <div className="float-right">{renderFilterWidget()}</div>
        <div className="clearfix" />
      </div>
    );
  };

  const renderRegradeAllButton = () => {
    if (!response || !response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeAll} isRegradingAll={regradeSubmissionsMutation.isPending} />;
  };

  const renderFilterWidget = () => {
    if (!response) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids, canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    return (
      <SubmissionFilterWidget
        usernames={userJids.map(jid => profilesMap[jid] && profilesMap[jid].username)}
        problemAliases={problemJids.map(jid => problemAliasesMap[jid])}
        username={username}
        problemAlias={problemAlias}
        onFilter={onFilter}
        isLoading={isLoading && !!(username || problemAlias)}
      />
    );
  };

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={contest}
        submissions={submissions.page}
        canSupervise={config.canSupervise}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={onRegrade}
      />
    );
  };

  const onRegrade = async submissionJid => {
    await regradeSubmissionMutation.mutateAsync(submissionJid, {
      onSuccess: () => toastActions.showSuccessToast('Regrade in progress.'),
    });
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      await regradeSubmissionsMutation.mutateAsync(
        { username, problemAlias },
        {
          onSuccess: () => toastActions.showSuccessToast('Regrade in progress.'),
        }
      );
    }
  };

  const onFilter = async filter => {
    navigate({ search: filter });
  };

  return (
    <ContentCard>
      <h3>Submissions</h3>
      <hr />
      {renderHeader()}
      {renderSubmissions()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}

export default ContestSubmissionsPage;
