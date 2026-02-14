import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard';

describe('ItemMultipleChoiceCard', () => {
  const onChoiceChangeFn = vi.fn();
  const itemConfig = {
    statement: 'Statement',
    choices: [
      {
        alias: 'A',
        content: 'A',
      },
      {
        alias: 'B',
        content: 'B',
      },
      {
        alias: 'C',
        content: 'C',
      },
    ],
  };
  const multipleChoiceCardProps = {
    jid: 'jid',
    type: ItemType.MultipleChoice,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onChoiceChange: onChoiceChangeFn,
    itemNumber: 1,
  };

  beforeEach(() => {
    const store = createMockStore()({});
    const props = multipleChoiceCardProps;
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <ItemMultipleChoiceCard {...props} />
          </Provider>
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  });

  test('Answer the question by clicking a radio button', async () => {
    const user = userEvent.setup();
    const radioButtons = screen.getAllByRole('radio');
    await user.click(radioButtons[0]);
    expect(onChoiceChangeFn).toBeCalled();
  });

  test('Answer the question and change the answer', async () => {
    const user = userEvent.setup();
    const radioButtons = screen.getAllByRole('radio');
    await user.click(radioButtons[0]);
    await user.click(radioButtons[2]);
    expect(onChoiceChangeFn).toHaveBeenCalled();
  });
});
