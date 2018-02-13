import * as React from 'react';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import { Scoreboard, ScoreboardEntry } from '../../scoreboard/Scoreboard/Scoreboard';

import mockScoreboardEntries from './mockScoreboardEntries';

import './ScoreboardContainer.css';

interface ScoreboardContainerProps {
  title: string;
  time: string;
  entries: ScoreboardEntry[];
}

class ScoreboardContainer extends React.Component<ScoreboardContainerProps> {
  render() {
    return (
      <FullPageLayout>
        <div className="score-board__title">TOKI Open Contest April 2017</div>
        <div className="score-board__time">Thursday, April 02 2017 17:00-21:00</div>
        <Scoreboard entries={mockScoreboardEntries} />
      </FullPageLayout>
    );
  }
}

export default ScoreboardContainer;
