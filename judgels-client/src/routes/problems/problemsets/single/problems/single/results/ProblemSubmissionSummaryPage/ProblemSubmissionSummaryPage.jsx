import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import {
  problemSetBundleSubmissionSummaryQueryOptions,
  regradeProblemSetBundleSubmissionsMutationOptions,
} from '../../../../../../../../modules/queries/problemSetSubmissionBundle';
import { useSession } from '../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';

import * as toastActions from '../../../../../../../../modules/toast/toastActions';

export default function ProblemSubmissionSummaryPage() {
  const { problemSetSlug, problemAlias, username } = useParams({ strict: false });
  const location = useLocation();
  const { user } = useSession();
  const userJid = user?.jid;
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { statementLanguage: language } = useWebPrefs();

  const { data: response } = useQuery({
    ...problemSetBundleSubmissionSummaryQueryOptions(problemSet.jid, {
      problemJid: problem.problemJid,
      username,
      language,
    }),
    enabled: !!userJid,
  });

  const regradeSubmissionsMutation = useMutation(regradeProblemSetBundleSubmissionsMutationOptions(problemSet.jid));

  const problemSummaries = !userJid
    ? []
    : response
      ? response.config.problemJids.map(problemJid => ({
          name: response.problemNamesMap[problemJid] || '-',
          itemJids: response.itemJidsByProblemJid[problemJid],
          submissionsByItemJid: response.submissionsByItemJid,
          canViewGrading: true,
          canManage: response.config.canManage,
          itemTypesMap: response.itemTypesMap,
          onRegrade: () => regrade(problemJid),
        }))
      : undefined;

  const regrade = async problemJid => {
    const { userJids } = response.config;
    const userJid = userJids[0];

    await regradeSubmissionsMutation.mutateAsync(
      { userJid, problemJid },
      {
        onSuccess: () => {
          toastActions.showSuccessToast('Regrade in progress.');
        },
      }
    );
  };

  const renderUserFilter = () => {
    if (location.pathname.includes('/users/')) {
      return null;
    }
    return <ItemSubmissionUserFilter />;
  };

  const renderResults = () => {
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No results.</small>;
    }
    return (
      <>
        <ContentCard>
          Summary for <UserRef profile={response.profile} />
        </ContentCard>
        {problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} />
        ))}
      </>
    );
  };

  return (
    <ContentCard>
      <h3>Results</h3>
      <hr />
      {renderUserFilter()}
      {renderResults()}
    </ContentCard>
  );
}
