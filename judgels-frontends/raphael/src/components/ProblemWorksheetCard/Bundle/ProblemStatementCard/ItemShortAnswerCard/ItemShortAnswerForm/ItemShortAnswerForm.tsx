import * as React from 'react';
import { Button, Intent, Classes, ControlGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';

import './ItemShortAnswerForm.css';
import { AnswerState, StatementButtonText } from 'components/ProblemWorksheetCard/Bundle/itemStatement';

export interface ItemShortAnswerFormProps {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => Promise<any>;
  answerState: AnswerState;
}

export interface ItemShortAnswerFormState {
  answerState: AnswerState;
  answer: string;
}

export default class ItemShortAnswerForm extends React.PureComponent<
  ItemShortAnswerFormProps,
  ItemShortAnswerFormState
> {
  fill: boolean = true;
  constructor(props: ItemShortAnswerFormProps) {
    super(props);
    this.state = { answerState: props.answerState, answer: props.initialAnswer ? props.initialAnswer : '' };
  }

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return <p>Belum ada jawaban</p>;
      case AnswerState.SavingAnswer:
        return <p>Menyimpan jawaban...</p>;
      case AnswerState.AnswerSaved:
        return <p>Jawaban Tersimpan!</p>;
      default:
        return <p />;
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

  onTextInputChange = event => this.setState({ answer: event.target.value });

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
    this.setState({ answerState: this.props.answerState, answer: this.props.initialAnswer! });
  };

  render() {
    return (
      <form onSubmit={this.onSubmit}>
        <ControlGroup fill={this.fill} className="item-statement-form">
          {this.renderTextInput()}
          {this.renderSubmitButton()}
          {this.renderCancelButton()}
        </ControlGroup>
        <div>{this.renderHelpText()}</div>
      </form>
    );
  }
}
