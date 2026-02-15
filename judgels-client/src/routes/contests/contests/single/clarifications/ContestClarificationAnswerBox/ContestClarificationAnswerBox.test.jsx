import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestClarificationAnswerBox } from './ContestClarificationAnswerBox';

describe('ContestClarificationAnswerBox', () => {
  let onAnswerClarification;

  beforeEach(() => {
    onAnswerClarification = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      contest: { jid: 'contestJid' },
      clarification: { jid: 'clarificationJid123' },
      isBoxOpen: true,
      isBoxLoading: false,
      onToggleBox: () => {
        return;
      },
      onAnswerClarification,
    };
    render(<ContestClarificationAnswerBox {...props} />);
  });

  test('form', async () => {
    const user = userEvent.setup();

    const answer = screen.getByRole('textbox');
    await user.type(answer, 'Yes.');

    const submitButton = screen.getByRole('button', { name: /answer/i });
    await user.click(submitButton);

    expect(onAnswerClarification).toHaveBeenCalledWith('contestJid', 'clarificationJid123', 'Yes.');
  });
});
