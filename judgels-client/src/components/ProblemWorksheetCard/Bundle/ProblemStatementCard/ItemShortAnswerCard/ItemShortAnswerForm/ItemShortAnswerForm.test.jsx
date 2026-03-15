import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm';

describe('ItemShortAnswerForm', () => {
  const itemConfig = {
    statement: 'statement',
    score: 4,
    penalty: -2,
    inputValidationRegex: '\\d+',
    gradingRegex: '\\d+',
  };

  const renderComponent = ({
    answerState = AnswerState.NotAnswered,
    initialAnswer,
    disabled = false,
    onSubmit = vi.fn(),
  } = {}) => {
    window.confirm = vi.fn(() => true);

    render(
      <ItemShortAnswerForm
        jid="jid"
        type={ItemType.ShortAnswer}
        meta="meta"
        config={itemConfig}
        disabled={disabled}
        initialAnswer={initialAnswer}
        onSubmit={onSubmit}
        answerState={answerState}
      />
    );

    return { onSubmit };
  };

  test('when not answered yet, text input has empty value', () => {
    renderComponent();
    const textInput = screen.getByRole('textbox');
    expect(textInput).toHaveValue('');
  });

  test('when not answered yet, renders no buttons', () => {
    renderComponent();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test("when not answered yet, helptext should be 'Unanswered.'", () => {
    renderComponent();
    expect(screen.getByText(/Unanswered\./i)).toBeInTheDocument();
  });

  test('when not answered yet, filling the answer with wrong format will render the new help text', async () => {
    renderComponent();
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.type(textInput, 'answer');
    expect(screen.getByText(/Wrong answer format!/i)).toBeInTheDocument();
  });

  test('when not answered yet, cancellling answer renders no buttons', async () => {
    renderComponent();
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.type(textInput, '1');

    const buttons = screen.getAllByRole('button');
    const cancelButton = buttons[buttons.length - 1];
    await user.click(cancelButton);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test('when not answered yet, submits the answer', async () => {
    const { onSubmit } = renderComponent();
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.type(textInput, '1');

    const buttons = screen.getAllByRole('button');
    const submitButton = buttons[0];
    await user.click(submitButton);
    expect(onSubmit).toBeCalled();
  });

  test('when answered, renders text input with initial answer', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const textInput = screen.getByRole('textbox');
    expect(textInput).toHaveValue('1');
  });

  test('when answered, renders Clear button', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const button = screen.getByRole('button');
    expect(button).toHaveTextContent('Clear');
  });

  test('when answered, changing the answer with right format', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.clear(textInput);
    await user.type(textInput, '2');
    expect(textInput).toHaveValue('2');
  });

  test('when answered, changing the answer with wrong format', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.clear(textInput);
    await user.type(textInput, 'answer');
    expect(screen.getByText(/Wrong answer format!/i)).toBeInTheDocument();
  });

  test('when answered, cancelling answer renders new Clear button', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.clear(textInput);
    await user.type(textInput, '2');

    const buttons = screen.getAllByRole('button');
    const cancelButton = buttons[buttons.length - 1];
    await user.click(cancelButton);
    const clearButton = screen.getByRole('button');
    expect(clearButton).toHaveTextContent('Clear');
  });

  test('when answered, submits the answer', async () => {
    const { onSubmit } = renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const user = userEvent.setup();
    const textInput = screen.getByRole('textbox');
    await user.click(textInput);
    await user.clear(textInput);
    await user.type(textInput, '2');

    const buttons = screen.getAllByRole('button');
    const submitButton = buttons[0];
    await user.click(submitButton);
    expect(onSubmit).toBeCalled();
  });

  test('when answered, clears the answer', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1' });
    const user = userEvent.setup();
    const clearButton = screen.getByRole('button');
    await user.click(clearButton);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test('when disabled, buttons should be disabled', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: '1', disabled: true });
    const button = screen.getByRole('button');
    expect(button).toBeDisabled();
  });
});
