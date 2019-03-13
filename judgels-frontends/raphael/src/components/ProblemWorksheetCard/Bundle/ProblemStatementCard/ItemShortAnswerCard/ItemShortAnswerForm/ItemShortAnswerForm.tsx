import * as React from 'react';
import { Button, Intent, Classes, ControlGroup, Callout } from '@blueprintjs/core';
import * as classNames from 'classnames';

import './ItemShortAnswerForm.css';
import { AnswerState, StatementButtonText } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import { Item, ItemShortAnswerConfig } from 'modules/api/sandalphon/problemBundle';

export interface ItemShortAnswerFormProps extends Item {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => Promise<any>;
  answerState: AnswerState;
}

export interface ItemShortAnswerFormState {
  answerState: AnswerState;
  answer: string;
  wrongFormat: boolean;
}

export default class ItemShortAnswerForm extends React.PureComponent<
  ItemShortAnswerFormProps,
  ItemShortAnswerFormState
> {
  fill: boolean = true;
  constructor(props: ItemShortAnswerFormProps) {
    super(props);
    this.state = {
      answerState: props.answerState,
      answer: props.initialAnswer ? props.initialAnswer : '',
      wrongFormat: false,
    };
  }

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return (
          <Callout intent={Intent.NONE} icon="issue" className="callout">
            Belum ada jawaban
          </Callout>
        );
      case AnswerState.SavingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="callout">
            Menyimpan jawaban...
          </Callout>
        );
      case AnswerState.AnswerSaved:
        return (
          <Callout intent={Intent.SUCCESS} icon="tick-circle" className="callout">
            Jawaban Tersimpan!
          </Callout>
        );
      default:
        return <div />;
    }
  }

  renderSubmitButton() {
    let buttonText;
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        buttonText = StatementButtonText.Answer;
        break;
      case AnswerState.AnswerSaved:
        buttonText = StatementButtonText.Change;
        break;
      default:
        buttonText = StatementButtonText.Submit;
    }
    return <Button type="submit" text={buttonText} intent={Intent.PRIMARY} className="button" />;
  }

  renderCancelButton() {
    if (this.state.answerState === AnswerState.Answering) {
      return (
        <Button
          type="button"
          text={StatementButtonText.Cancel}
          intent={Intent.DANGER}
          onClick={this.onCancelButtonClick}
          className="button"
        />
      );
    } else {
      return <div className="button" />;
    }
  }

  renderWrongFormatNotice() {
    if (this.state.wrongFormat) {
      return (
        <Callout intent={Intent.DANGER} icon="info-sign" className="callout">
          <strong>Format jawaban salah</strong>
        </Callout>
      );
    } else {
      return <div />;
    }
  }

  renderTextInput() {
    let readOnly = false;
    if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
      readOnly = true;
    }
    return (
      <input
        name={this.props.meta}
        value={this.state.answer}
        onChange={this.onTextInputChange}
        readOnly={readOnly}
        className={`text-input ${classNames(Classes.INPUT)}`}
      />
    );
  }

  onTextInputChange = event => {
    const value = event.target.value as string;
    const config: ItemShortAnswerConfig = this.props.config as ItemShortAnswerConfig;
    const formatValid = value.match(config.inputValidationRegex);
    formatValid
      ? this.setState({ answer: event.target.value, wrongFormat: false })
      : this.setState({ answer: event.target.value, wrongFormat: true });
  };

  onSubmit = async event => {
    event.preventDefault();
    const formValue = this.state.answer;
    if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
      this.setState({ answerState: AnswerState.Answering });
    } else {
      const oldValue = this.props.initialAnswer === undefined ? '' : this.props.initialAnswer;
      const newValue = formValue;
      if (this.props.onSubmit && oldValue !== newValue) {
        this.setState({ answerState: AnswerState.SavingAnswer });
        const val = await this.props.onSubmit(newValue);
        if (val === 1) {
          this.setState({ answerState: AnswerState.AnswerSaved });
        }
      }
    }
  };

  onCancelButtonClick = () => {
    this.setState({ answerState: this.props.answerState, answer: this.props.initialAnswer!, wrongFormat: false });
  };

  render() {
    return (
      <form onSubmit={this.onSubmit}>
        <ControlGroup fill={this.fill} className="item-statement-form">
          {this.renderTextInput()}
          {this.renderSubmitButton()}
          {this.renderCancelButton()}
        </ControlGroup>
        <div>{this.renderWrongFormatNotice()}</div>
        <div>{this.renderHelpText()}</div>
      </form>
    );
  }
}
