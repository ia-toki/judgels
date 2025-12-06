import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ArchiveCreateDialog } from './ArchiveCreateDialog';

describe('ArchiveCreateDialog', () => {
  let onGetArchiveConfig;
  let onCreateArchive;

  beforeEach(() => {
    onCreateArchive = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetArchiveConfig,
      onCreateArchive,
    };
    render(
      <Provider store={store}>
        <ArchiveCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const slug = document.querySelector('input[name="slug"]');
    await user.type(slug, 'new-archive');

    const name = document.querySelector('input[name="name"]');
    await user.type(name, 'New archive');

    const category = document.querySelector('input[name="category"]');
    await user.type(category, 'New category');

    const description = document.querySelector('textarea[name="description"]');
    await user.type(description, 'New description');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateArchive).toHaveBeenCalledWith({
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
