import { Card, Divider, RadioGroup, Radio, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { HtmlText } from 'components/HtmlText/HtmlText';
import { Item, ItemMultipleChoiceConfig } from 'modules/api/sandalphon/problemBundle';

import './ItemMultipleChoiceCard.css';

export interface ItemMultipleChoiceCardProps extends Item {
  className?: string;
  initialAnswer?: string;
  onChoiceChange?: (choice?: string) => Promise<any>;
  itemNumber: number;
}

export interface ItemMultipleChoiceCardState {
  value?: string;
}

export class ItemMultipleChoiceCard extends React.Component<ItemMultipleChoiceCardProps, ItemMultipleChoiceCardState> {
  constructor(props: ItemMultipleChoiceCardProps) {
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
    const config: ItemMultipleChoiceConfig = this.props.config as ItemMultipleChoiceConfig;
    const disabled = this.props.reasonNotAllowedToSubmit != null;
    return (
      <Card className={this.props.className}>
        <div className="bundle-problem-statement-item__statement">
          <div className="__item-num">{this.props.itemNumber}.</div>
          <div className="__item-statement">
            <HtmlText>{config.statement}</HtmlText>
          </div>
        </div>
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
              disabled={disabled}
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
  }
}
