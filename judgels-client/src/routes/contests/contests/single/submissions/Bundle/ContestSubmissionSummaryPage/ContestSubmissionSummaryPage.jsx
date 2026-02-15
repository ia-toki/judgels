import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { callAction } from '../../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../modules/webPrefs';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

export default function ContestSubmissionSummaryPage() {
  const { contestSlug, username } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const { statementLanguage: language } = useWebPrefs();

  const [state, setState] = useState({
    config: undefined,
    profile: undefined,
    problemSummaries: [],
  });

  const refreshSubmissions = async () => {
    const response = await callAction(contestSubmissionActions.getSubmissionSummary(contest.jid, username, language));

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: response.config.canManage,
      canManage: response.config.canManage,
      itemTypesMap: response.itemTypesMap,
      onRegrade: () => regrade(problemJid),
    }));

    setState({ config: response.config, profile: response.profile, problemSummaries });
  };

  useEffect(() => {
    refreshSubmissions();
  }, []);

  const render = () => {
    if (!state.profile) {
      return null;
    }
    return (
      <ContentCard className="contest-submision-summary-page">
        <h3>Submissions</h3>
        <hr />
        <ContentCard>
          Summary for <UserRef profile={state.profile} />
        </ContentCard>
        {state.problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} />
        ))}
      </ContentCard>
    );
  };

  const regrade = async problemJid => {
    const { userJids } = state.config;
    const userJid = userJids[0];

    await callAction(contestSubmissionActions.regradeSubmissions(contest.jid, userJid, problemJid));
    await refreshSubmissions();
  };

  return render();
}
