import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ChapterCreateDialog } from './ChapterCreateDialog';

describe('ChapterCreateDialog', () => {
  let onGetChapterConfig;
  let onCreateChapter;

  beforeEach(async () => {
    onCreateChapter = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      onGetChapterConfig,
      onCreateChapter,
    };
    render(<ChapterCreateDialog {...props} />);
  });

  test('create dialog form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const name = screen.getByRole('textbox');
    await user.type(name, 'New Chapter');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateChapter).toHaveBeenCalledWith({ name: 'New Chapter' });
  });
});
