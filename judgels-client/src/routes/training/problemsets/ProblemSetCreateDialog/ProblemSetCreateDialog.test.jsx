import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { parseDateTime } from '../../../../utils/datetime';
import { ProblemSetCreateDialog } from './ProblemSetCreateDialog';

describe('ProblemSetCreateDialog', () => {
  let onGetProblemSetConfig;
  let onCreateProblemSet;
  beforeEach(() => {
    onCreateProblemSet = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetProblemSetConfig,
      onCreateProblemSet,
    };
    render(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSetCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create dialog form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const slug = screen.getByRole('textbox', { name: /^slug/i });
    await user.type(slug, 'new-problemSet');

    const name = screen.getByRole('textbox', { name: /name/i });
    await user.type(name, 'New problemSet');

    const archiveSlug = screen.getByRole('textbox', { name: /archive slug/i });
    await user.type(archiveSlug, 'New archive');

    const description = screen.getByRole('textbox', { name: /description/i });
    await user.type(description, 'New description');

    const contestTime = document.querySelector('input[name="contestTime"]');
    await user.clear(contestTime);
    await user.type(contestTime, '2100-01-01 00:00');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateProblemSet).toHaveBeenCalledWith({
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
      contestTime: parseDateTime('2100-01-01 00:00').getTime(),
    });
  });
});
