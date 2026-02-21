import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import {
  contestBundleSubmissionSummaryQueryOptions,
  regradeBundleSubmissionsMutationOptions,
} from '../../../../../../../modules/queries/contestSubmissionBundle';
import { useWebPrefs } from '../../../../../../../modules/webPrefs';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

export default function ContestSubmissionSummaryPage() {
  const { contestSlug, username } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage: language } = useWebPrefs();

  const { data: response } = useQuery(contestBundleSubmissionSummaryQueryOptions(contest.jid, { username, language }));

  const regradeSubmissionsMutation = useMutation(regradeBundleSubmissionsMutationOptions(contest.jid));

  if (!response) {
    return null;
  }

  const { config, profile } = response;

  const problemSummaries = config.problemJids.map(problemJid => ({
    name: response.problemNamesMap[problemJid] || '-',
    alias: response.problemAliasesMap[problemJid] || '-',
    itemJids: response.itemJidsByProblemJid[problemJid],
    submissionsByItemJid: response.submissionsByItemJid,
    canViewGrading: config.canManage,
    canManage: config.canManage,
    itemTypesMap: response.itemTypesMap,
    onRegrade: () => regrade(problemJid),
  }));

  const regrade = async problemJid => {
    const userJid = config.userJids[0];
    await regradeSubmissionsMutation.mutateAsync(
      { username: userJid, problemAlias: problemJid },
      {
        onSuccess: () => toastActions.showSuccessToast('Regraded.'),
      }
    );
  };

  return (
    <ContentCard className="contest-submision-summary-page">
      <h3>Submissions</h3>
      <hr />
      <ContentCard>
        Summary for <UserRef profile={profile} />
      </ContentCard>
      {problemSummaries.map(props => (
        <SubmissionDetails key={props.alias} {...props} />
      ))}
    </ContentCard>
  );
}
