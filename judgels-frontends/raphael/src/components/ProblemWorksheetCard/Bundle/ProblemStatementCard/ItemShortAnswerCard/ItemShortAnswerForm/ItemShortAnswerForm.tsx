import { Button, Intent, Classes, ControlGroup, Callout } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { AnswerState, StatementButtonText } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import { Item, ItemShortAnswerConfig } from 'modules/api/sandalphon/problemBundle';

import './ItemShortAnswerForm.css';

export interface ItemShortAnswerFormProps extends Item {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => Promise<any>;
  answerState: AnswerState;
}

export interface ItemShortAnswerFormState {
  answerState: AnswerState;
  initialAnswer: string;
  cancelButtonState: AnswerState.NotAnswered | AnswerState.AnswerSaved;
  answer: string;
  wrongFormat: boolean;
}

export default class ItemShortAnswerForm extends React.PureComponent<
  ItemShortAnswerFormProps,
  ItemShortAnswerFormState
> {
  state: ItemShortAnswerFormState = {
    answerState: this.props.answerState,
    answer: this.props.initialAnswer || '',
    initialAnswer: this.props.initialAnswer || '',
    cancelButtonState:
      this.props.answerState === AnswerState.NotAnswered ? AnswerState.NotAnswered : AnswerState.AnswerSaved,
    wrongFormat: true,
  };

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return (
          <Callout intent={Intent.NONE} icon="circle" className="callout">
            Not answered.
          </Callout>
        );
      case AnswerState.SavingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="callout">
            Saving...
          </Callout>
        );
      case AnswerState.AnswerSaved:
        return (
          <Callout intent={Intent.PRIMARY} icon="confirm" className="callout">
            Answered.
          </Callout>
        );
      case AnswerState.ClearingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="callout">
            Clearing Answer...
          </Callout>
        );
      default:
        return <div />;
    }
  }

  renderSubmitButton() {
    let buttonText;
    let intent: Intent = Intent.PRIMARY;
    const disabledState = this.state.wrongFormat && this.state.answerState === AnswerState.Answering;
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        buttonText = StatementButtonText.Answer;
        break;
      case AnswerState.AnswerSaved:
        buttonText = StatementButtonText.Change;
        intent = Intent.NONE;
        break;
      default:
        buttonText = StatementButtonText.Submit;
    }
    return <Button type="submit" text={buttonText} intent={intent} disabled={disabledState} className="button" />;
  }

  renderCancelButton() {
    return (
      this.state.answerState === AnswerState.Answering && (
        <Button
          type="button"
          text={StatementButtonText.Cancel}
          intent={Intent.DANGER}
          onClick={this.onCancelButtonClick}
          className="button"
        />
      )
    );
  }

  renderClearAnswerButton() {
    return (
      (this.state.answerState === AnswerState.AnswerSaved || this.state.answerState === AnswerState.ClearingAnswer) && (
        <Button
          type="button"
          text={StatementButtonText.ClearAnswer}
          intent={Intent.DANGER}
          onClick={this.onClearAnswerButtonClick}
          className="button"
        />
      )
    );
  }

  onClearAnswerButtonClick = async () => {
    const formValue = '';
    if (window.confirm('Are you sure to clear your answer?')) {
      if (this.props.onSubmit) {
        this.setState({ answerState: AnswerState.ClearingAnswer });
        await this.props.onSubmit(formValue);
        this.setState({
          answerState: AnswerState.NotAnswered,
          cancelButtonState: AnswerState.NotAnswered,
          answer: formValue,
          initialAnswer: '',
          wrongFormat: true,
        });
      }
    }
  };

  renderEmptyDiv() {
    return this.state.answerState !== AnswerState.Answering && <div className="button" />;
  }

  renderWrongFormatNotice() {
    const { answer, answerState, wrongFormat } = this.state;
    return (
      answer !== '' &&
      wrongFormat &&
      answerState === AnswerState.Answering && (
        <Callout intent={Intent.DANGER} icon="remove" className="callout">
          <strong>Wrong answer format!</strong>
        </Callout>
      )
    );
  }

  renderTextInput() {
    const readOnly =
      this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved;
    const readOnlyClass = readOnly ? 'readonly' : '';
    return (
      <input
        name={this.props.meta}
        value={this.state.answer}
        onChange={this.onTextInputChange}
        readOnly={readOnly}
        className={`text-input ${readOnlyClass} ${classNames(Classes.INPUT)}`}
      />
    );
  }

  onTextInputChange = event => {
    const value = event.target.value as string;
    const config: ItemShortAnswerConfig = this.props.config as ItemShortAnswerConfig;
    const formatValid = value.match(config.inputValidationRegex);
    this.setState({ answer: event.target.value, wrongFormat: !formatValid });
  };

  onSubmit = async event => {
    event.preventDefault();
    const formValue = this.state.answer;
    if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
      this.setState({ answerState: AnswerState.Answering });
    } else {
      const oldValue = this.props.initialAnswer || '';
      const newValue = formValue;
      if (this.props.onSubmit && oldValue !== newValue) {
        this.setState({ answerState: AnswerState.SavingAnswer });
        await this.props.onSubmit(newValue);
        this.setState({
          answerState: AnswerState.AnswerSaved,
          cancelButtonState: AnswerState.AnswerSaved,
          initialAnswer: newValue,
        });
      }
    }
  };

  onCancelButtonClick = () => {
    this.setState({
      answerState: this.state.cancelButtonState,
      answer: this.state.initialAnswer,
      wrongFormat: true,
    });
  };

  render() {
    return (
      <form onSubmit={this.onSubmit} className="item-short-answer-form">
        <ControlGroup fill className="answer-form">
          {this.renderTextInput()}
          {this.renderSubmitButton()}
          {this.renderCancelButton()}
          {this.renderClearAnswerButton()}
        </ControlGroup>
        <div>{this.renderWrongFormatNotice()}</div>
        <div>{this.renderHelpText()}</div>
        <div className="clearfix" />
      </form>
    );
  }
}
