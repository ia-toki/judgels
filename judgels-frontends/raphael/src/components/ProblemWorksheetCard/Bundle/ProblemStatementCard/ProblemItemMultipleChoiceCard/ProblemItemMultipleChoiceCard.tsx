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
  initialAnswer?: string;
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
    this.state = { value: props.initialAnswer };
  }
  handleRadioChange = (event: React.FormEvent<HTMLInputElement>) => {
    const oldValue = this.state.value;
    const newValue = event.currentTarget.value;
    this.setState({ value: newValue });
    if (this.props.onChoiceChange && oldValue !== newValue) {
      this.props.onChoiceChange(newValue);
    }
  };
  handleRadioClick = (event: React.FormEvent<HTMLInputElement>) => {
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
                key={choice.alias}
                className="problem-multiple-choice-item-choice"
                value={choice.alias}
                onClick={this.handleRadioClick}
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
