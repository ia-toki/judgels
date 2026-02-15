import { HTMLTable } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { Link } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import ProblemDifficulty from '../../../../../../../components/ProblemDifficulty/ProblemDifficulty';
import ProblemSpoilerWidget from '../../../../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import ProblemTopicTags from '../../../../../../../components/ProblemTopicTags/ProblemTopicTags';
import { ProgressBar } from '../../../../../../../components/ProgressBar/ProgressBar';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { VerdictProgressTag } from '../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { callAction } from '../../../../../../../modules/callAction';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../modules/queries/problemSet';
import { useSession } from '../../../../../../../modules/session';
import ProblemEditorialDialog from '../ProblemEditorialDialog/ProblemEditorialDialog';

import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

import './ProblemReportWidget.scss';

const TOP_STATS_SIZE = 5;

export default function ProblemReportWidget() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { token } = useSession();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));

  const [state, setState] = useState({
    response: undefined,
  });

  const loadReport = async () => {
    const response = await callAction(problemSetProblemActions.getProblemReport(problemSet.jid, problemAlias));
    setState({ response });
  };

  useEffect(() => {
    loadReport();
  }, []);

  const render = () => {
    const { response } = state;
    if (!response) {
      return null;
    }

    return (
      <div className="problem-report-widget">
        {renderContests(response)}
        {renderProgress(response)}
        {renderSpoilers(response)}
        {renderTopStats(response)}
      </div>
    );
  };

  const renderContests = ({ contests }) => {
    if (contests.length === 0) {
      return null;
    }

    return (
      <ContentCard>
        <h4>Contests</h4>
        <ul>
          {contests.map(c => (
            <li key={c.slug}>
              <Link to={`/contests/${c.slug}`}>{c.name}</Link>
            </li>
          ))}
        </ul>
      </ContentCard>
    );
  };

  const renderProgress = ({ progress }) => {
    return (
      <ContentCard>
        <h4 className="progress-title">Your score</h4>
        <VerdictProgressTag {...progress} />
        <br />
        <ProgressBar num={progress.score} denom={100} verdict={progress.verdict} />
      </ContentCard>
    );
  };

  const renderSpoilers = ({ metadata, difficulty, profilesMap }) => {
    return (
      <ContentCard>
        <h4>Spoilers</h4>
        {renderSpoilersWidget()}
        {renderDifficulty({ difficulty })}
        {renderTopicTags({ metadata })}
        {renderEditorial({ metadata, profilesMap })}
      </ContentCard>
    );
  };

  const renderSpoilersWidget = () => {
    return <ProblemSpoilerWidget />;
  };

  const renderDifficulty = ({ difficulty }) => {
    return <ProblemDifficulty problem={problem} difficulty={difficulty} />;
  };

  const renderTopicTags = ({ metadata }) => {
    return <ProblemTopicTags tags={metadata.tags} alignLeft />;
  };

  const renderEditorial = ({ metadata, profilesMap }) => {
    const { hasEditorial, settersMap } = metadata;
    if (!hasEditorial) {
      return null;
    }
    return (
      <>
        <hr />
        <ProblemEditorialDialog settersMap={settersMap} profilesMap={profilesMap} />
      </>
    );
  };

  const renderTopStats = ({ topStats, profilesMap }) => {
    const { topUsersByScore, topUsersByTime, topUsersByMemory } = topStats;
    if (
      topUsersByScore.length === TOP_STATS_SIZE &&
      topUsersByTime.length === TOP_STATS_SIZE &&
      topUsersByScore[TOP_STATS_SIZE - 1].stats >= 100
    ) {
      return (
        <>
          {renderTopTime(topUsersByTime, profilesMap)}
          {renderTopMemory(topUsersByMemory, profilesMap)}
        </>
      );
    } else {
      return renderTopScore(topUsersByScore, profilesMap);
    }
  };

  const renderTopScore = (entries, profilesMap) => {
    return renderTopEntries(entries, profilesMap, 'score', 'Score', '');
  };

  const renderTopTime = (entries, profilesMap) => {
    return renderTopEntries(entries, profilesMap, 'time', 'Time', 'ms');
  };

  const renderTopMemory = (entries, profilesMap) => {
    return renderTopEntries(entries, profilesMap, 'memory', 'Memory', 'KB');
  };

  const renderTopEntries = (entries, profilesMap, title, header, suffix) => {
    if (entries.length === 0) {
      return null;
    }

    const rows = entries.map((e, idx) => (
      <tr key={e.userJid}>
        <td>{idx + 1}</td>
        <td>
          <UserRef profile={profilesMap[e.userJid]} />
        </td>
        <td>
          {e.stats} {suffix}
        </td>
      </tr>
    ));

    return (
      <ContentCard className="problem-report-widget">
        <h4>Top users by {title}</h4>
        <HTMLTable striped className="table-list stats-table">
          <thead>
            <tr>
              <th className="col-rank">#</th>
              <th>User</th>
              <th className="col-value">{header}</th>
            </tr>
          </thead>
          <tbody>{rows}</tbody>
        </HTMLTable>
      </ContentCard>
    );
  };

  return render();
}
