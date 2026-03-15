import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard';

describe('ItemMultipleChoiceCard', () => {
  const onChoiceChangeFn = vi.fn();

  const renderComponent = () => {
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <ItemMultipleChoiceCard
            jid="jid"
            type={ItemType.MultipleChoice}
            meta="meta"
            config={{
              statement: 'Statement',
              choices: [
                { alias: 'A', content: 'A' },
                { alias: 'B', content: 'B' },
                { alias: 'C', content: 'C' },
              ],
            }}
            disabled={false}
            onChoiceChange={onChoiceChangeFn}
            itemNumber={1}
          />
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  };

  test('answers the question by clicking a radio button', async () => {
    renderComponent();
    const user = userEvent.setup();
    const radioButtons = screen.getAllByRole('radio');
    await user.click(radioButtons[0]);
    expect(onChoiceChangeFn).toBeCalled();
  });

  test('answers the question and changes the answer', async () => {
    renderComponent();
    const user = userEvent.setup();
    const radioButtons = screen.getAllByRole('radio');
    await user.click(radioButtons[0]);
    await user.click(radioButtons[2]);
    expect(onChoiceChangeFn).toHaveBeenCalled();
  });
});
