import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { isTLX } from '../../../../../../conf';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import * as avatarActions from '../../../../modules/avatarActions';
import * as profileActions from '../../modules/profileActions';

import './ProfileSummaryPage.scss';

export default function ProfileSummaryPage() {
  const userJid = useSelector(selectUserJid);
  const username = useSelector(selectUsername);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    avatarUrl: undefined,
    basicProfile: undefined,
    userStats: undefined,
  });

  const refreshSummary = async () => {
    const [avatarUrl, basicProfile, userStats] = await Promise.all([
      dispatch(avatarActions.renderAvatar(userJid)),
      dispatch(profileActions.getBasicProfile(userJid)),
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
    return dispatch(profileActions.getUserStats(usernameArg));
  };

  return render();
}
