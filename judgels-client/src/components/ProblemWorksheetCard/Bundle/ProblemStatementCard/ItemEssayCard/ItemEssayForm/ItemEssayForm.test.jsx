import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemEssayForm from './ItemEssayForm';

describe('ItemEssayForm', () => {
  const itemConfig = {
    statement: 'statement',
    score: 100,
  };

  const renderComponent = ({
    answerState = AnswerState.NotAnswered,
    initialAnswer,
    disabled = false,
    onSubmit = vi.fn(() => Promise.resolve(true)),
  } = {}) => {
    window.confirm = vi.fn(() => true);

    render(
      <ItemEssayForm
        jid="jid"
        type={ItemType.Essay}
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

  test('when not answered yet, renders textarea with empty value', () => {
    renderComponent();
    const textarea = screen.getByRole('textbox');
    expect(textarea).toHaveValue('');
  });

  test('when not answered yet, renders no buttons', () => {
    renderComponent();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test("when not answered yet, helptext should be 'Unanswered.'", () => {
    renderComponent();
    expect(screen.getByText(/Unanswered\./i)).toBeInTheDocument();
  });

  test("when not answered yet, textarea value should be 'answer'", async () => {
    renderComponent();
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.type(textarea, 'answer');
    expect(textarea).toHaveValue('answer');
  });

  test('when not answered yet, cancelling filling the answer renders no buttons', async () => {
    renderComponent();
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.type(textarea, 'answer');

    const buttons = screen.getAllByRole('button');
    const cancelButton = buttons[buttons.length - 1];
    await user.click(cancelButton);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test('when not answered yet, submits the answer', async () => {
    const { onSubmit } = renderComponent();
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.type(textarea, 'answer');

    const buttons = screen.getAllByRole('button');
    const submitButton = buttons[0];
    await user.click(submitButton);
    expect(onSubmit).toBeCalled();
  });

  test('when answered, renders textarea with initial answer', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const textarea = screen.getByRole('textbox');
    expect(textarea).toHaveValue('initial');
  });

  test('when answered, renders Clear button', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const button = screen.getByRole('button');
    expect(button).toHaveTextContent('Clear');
  });

  test("when answered, helptext should be 'Answered'", () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    expect(screen.getByText(/Answered\./i)).toBeInTheDocument();
  });

  test("when answered, textarea value should be 'answer'", async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.click(textarea);
    await user.clear(textarea);
    await user.type(textarea, 'answer');
    expect(textarea).toHaveValue('answer');
  });

  test('when answered, cancelling filling the answer renders Clear button', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.click(textarea);
    await user.clear(textarea);
    await user.type(textarea, 'answer');

    const buttons = screen.getAllByRole('button');
    const cancelButton = buttons[buttons.length - 1];
    await user.click(cancelButton);
    const button = screen.getByRole('button');
    expect(button).toHaveTextContent('Clear');
  });

  test('when answered, submits the answer', async () => {
    const { onSubmit } = renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const user = userEvent.setup();
    const textarea = screen.getByRole('textbox');
    await user.click(textarea);
    await user.clear(textarea);
    await user.type(textarea, 'answer');

    const buttons = screen.getAllByRole('button');
    const submitButton = buttons[0];
    await user.click(submitButton);
    expect(onSubmit).toBeCalled();
  });

  test('when answered, clears the answer', async () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial' });
    const user = userEvent.setup();
    const clearButton = screen.getByRole('button');
    await user.click(clearButton);
    expect(window.confirm).toBeCalled();
  });

  test('when disabled, button should be disabled', () => {
    renderComponent({ answerState: AnswerState.AnswerSaved, initialAnswer: 'initial', disabled: true });
    const button = screen.getByRole('button');
    expect(button).toBeDisabled();
  });
});
