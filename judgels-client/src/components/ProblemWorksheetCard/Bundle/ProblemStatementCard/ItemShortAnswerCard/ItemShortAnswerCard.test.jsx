import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemShortAnswerCard } from './ItemShortAnswerCard';

describe('ItemShortAnswerCard', () => {
  const renderComponent = () => {
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <ItemShortAnswerCard
            jid="jid"
            type={ItemType.Essay}
            meta="meta"
            config={{
              statement: 'statement',
              score: 4,
              penalty: -2,
              inputValidationRegex: '/^d+$/',
              gradingRegex: '/^d+$/',
            }}
            disabled={false}
            onSubmit={vi.fn()}
            itemNumber={1}
          />
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  };

  test('renders item statement', () => {
    renderComponent();
    expect(screen.getByText((_, el) => el.textContent === '1.statement')).toBeInTheDocument();
  });
});
