import { useQuery } from '@tanstack/react-query';

import { LoadingContentCard } from '../../../../../components/LoadingContentCard/LoadingContentCard';
import { activeContestsQueryOptions } from '../../../../../modules/queries/contest';
import { WidgetHeader } from '../../WidgetHeader/WidgetHeader';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';

import './ActiveContestsWidget.scss';

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
      <div className="active-contests">
        {contests.map(contest => (
          <ActiveContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
        ))}
      </div>
    );
  };

  return (
    <div>
      <WidgetHeader title="Active contests" to="/contests" />
      {renderActiveContests()}
    </div>
  );
}
