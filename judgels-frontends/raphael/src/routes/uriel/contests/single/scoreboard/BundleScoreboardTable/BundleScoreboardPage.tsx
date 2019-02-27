import * as classNames from 'classnames';
import * as React from 'react';

import './BundleScoreboardPage.css';
import { BundleScoreboard } from 'modules/api/uriel/scoreboard';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';
import { BundleScoreboardContent } from 'modules/api/uriel/scoreboard';
import { BundleScoreboardEntry } from 'modules/api/uriel/scoreboard';
import { UserRef } from 'components/UserRef/UserRef';

export class BundleScoreboardTableProps {
  userJid?: string;
  scoreboard: BundleScoreboard;
  profilesMap: ProfilesMap;
}

export class BundleScoreboardTable extends React.PureComponent<BundleScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <ScoreboardTable className="bundle-scoreboard__content" state={scoreboard.state}>
        {this.renderData(scoreboard.content)}
      </ScoreboardTable>
    );
  }

  private renderData = (content: BundleScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: BundleScoreboardEntry) => {
    const { scoreboard } = this.props;
    const problemItems = scoreboard.content.problemItems;
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={this.props.profilesMap[entry.contestantJid]} />
      </td>,
      <td key="totalAnsweredItems">
        <strong>{entry.totalAnsweredItems}</strong>
      </td>,
    ];
    const problemCells = entry.answeredItems.map((item, i) => this.renderProblemCell(i, item, problemItems[i]));
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === this.props.userJid })}>
        {cells}
      </tr>
    );
  };

  private renderProblemCell = (idx: number, answered: number, items: number) => {
    return (
      <td key={idx}>
        {answered}/{items}
      </td>
    );
  };
}
