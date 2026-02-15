import { useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { callAction } from '../../../../../../../../modules/callAction';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { useSession } from '../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';

import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

export default function ProblemSubmissionSummaryPage() {
  const { problemSetSlug, problemAlias, username } = useParams({ strict: false });
  const location = useLocation();
  const { token, user } = useSession();
  const userJid = user?.jid;
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));
  const { statementLanguage: language } = useWebPrefs();

  const [state, setState] = useState({
    config: undefined,
    profile: undefined,
    problemSummaries: undefined,
  });

  const refreshSubmissions = async () => {
    if (!userJid) {
      setState(prevState => ({ ...prevState, problemSummaries: [] }));
      return;
    }

    const response = await callAction(
      problemSetSubmissionActions.getSubmissionSummary(problemSet.jid, problem.problemJid, username, language)
    );

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
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
    return (
      <ContentCard>
        <h3>Results</h3>
        <hr />
        {renderUserFilter()}
        {renderResults()}
      </ContentCard>
    );
  };

  const renderUserFilter = () => {
    if (location.pathname.includes('/users/')) {
      return null;
    }
    return <ItemSubmissionUserFilter />;
  };

  const renderResults = () => {
    const { problemSummaries } = state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No results.</small>;
    }
    return (
      <>
        <ContentCard>
          Summary for <UserRef profile={state.profile} />
        </ContentCard>
        {state.problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} />
        ))}
      </>
    );
  };

  const regrade = async problemJid => {
    const { userJids } = state.config;
    const userJid = userJids[0];

    await callAction(problemSetSubmissionActions.regradeSubmissions(problemSet.jid, userJid, problemJid));
    await refreshSubmissions();
  };

  return render();
}
