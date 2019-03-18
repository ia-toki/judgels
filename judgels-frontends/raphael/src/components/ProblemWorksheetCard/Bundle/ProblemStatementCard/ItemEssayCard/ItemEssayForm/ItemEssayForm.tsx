import * as React from 'react';

import './ItemEssayForm.css';
import { AnswerState, StatementButtonText } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { ControlGroup, TextArea, Classes, Button, Intent, Callout } from '@blueprintjs/core';
import * as classNames from 'classnames';

export interface ItemEssayFormProps extends Item {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => Promise<any>;
  answerState: AnswerState;
}

export interface ItemEssayFormState {
  answerState: AnswerState;
  answer: string;
}

export default class ItemEssayForm extends React.PureComponent<ItemEssayFormProps, ItemEssayFormState> {
  state: ItemEssayFormState = { answerState: this.props.answerState, answer: this.props.initialAnswer || '' };

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
        className={`item-essay-form text-area ${readOnlyClass} ${classNames(Classes.INPUT)}`}
      />
    );
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
    return <Button type="submit" text={buttonText} intent={Intent.PRIMARY} className="item-essay-form essay-button" />;
  }

  renderCancelButton() {
    return (
      this.state.answerState === AnswerState.Answering && (
        <Button
          type="button"
          text={StatementButtonText.Cancel}
          intent={Intent.DANGER}
          onClick={this.onCancelButtonClick}
          className="item-essay-form essay-button"
        />
      )
    );
  }

  renderHelpText() {
    switch (this.state.answerState) {
      case AnswerState.NotAnswered:
        return (
          <Callout intent={Intent.NONE} icon="issue" className="item-essay-form essay-callout">
            Not answered yet.
          </Callout>
        );
      case AnswerState.SavingAnswer:
        return (
          <Callout intent={Intent.NONE} icon="ban-circle" className="item-essay-form essay-callout">
            Saving answer...
          </Callout>
        );
      case AnswerState.AnswerSaved:
        return (
          <Callout intent={Intent.SUCCESS} icon="tick-circle" className="item-essay-form essay-callout">
            Answer saved!
          </Callout>
        );
      default:
        return <div className="bp3-callout bp3-callout-icon item-essay-form essay-callout-edit">&nbsp;</div>;
    }
  }

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
        this.setState({ answerState: AnswerState.AnswerSaved });
      }
    }
  };

  onTextAreaInputChange = event => this.setState({ answer: event.target.value });

  onCancelButtonClick = () => {
    this.setState({
      answerState: this.props.answerState,
      answer: this.props.initialAnswer || '',
    });
  };

  render() {
    return (
      <form onSubmit={this.onSubmit}>
        <ControlGroup fill>{this.renderTextAreaInput()}</ControlGroup>
        <div className="item-essay-form divider" />
        <ControlGroup fill>
          {this.renderHelpText()}
          {this.renderSubmitButton()}
          {this.renderCancelButton()}
        </ControlGroup>
      </form>
    );
  }
}
