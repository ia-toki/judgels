import * as React from 'react';
import { Card } from '@blueprintjs/core';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { HtmlText } from 'components/HtmlText/HtmlText';

export type ItemStatementCardProps = Item & { className?: string };

export class ItemStatementCard extends React.PureComponent<ItemStatementCardProps> {
  render() {
    return (
      <Card className={this.props.className}>
        <HtmlText>{this.props.config.statement}</HtmlText>
      </Card>
    );
  }
}
