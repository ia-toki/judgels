import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { Card } from '../../../../../components/Card/Card';
import { activeContestsQueryOptions } from '../../../../../modules/queries/contest';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';
import { LoadingActiveContestCard } from '../ActiveContestCard/LoadingActiveContestCard';

export default function ActiveContestsWidget() {
  const { data: response } = useQuery(activeContestsQueryOptions());

  const renderActiveContests = () => {
    if (!response) {
      return <LoadingActiveContestCard />;
    }

    const { data: contests, rolesMap } = response;
    if (contests.length === 0) {
      return (
        <p>
          <small>No active contests.</small>
        </p>
      );
    }
    return contests.map(contest => (
      <ActiveContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  return (
    <Card title="Active contests" className="active-contests-widget">
      {renderActiveContests()}

      <small>
        <Link to={'/contests'}>See all contests...</Link>
      </small>
    </Card>
  );
}
