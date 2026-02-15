import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestCreateDialog } from './ContestCreateDialog';

describe('ContestCreateDialog', () => {
  let onCreateContest;

  beforeEach(() => {
    onCreateContest = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      onCreateContest,
    };
    render(<ContestCreateDialog {...props} />);
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const slug = screen.getByRole('textbox');
    await user.type(slug, 'new-contest');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateContest).toHaveBeenCalledWith({ slug: 'new-contest' });
  });
});
