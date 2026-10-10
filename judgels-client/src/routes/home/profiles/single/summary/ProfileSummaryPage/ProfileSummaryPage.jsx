import { Flex } from '@blueprintjs/labs';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { isTLX } from '../../../../../../conf';
import { basicProfileQueryOptions } from '../../../../../../modules/queries/profile';
import { trainingUserStatsQueryOptions } from '../../../../../../modules/queries/trainingUserStats';
import { userAvatarUrlQueryOptions } from '../../../../../../modules/queries/userAvatar';
import { userJidByUsernameQueryOptions } from '../../../../../../modules/queries/userSearch';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import './ProfileSummaryPage.scss';

export default function ProfileSummaryPage() {
  const { username } = useParams({ strict: false });
  const { data: userJid } = useSuspenseQuery(userJidByUsernameQueryOptions(username));

  const { data: avatarUrl } = useQuery(userAvatarUrlQueryOptions(userJid));
  const { data: basicProfile } = useQuery(basicProfileQueryOptions(userJid));
  const { data: userStats } = useQuery({
    ...trainingUserStatsQueryOptions(username),
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
    <Flex gap={2} flexDirection="column">
      {renderBasicProfile()}
      {renderProblemStats()}
    </Flex>
  );
}
