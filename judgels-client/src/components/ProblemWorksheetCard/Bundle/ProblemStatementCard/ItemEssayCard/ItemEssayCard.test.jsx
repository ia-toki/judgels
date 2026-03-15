import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemEssayCard } from './ItemEssayCard';

describe('ItemEssayCard', () => {
  const renderComponent = () => {
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <ItemEssayCard
            jid="jid"
            type={ItemType.Essay}
            meta="meta"
            config={{ statement: 'statement', score: 100 }}
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
