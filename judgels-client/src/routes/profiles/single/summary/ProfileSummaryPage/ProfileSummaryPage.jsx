import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { basicProfileByUsernameQueryOptions } from '../../../../../modules/queries/profile';
import { trainingUserStatsQueryOptions } from '../../../../../modules/queries/trainingUserStats';
import { userAvatarUrlQueryOptions } from '../../../../../modules/queries/userAvatar';
import { isTrainingEnabled } from '../../../../../modules/webConfig';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import './ProfileSummaryPage.scss';

export default function ProfileSummaryPage() {
  const { username } = useParams({ strict: false });
  const { data: basicProfile } = useSuspenseQuery(basicProfileByUsernameQueryOptions(username));

  const { data: avatarUrl } = useQuery(userAvatarUrlQueryOptions(basicProfile.userJid));
  const { data: userStats } = useQuery({
    ...trainingUserStatsQueryOptions(username),
    enabled: isTrainingEnabled(),
  });

  const renderBasicProfile = () => {
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  const renderProblemStats = () => {
    if (!isTrainingEnabled()) {
      return null;
    }
    if (!userStats) {
      return <LoadingState />;
    }

    return <ProblemStatsPanel userStats={userStats} />;
  };

  return (
    <>
      {renderBasicProfile()}
      {renderProblemStats()}
    </>
  );
}
