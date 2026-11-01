import { useQuery } from '@tanstack/react-query';

import { ButtonLink } from '../../../../../components/ButtonLink/ButtonLink';
import { Card } from '../../../../../components/Card/Card';
import { ContentList } from '../../../../../components/ContentList/ContentList';
import { LoadingContentCard } from '../../../../../components/LoadingContentCard/LoadingContentCard';
import { activeContestsQueryOptions } from '../../../../../modules/queries/contest';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';

export default function ActiveContestsWidget() {
  const { data: response } = useQuery(activeContestsQueryOptions());

  const renderActiveContests = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: contests, rolesMap } = response;
    if (contests.length === 0) {
      return (
        <p>
          <small>No active contests.</small>
        </p>
      );
    }
    return (
      <ContentList>
        {contests.map(contest => (
          <ActiveContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
        ))}
      </ContentList>
    );
  };

  return (
    <Card
      title="Active contests"
      action={
        <ButtonLink to="/contests" small minimal aria-label="See all active contests">
          See all
        </ButtonLink>
      }
    >
      {renderActiveContests()}
    </Card>
  );
}
