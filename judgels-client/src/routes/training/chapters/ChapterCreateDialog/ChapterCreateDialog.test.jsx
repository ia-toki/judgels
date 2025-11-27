import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ChapterCreateDialog } from './ChapterCreateDialog';

describe('ChapterCreateDialog', () => {
  let onGetChapterConfig;
  let onCreateChapter;

  beforeEach(async () => {
    onCreateChapter = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetChapterConfig,
      onCreateChapter,
    };
    render(
      <Provider store={store}>
        <ChapterCreateDialog {...props} />
      </Provider>
    );
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
