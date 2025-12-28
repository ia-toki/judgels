import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { selectStatementLanguage } from '../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../modules/contestSelectors';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

export default function ContestSubmissionSummaryPage() {
  const { username } = useParams();
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
  const language = useSelector(selectStatementLanguage);

  const [state, setState] = useState({
    config: undefined,
    profile: undefined,
    problemSummaries: [],
  });

  const refreshSubmissions = async () => {
    const response = await dispatch(contestSubmissionActions.getSubmissionSummary(contest.jid, username, language));

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
        {problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} />
        ))}
      </ContentCard>
    );
  };

  const regrade = async problemJid => {
    const { userJids } = state.config;
    const userJid = userJids[0];

    await dispatch(contestSubmissionActions.regradeSubmissions(contest.jid, userJid, problemJid));
    await refreshSubmissions();
  };

  return render();
}
