import * as React from 'react';
import { InjectedFormProps, Field, reduxForm } from 'redux-form';
import { Button, Intent, Classes, ControlGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';

import './ItemStatementForm.css';
import { AnswerState, StatementButtonText } from '../itemStatement';

export interface ItemStatementFormProps {
  initialAnswer?: string;
  meta: string;
  onSubmit?: (answer?: string) => any;
  answerState: AnswerState;
}

export interface ItemStatementFormData {
  value?: string;
}

export interface InjectedProps extends InjectedFormProps<ItemStatementFormData, ItemStatementFormProps> {}

export interface ComponentProps extends ItemStatementFormProps, InjectedProps {}

export interface ComponentState {
  answerState: AnswerState;
}

class ItemStatementForm extends React.PureComponent<ComponentProps, ComponentState> {
  fill: boolean = true;
  constructor(props: ComponentProps) {
    super(props);
    this.state = { answerState: props.answerState };
  }

  renderInput = formProps => {
    return (
      <input {...formProps.input} disabled={formProps.disabled} className={`text-input ${classNames(Classes.INPUT)}`} />
    );
  };

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
    let disabled = false;
    if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
      disabled = true;
    }
    return (
      <Field
        name={this.props.meta}
        value={this.props.initialAnswer ? this.props.initialAnswer : ''}
        component={this.renderInput}
        onChange={this.onChange}
        disabled={disabled}
      />
    );
  }

  onChange = () => {
    this.setState({ answerState: AnswerState.Answering });
  };

  onSubmit = formValue => {
    if (this.state.answerState === AnswerState.NotAnswered || this.state.answerState === AnswerState.AnswerSaved) {
      this.setState({ answerState: AnswerState.Answering });
    } else {
      const oldValue = this.props.initialAnswer === undefined ? '' : this.props.initialAnswer;
      const newValue = formValue[this.props.meta];
      if (this.props.onSubmit && oldValue !== newValue) {
        this.setState({ answerState: AnswerState.SavingAnswer });
        this.props.onSubmit(newValue);
      }
    }
  };

  onCancelButtonClick = () => {
    this.setState({ answerState: this.props.answerState });
  };

  render() {
    return (
      <form onSubmit={this.props.handleSubmit(this.onSubmit)}>
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

export default reduxForm<ItemStatementFormData, ItemStatementFormProps>({
  form: 'item-statement-form',
})(ItemStatementForm);
