import { render, screen } from '@testing-library/react';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemStatementCard } from './ItemStatementCard';

describe('ItemStatementCard', () => {
  const renderComponent = () => {
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <ItemStatementCard
            jid="jid"
            type={ItemType.Statement}
            meta="meta"
            config={{ statement: 'statement' }}
            disabled={false}
          />
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  };

  test('renders item statement', () => {
    renderComponent();
    expect(screen.getByText('statement')).toBeInTheDocument();
  });
});
