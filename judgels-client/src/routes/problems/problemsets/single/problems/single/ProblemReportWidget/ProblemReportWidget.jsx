import { HTMLTable } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import ProblemDifficulty from '../../../../../../../components/ProblemDifficulty/ProblemDifficulty';
import ProblemSpoilerWidget from '../../../../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import ProblemTopicTags from '../../../../../../../components/ProblemTopicTags/ProblemTopicTags';
import { ProgressBar } from '../../../../../../../components/ProgressBar/ProgressBar';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { VerdictProgressTag } from '../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../modules/problemSetProblemSelectors';
import ProblemEditorialDialog from '../ProblemEditorialDialog/ProblemEditorialDialog';

import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

import './ProblemReportWidget.scss';

class ProblemReportWidget extends Component {
  static TOP_STATS_SIZE = 5;

  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblemReport(
      this.props.problemSet.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    return (
      <div className="problem-report-widget">
        {this.renderContests(response)}
        {this.renderProgress(response)}
        {this.renderSpoilers(response)}
        {this.renderTopStats(response)}
      </div>
    );
  }

  renderContests = ({ contests }) => {
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

  renderProgress = ({ progress }) => {
    return (
      <ContentCard>
        <h4 className="progress-title">Your score</h4>
        <VerdictProgressTag {...progress} />
        <br />
        <ProgressBar num={progress.score} denom={100} verdict={progress.verdict} />
      </ContentCard>
    );
  };

  renderSpoilers = ({ metadata, difficulty, profilesMap }) => {
    return (
      <ContentCard>
        <h4>Spoilers</h4>
        {this.renderSpoilersWidget()}
        {this.renderDifficulty({ difficulty })}
        {this.renderTopicTags({ metadata })}
        {this.renderEditorial({ metadata, profilesMap })}
      </ContentCard>
    );
  };

  renderSpoilersWidget = () => {
    return <ProblemSpoilerWidget />;
  };

  renderDifficulty = ({ difficulty }) => {
    return <ProblemDifficulty problem={this.props.problem} difficulty={difficulty} />;
  };

  renderTopicTags = ({ metadata }) => {
    return <ProblemTopicTags tags={metadata.tags} alignLeft />;
  };

  renderEditorial = ({ metadata, profilesMap }) => {
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

  renderTopStats = ({ topStats, profilesMap }) => {
    const { topUsersByScore, topUsersByTime, topUsersByMemory } = topStats;
    if (
      topUsersByScore.length === ProblemReportWidget.TOP_STATS_SIZE &&
      topUsersByTime.length === ProblemReportWidget.TOP_STATS_SIZE &&
      topUsersByScore[ProblemReportWidget.TOP_STATS_SIZE - 1].stats >= 100
    ) {
      return (
        <>
          {this.renderTopTime(topUsersByTime, profilesMap)}
          {this.renderTopMemory(topUsersByMemory, profilesMap)}
        </>
      );
    } else {
      return this.renderTopScore(topUsersByScore, profilesMap);
    }
  };

  renderTopScore = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'score', 'Score', '');
  };

  renderTopTime = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'time', 'Time', 'ms');
  };
  renderTopMemory = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'memory', 'Memory', 'KB');
  };

  renderTopEntries = (entries, profilesMap, title, header, suffix) => {
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
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
});
const mapDispatchToProps = {
  onGetProblemReport: problemSetProblemActions.getProblemReport,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemReportWidget));
