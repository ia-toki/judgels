import { Link } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { Card } from '../../../../../components/Card/Card';
import { callAction } from '../../../../../modules/callAction';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';
import { LoadingActiveContestCard } from '../ActiveContestCard/LoadingActiveContestCard';

import * as contestActions from '../../../../contests/contests/modules/contestActions';

export default function ActiveContestsWidget() {
  const [response, setResponse] = useState(undefined);

  useEffect(() => {
    (async () => {
      const resp = await callAction(contestActions.getActiveContests());
      setResponse(resp);
    })();
  }, []);

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
