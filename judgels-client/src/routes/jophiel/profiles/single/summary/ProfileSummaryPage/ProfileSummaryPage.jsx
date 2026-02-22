import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { isTLX } from '../../../../../../conf';
import { basicProfileQueryOptions, userJidByUsernameQueryOptions } from '../../../../../../modules/queries/profile';
import { userStatsQueryOptions } from '../../../../../../modules/queries/stats';
import { avatarUrlQueryOptions } from '../../../../../../modules/queries/userAvatar';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import './ProfileSummaryPage.scss';

export default function ProfileSummaryPage() {
  const { username } = useParams({ strict: false });
  const { data: userJid } = useSuspenseQuery(userJidByUsernameQueryOptions(username));

  const { data: avatarUrl } = useQuery(avatarUrlQueryOptions(userJid));
  const { data: basicProfile } = useQuery(basicProfileQueryOptions(userJid));
  const { data: userStats } = useQuery({
    ...userStatsQueryOptions(username),
    enabled: isTLX(),
  });

  const renderBasicProfile = () => {
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  const renderProblemStats = () => {
    if (!isTLX()) {
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
