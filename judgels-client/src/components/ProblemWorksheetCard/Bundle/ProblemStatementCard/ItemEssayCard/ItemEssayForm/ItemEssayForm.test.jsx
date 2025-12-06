import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemEssayForm from './ItemEssayForm';

describe('ItemEssayForm', () => {
  const onSubmitFn = vi.fn(() => Promise.resolve(true));
  const itemConfig = {
    statement: 'statement',
    score: 100,
  };

  beforeEach(() => {
    window.confirm = vi.fn(() => true);
  });

  describe('not answered yet', () => {
    const props = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: onSubmitFn,
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      render(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('should render textarea with empty value', () => {
        const textarea = screen.getByRole('textbox');
        expect(textarea).toHaveValue('');
      });

      it('should be no buttons', () => {
        expect(screen.queryByRole('button')).not.toBeInTheDocument();
      });

      test("helptext should be 'Unanswered.'", () => {
        expect(screen.getByText(/Unanswered\./i)).toBeInTheDocument();
      });
    });

    describe('fill and submit the answer', () => {
      beforeEach(async () => {
        const user = userEvent.setup();
        const textarea = screen.getByRole('textbox');
        await user.type(textarea, 'answer');
      });

      test("textarea value should be 'answer'", () => {
        const textarea = screen.getByRole('textbox');
        expect(textarea).toHaveValue('answer');
      });

      test('cancel filling the answer should render no buttons', async () => {
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
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      initialAnswer: 'initial',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      render(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('should render textarea with initial answer', () => {
        const textarea = screen.getByRole('textbox');
        expect(textarea).toHaveValue(props.initialAnswer);
      });

      it('should render Clear button', () => {
        const button = screen.getByRole('button');
        expect(button).toHaveTextContent('Clear');
      });

      test("helptext should be 'Answered'", () => {
        expect(screen.getByText(/Answered\./i)).toBeInTheDocument();
      });
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const user = userEvent.setup();
        const textarea = screen.getByRole('textbox');
        await user.click(textarea);
        await user.clear(textarea);
        await user.type(textarea, 'answer');
      });

      test("textarea value should be 'answer'", () => {
        const textarea = screen.getByRole('textbox');
        expect(textarea).toHaveValue('answer');
      });

      test('cancel filling the answer should render Clear button', async () => {
        const user = userEvent.setup();
        const buttons = screen.getAllByRole('button');
        const cancelButton = buttons[buttons.length - 1];
        await user.click(cancelButton);
        const button = screen.getByRole('button');
        expect(button).toHaveTextContent('Clear');
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
      expect(window.confirm).toBeCalled();
    });
  });

  describe('disabled', () => {
    const props = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: true,
      initialAnswer: 'initial',
      onSubmit: vi.fn().mockReturnValue(undefined),
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      render(<ItemEssayForm {...props} />);
    });

    it('button disabled', () => {
      const button = screen.getByRole('button');
      expect(button).toBeDisabled();
    });
  });
});
