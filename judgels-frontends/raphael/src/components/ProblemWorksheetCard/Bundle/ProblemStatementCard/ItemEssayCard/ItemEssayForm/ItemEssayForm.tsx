import { ControlGroup, TextArea, Classes, Button, Intent, Callout } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { AnswerState, StatementButtonText } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import { Item } from 'modules/api/sandalphon/problemBundle';

import './ItemEssayForm.css';

export interface ItemEssayFormProps extends Item {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => Promise<any>;
  answerState: AnswerState;
}

export interface ItemEssayFormState {
  answerState: AnswerState;
  answer: string;
  initialAnswer: string;
  cancelButtonState: AnswerState.NotAnswered | AnswerState.AnswerSaved;
}

export default class ItemEssayForm extends React.PureComponent<ItemEssayFormProps, ItemEssayFormState> {
  state: ItemEssayFormState = {
    answerState: this.props.answerState,
    answer: this.props.initialAnswer || '',
    initialAnswer: this.props.initialAnswer || '',
    cancelButtonState:
      this.props.answerState === AnswerState.NotAnswered ? AnswerState.NotAnswered : AnswerState.AnswerSaved,
  };

  renderTextAreaInput() {
    const readOnly =
      this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved;
    const readOnlyClass = readOnly ? 'readonly' : 'live';
    return (
      <TextArea
        name={this.props.meta}
        value={this.state.answer}
        onChange={this.onTextAreaInputChange}
        readOnly={readOnly}
        className={`form-textarea--code text-area ${readOnlyClass} ${classNames(Classes.INPUT)}`}
        rows={20}
      />
    );
  }

  renderSubmitButton() {
    let buttonText;
    let intent: Intent = Intent.PRIMARY;
    let disabled = false;
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        buttonText = StatementButtonText.Answer;
        break;
      case AnswerState.AnswerSaved:
      case AnswerState.ClearingAnswer:
        buttonText = StatementButtonText.Change;
        intent = Intent.NONE;
        break;
      default:
        buttonText = StatementButtonText.Submit;
        disabled = this.state.answer === '';
    }
    return <Button type="submit" text={buttonText} intent={intent} disabled={disabled} className="essay-button" />;
  }

  renderCancelButton() {
    return (
      this.state.answerState === AnswerState.Answering && (
        <Button
          type="button"
          text={StatementButtonText.Cancel}
          intent={Intent.DANGER}
          onClick={this.onCancelButtonClick}
          className="essay-button"
        />
      )
    );
  }

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return (
          <Callout intent={Intent.NONE} icon="circle" className="essay-callout">
            Not answered.
          </Callout>
        );
      case AnswerState.SavingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="essay-callout">
            Saving...
          </Callout>
        );
      case AnswerState.AnswerSaved:
        return (
          <Callout intent={Intent.PRIMARY} icon="confirm" className="essay-callout">
            Answered.
          </Callout>
        );
      case AnswerState.ClearingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="essay-callout">
            Clearing answer...
          </Callout>
        );
      default:
        return <div className="bp3-callout bp3-callout-icon essay-callout-edit">&nbsp;</div>;
    }
  }

  renderClearAnswerButton() {
    return (
      (this.state.answerState === AnswerState.AnswerSaved || this.state.answerState === AnswerState.ClearingAnswer) && (
        <Button
          type="button"
          text={StatementButtonText.ClearAnswer}
          intent={Intent.DANGER}
          onClick={this.onClearAnswerButtonClick}
          className="essay-button"
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
          answer: formValue,
          initialAnswer: '',
          cancelButtonState: AnswerState.NotAnswered,
        });
      }
    }
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

  onTextAreaInputChange = event => this.setState({ answer: event.target.value });

  onCancelButtonClick = () => {
    this.setState({
      answerState: this.state.cancelButtonState,
      answer: this.state.initialAnswer,
    });
  };

  render() {
    return (
      <form onSubmit={this.onSubmit} className="item-essay-form">
        <ControlGroup fill>{this.renderTextAreaInput()}</ControlGroup>
        <div className="divider" />
        <ControlGroup fill>
          {this.renderHelpText()}
          {this.renderSubmitButton()}
          {this.renderCancelButton()}
          {this.renderClearAnswerButton()}
        </ControlGroup>
      </form>
    );
  }
}
