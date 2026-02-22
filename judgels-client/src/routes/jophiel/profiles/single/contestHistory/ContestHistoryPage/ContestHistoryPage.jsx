import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { Card } from '../../../../../../components/Card/Card';
import { ContestLink } from '../../../../../../components/ContestLink/ContestLink';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { getRatingClass } from '../../../../../../modules/api/jophiel/userRating';
import { profileContestHistoryQueryOptions } from '../../../../../../modules/queries/profile';

import './ContestHistoryPage.scss';

export default function ContestHistoryPage() {
  const { username } = useParams({ strict: false });

  const { data: response } = useQuery(profileContestHistoryQueryOptions(username));

  if (!response) {
    return <LoadingState />;
  }

  return <Card title="Contest history">{renderTable(response)}</Card>;
}

function renderTable(response) {
  const { data } = response;
  if (data.length === 0) {
    return (
      <p>
        <small>No contests.</small>
      </p>
    );
  }
  return (
    <HTMLTable striped condensed className="contest-history-table">
      <thead>
        <tr>
          <th className="col-row">#</th>
          <th>Contest</th>
          <th>Rank</th>
          <th>Rating change</th>
          <th>Diff</th>
        </tr>
      </thead>
      <tbody>{renderRows(response)}</tbody>
    </HTMLTable>
  );
}

function renderRows(response) {
  const { data, contestsMap } = response;
  const rows = [];
  let lastRating = null;

  data.forEach((event, idx) => {
    let ratingChange = '';
    let ratingDiff = '';

    if (event.rating) {
      if (lastRating === null) {
        ratingChange = <span className={getRatingClass(event.rating)}>{event.rating.publicRating}</span>;
      } else {
        ratingChange = (
          <>
            <span className={getRatingClass(lastRating)}>{lastRating.publicRating}</span>&nbsp;&rarr;&nbsp;
            <span className={getRatingClass(event.rating)}>{event.rating.publicRating}</span>
          </>
        );
        ratingDiff = renderRatingDiff(event.rating.publicRating - lastRating.publicRating);
      }
      lastRating = event.rating;
    }

    rows.push(
      <tr key={event.contestJid}>
        <td>{idx + 1}</td>
        <td>
          <ContestLink contest={contestsMap[event.contestJid]} />
        </td>
        <td>{event.rank}</td>
        <td>{ratingChange}</td>
        <td>{ratingDiff}</td>
      </tr>
    );
  });

  return rows.reverse();
}

function renderRatingDiff(diff) {
  if (diff === 0) {
    return '0';
  } else if (diff > 0) {
    return <span className="diff-positive">+{diff}</span>;
  } else {
    return <span className="diff-negative">&minus;{-diff}</span>;
  }
}
