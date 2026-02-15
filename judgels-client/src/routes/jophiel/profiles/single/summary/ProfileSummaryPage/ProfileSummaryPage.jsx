import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { isTLX } from '../../../../../../conf';
import { callAction } from '../../../../../../modules/callAction';
import { userJidByUsernameQueryOptions } from '../../../../../../modules/queries/profile';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import * as avatarActions from '../../../../modules/avatarActions';
import * as profileActions from '../../modules/profileActions';

import './ProfileSummaryPage.scss';

export default function ProfileSummaryPage() {
  const { username } = useParams({ strict: false });
  const { data: userJid } = useSuspenseQuery(userJidByUsernameQueryOptions(username));

  const [state, setState] = useState({
    avatarUrl: undefined,
    basicProfile: undefined,
    userStats: undefined,
  });

  const refreshSummary = async () => {
    const [avatarUrl, basicProfile, userStats] = await Promise.all([
      callAction(avatarActions.renderAvatar(userJid)),
      callAction(profileActions.getBasicProfile(userJid)),
      getUserStats(username),
    ]);
    setState(prevState => ({ ...prevState, avatarUrl, basicProfile, userStats }));
  };

  useEffect(() => {
    refreshSummary();
  }, [userJid]);

  const render = () => {
    return (
      <>
        {renderBasicProfile()}
        {renderProblemStats()}
      </>
    );
  };

  const renderBasicProfile = () => {
    const { avatarUrl, basicProfile } = state;
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  const renderProblemStats = () => {
    const { userStats } = state;
    if (!isTLX()) {
      return null;
    }
    if (!userStats) {
      return <LoadingState />;
    }

    return <ProblemStatsPanel userStats={userStats} />;
  };

  const getUserStats = usernameArg => {
    if (!isTLX()) {
      return Promise.resolve(null);
    }
    return callAction(profileActions.getUserStats(usernameArg));
  };

  return render();
}
