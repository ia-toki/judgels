import * as React from 'react';
import { Card, Divider, RadioGroup, Radio, Tag } from '@blueprintjs/core';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { HtmlText } from 'components/HtmlText/HtmlText';

import './ProblemItemMultipleChoiceCard.css';

export interface ItemMultipleChoiceConfig {
  statement: string;
  choices: {
    alias: string;
    content: string;
  }[];
}

export interface ProblemItemMultipleChoiceCardProps extends Item {
  className?: string;
  onChoiceChange?: (choice?: string) => any;
}

export interface ProblemItemMultipleChoiceCardState {
  value?: string;
}

export class ProblemItemMultipleChoiceCard extends React.Component<
  ProblemItemMultipleChoiceCardProps,
  ProblemItemMultipleChoiceCardState
> {
  constructor(props: ProblemItemMultipleChoiceCardProps) {
    super(props);
    this.state = { value: undefined };
  }
  handleRadioChange = (event: React.FormEvent<HTMLInputElement>) => {
    const newValue = event.currentTarget.value;
    const value = newValue === this.state.value ? undefined : newValue;
    this.setState({ value });
    if (this.props.onChoiceChange) {
      this.props.onChoiceChange(value);
    }
  };
  render() {
    try {
      const config: ItemMultipleChoiceConfig = JSON.parse(this.props.config);
      return (
        <Card className={this.props.className}>
          <HtmlText>{config.statement}</HtmlText>
          <Divider />
          <RadioGroup
            className="problem-multiple-choice-item-choices"
            onChange={this.handleRadioChange}
            selectedValue={this.state.value}
          >
            {config.choices.map(choice => (
              <Radio
                className="problem-multiple-choice-item-choice"
                value={choice.alias}
                onClick={this.handleRadioChange}
              >
                <Tag className="__alias-tag">
                  <HtmlText>{choice.alias}</HtmlText>
                </Tag>
                <div className="__content">
                  <HtmlText>{choice.content}</HtmlText>
                </div>
              </Radio>
            ))}
          </RadioGroup>
        </Card>
      );
    } catch (error) {
      return <React.Fragment />;
    }
  }
}
