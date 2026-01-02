import { useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { selectMaybeUserJid } from '../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';

import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

export default function ProblemSubmissionSummaryPage() {
  const { username } = useParams({ strict: false });
  const location = useLocation();
  const dispatch = useDispatch();
  const userJid = useSelector(selectMaybeUserJid);
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);
  const language = useSelector(selectStatementLanguage);

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

    const response = await dispatch(
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

    await dispatch(problemSetSubmissionActions.regradeSubmissions(problemSet.jid, userJid, problemJid));
    await refreshSubmissions();
  };

  return render();
}
