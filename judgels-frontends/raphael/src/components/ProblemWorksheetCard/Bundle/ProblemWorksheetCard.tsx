import * as React from 'react';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemWorksheet } from 'modules/api/sandalphon/problemBundle';

import './ProblemWorksheetCard.css';
import { ProblemInfo, getProblemName } from 'modules/api/sandalphon/problem';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';

export interface ProblemWorksheetCardProps {
  alias: string;
  worksheet: ProblemWorksheet;
  problemInfo: ProblemInfo;
  language: string;
  latestSubmission: { [id: string]: ItemSubmission };
  onItemAnswered: (itemJid: string, answer: string) => any;
}

export class ProblemWorksheetCard extends React.PureComponent<ProblemWorksheetCardProps> {
  render() {
    return <div className="bundle-problem-worksheet">{this.renderStatement()}</div>;
  }

  private renderStatement = () => {
    const { alias, worksheet, problemInfo, language, latestSubmission } = this.props;
    const title = getProblemName(problemInfo, language);
    return (
      <ProblemStatementCard
        onItemAnswered={this.props.onItemAnswered}
        title={title}
        alias={alias}
        items={worksheet.items}
        latestSubmission={latestSubmission}
      />
    );
  };
}
