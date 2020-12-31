import { Card, Divider, RadioGroup, Radio, Tag } from '@blueprintjs/core';
import { Component } from 'react';

import { HtmlText } from '../../../../HtmlText/HtmlText';

import './ItemMultipleChoiceCard.css';

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
              disabled={this.props.disabled}
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
