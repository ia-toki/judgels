import { Button, Callout, Classes, ControlGroup, Intent } from '@blueprintjs/core';
import { BanCircle, Circle, Confirm, Remove } from '@blueprintjs/icons';
import classNames from 'classnames';
import { PureComponent } from 'react';

import { AnswerState, StatementButtonText } from '../../../itemStatement';

import './ItemShortAnswerForm.scss';

export default class ItemShortAnswerForm extends PureComponent {
  _input;
  state = {
    answerState: this.props.answerState,
    answer: this.props.initialAnswer || '',
    initialAnswer: this.props.initialAnswer || '',
    cancelButtonState:
      this.props.answerState === AnswerState.NotAnswered ? AnswerState.NotAnswered : AnswerState.AnswerSaved,
    previousWrongFormat: this.props.answerState === AnswerState.NotAnswered,
    wrongFormat: this.props.answerState === AnswerState.NotAnswered,
  };

  componentDidUpdate() {
    this._input.focus();
  }

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return (
          <Callout intent={Intent.NONE} icon={<Circle />} className="callout">
            Unanswered.
          </Callout>
        );
      case AnswerState.SavingAnswer:
        return (
          <Callout intent={Intent.NONE} icon={<BanCircle />} className="callout">
            Saving...
          </Callout>
        );
      case AnswerState.AnswerSaved:
        return (
          <Callout intent={Intent.PRIMARY} icon={<Confirm />} className="callout">
            Answered.
          </Callout>
        );
      case AnswerState.ClearingAnswer:
        return (
          <Callout intent={Intent.NONE} icon={<BanCircle />} className="callout">
            Clearing Answer...
          </Callout>
        );
      default:
        return <div />;
    }
  }

  renderSubmitButton() {
    const disabledState =
      this.props.disabled ||
      !this.state.answer ||
      (this.state.wrongFormat && this.state.answerState === AnswerState.Answering);

    return (
      this.state.answerState == AnswerState.Answering && (
        <Button
          type="submit"
          text={StatementButtonText.Submit}
          intent={Intent.WARNING}
          disabled={disabledState}
          className="button"
        />
      )
    );
  }

  renderCancelButton() {
    return (
      this.state.answerState === AnswerState.Answering && (
        <Button
          type="button"
          text={StatementButtonText.Cancel}
          onClick={this.onCancelButtonClick}
          className="button"
          disabled={this.props.disabled}
        />
      )
    );
  }

  renderClearAnswerButton() {
    return (
      this.state.answerState === AnswerState.AnswerSaved && (
        <Button
          type="button"
          text={StatementButtonText.ClearAnswer}
          intent={Intent.DANGER}
          onClick={this.onClearAnswerButtonClick}
          className="button"
          disabled={this.props.disabled}
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
          previousWrongFormat: true,
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
        <Callout intent={Intent.DANGER} icon={<Remove />} className="callout">
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
        type="text"
        placeholder={this.state.answerState === AnswerState.NotAnswered ? '(click to input answer)' : undefined}
        name={this.props.meta}
        value={this.state.answer}
        onChange={this.onTextInputChange}
        onClick={this.onTextInputClick}
        readOnly={readOnly}
        ref={input => (this._input = input)}
        className={`text-input ${readOnlyClass} ${classNames(Classes.INPUT)}`}
      />
    );
  }

  onTextInputClick = () => {
    if (!this.props.disabled) {
      if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
        this.setState({ answerState: AnswerState.Answering });
      }
    }
  };

  onTextInputChange = event => {
    const value = event.target.value;
    const config = this.props.config;
    const formatValid = value.match(`^(${config.inputValidationRegex})$`);
    this.setState({
      answer: event.target.value,
      wrongFormat: !formatValid,
    });
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
          previousWrongFormat: this.state.wrongFormat,
        });
      }
    }
  };

  onCancelButtonClick = () => {
    this.setState({
      answerState: this.state.cancelButtonState,
      answer: this.state.initialAnswer,
      wrongFormat: this.state.previousWrongFormat,
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
        {this.renderWrongFormatNotice()}
        {this.renderHelpText()}
        <div className="clearfix" />
      </form>
    );
  }
}
