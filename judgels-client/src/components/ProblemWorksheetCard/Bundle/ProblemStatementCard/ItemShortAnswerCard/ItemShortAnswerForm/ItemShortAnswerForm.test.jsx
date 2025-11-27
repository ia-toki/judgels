import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm';

describe('ItemShortAnswerForm', () => {
  const onSubmitFn = jest.fn();
  const itemConfig = {
    statement: 'statement',
    score: 4,
    penalty: -2,
    inputValidationRegex: '\\d+',
    gradingRegex: '\\d+',
  };

  beforeEach(() => {
    window.confirm = jest.fn(() => true);
  });

  describe('not answered yet condition', () => {
    const props = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: onSubmitFn,
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      render(<ItemShortAnswerForm {...props} />);
    });

    test('text input should has empty value', () => {
      const textInput = screen.getByRole('textbox');
      expect(textInput).toHaveValue('');
    });

    it('should render no buttons', () => {
      expect(screen.queryByRole('button')).not.toBeInTheDocument();
    });

    test("helptext should be 'Unanswered.'", () => {
      expect(screen.getByText(/Unanswered\./i)).toBeInTheDocument();
    });

    describe('fill and submit the answer', () => {
      beforeEach(async () => {
        const user = userEvent.setup();
        const textInput = screen.getByRole('textbox');
        await user.click(textInput);
        await user.type(textInput, '1');
      });

      test('fill the answer with wrong format will render the new help text', async () => {
        const user = userEvent.setup();
        const textInput = screen.getByRole('textbox');
        await user.clear(textInput);
        await user.type(textInput, 'answer');
        expect(screen.getByText(/Wrong answer format!/i)).toBeInTheDocument();
      });

      test('cancel answer should render no buttons', async () => {
        const user = userEvent.setup();
        const buttons = screen.getAllByRole('button');
        const cancelButton = buttons[buttons.length - 1];
        await user.click(cancelButton);
        expect(screen.queryByRole('button')).not.toBeInTheDocument();
      });

      test('submit the answer', async () => {
        const user = userEvent.setup();
        const buttons = screen.getAllByRole('button');
        const submitButton = buttons[0];
        await user.click(submitButton);
        expect(onSubmitFn).toBeCalled();
      });
    });
  });

  describe('answered', () => {
    const props = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      initialAnswer: '1',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      render(<ItemShortAnswerForm {...props} />);
    });

    it('should render text input with initial answer', () => {
      const textInput = screen.getByRole('textbox');
      expect(textInput).toHaveValue(props.initialAnswer);
    });

    it('should render Clear button', () => {
      const button = screen.getByRole('button');
      expect(button).toHaveTextContent('Clear');
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const user = userEvent.setup();
        const textInput = screen.getByRole('textbox');
        await user.click(textInput);
        await user.clear(textInput);
        await user.type(textInput, '2');
      });

      test('change the answer with right format', () => {
        const textInput = screen.getByRole('textbox');
        expect(textInput).toHaveValue('2');
      });

      test('change the answer with wrong format', async () => {
        const user = userEvent.setup();
        const textInput = screen.getByRole('textbox');
        await user.clear(textInput);
        await user.type(textInput, 'answer');
        expect(screen.getByText(/Wrong answer format!/i)).toBeInTheDocument();
      });

      test('cancel answer should render new Clear button', async () => {
        const user = userEvent.setup();
        const buttons = screen.getAllByRole('button');
        const cancelButton = buttons[buttons.length - 1];
        await user.click(cancelButton);
        const clearButton = screen.getByRole('button');
        expect(clearButton).toHaveTextContent('Clear');
      });

      test('submit the answer', async () => {
        const user = userEvent.setup();
        const buttons = screen.getAllByRole('button');
        const submitButton = buttons[0];
        await user.click(submitButton);
        expect(onSubmitFn).toBeCalled();
      });
    });

    test('clear the answer', async () => {
      const user = userEvent.setup();
      const clearButton = screen.getByRole('button');
      await user.click(clearButton);
      expect(screen.queryByRole('button')).not.toBeInTheDocument();
    });
  });

  describe('disabled', () => {
    const props = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: true,
      initialAnswer: '1',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      render(<ItemShortAnswerForm {...props} />);
    });

    it('buttons disabled', () => {
      const button = screen.getByRole('button');
      expect(button).toBeDisabled();
    });
  });
});
