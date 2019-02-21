import * as React from 'react';
import { Card } from '@blueprintjs/core';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { HtmlText } from 'components/HtmlText/HtmlText';

export type ProblemItemStatementCardProps = Item & { className?: string };

export class ProblemItemStatementCard extends React.PureComponent<ProblemItemStatementCardProps> {
  render() {
    try {
      const config: { statement: string } = JSON.parse(this.props.config);
      return (
        <Card className={this.props.className}>
          <HtmlText>{config.statement}</HtmlText>
        </Card>
      );
    } catch (error) {
      return <React.Fragment />;
    }
  }
}
