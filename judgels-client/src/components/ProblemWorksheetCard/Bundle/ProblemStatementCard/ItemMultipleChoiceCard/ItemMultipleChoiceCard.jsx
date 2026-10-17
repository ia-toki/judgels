import { Card, Divider, Radio, RadioGroup, Tag } from '@blueprintjs/core';
import { Box, Flex } from '@blueprintjs/labs';
import { Component } from 'react';

import { HtmlText } from '../../../../HtmlText/HtmlText';
import RichStatementText from '../../../../RichStatementText/RichStatementText';

import './ItemMultipleChoiceCard.scss';

export class ItemMultipleChoiceCard extends Component {
  constructor(props) {
    super(props);
    this.state = { value: props.initialAnswer };
  }

  handleRadioChange = event => {
    const oldValue = this.state.value;
    const newValue = event.currentTarget.value;
    this.setState({ value: newValue });
    if (this.props.onChoiceChange && oldValue !== newValue) {
      this.props.onChoiceChange(newValue);
    }
  };

  handleRadioClick = event => {
    const oldValue = this.state.value;
    const newValue = event.currentTarget.value;
    if (oldValue === newValue) {
      this.setState({ value: undefined });
      if (this.props.onChoiceChange) {
        this.props.onChoiceChange(undefined);
      }
    }
  };

  render() {
    const config = this.props.config;
    return (
      <Card className={this.props.className}>
        <Flex alignItems="baseline" gap={1}>
          <div className="bundle-problem-statement-item__num">{this.props.itemNumber}.</div>
          <Box flexGrow={1}>
            <RichStatementText>{config.statement}</RichStatementText>
          </Box>
        </Flex>
        <Divider />
        <RadioGroup
          className="problem-multiple-choice-item-choices"
          onChange={this.handleRadioChange}
          selectedValue={this.state.value}
        >
          {config.choices.map(choice => (
            <Radio
              key={choice.alias}
              className="problem-multiple-choice-item-choice"
              value={choice.alias}
              onClick={this.handleRadioClick}
              disabled={this.props.disabled}
            >
              <Tag className="__alias-tag">
                <HtmlText>{choice.alias}</HtmlText>
              </Tag>
              <div className="__content">
                <RichStatementText>{choice.content.replace('<br>', '\n')}</RichStatementText>
              </div>
            </Radio>
          ))}
        </RadioGroup>
      </Card>
    );
  }
}
