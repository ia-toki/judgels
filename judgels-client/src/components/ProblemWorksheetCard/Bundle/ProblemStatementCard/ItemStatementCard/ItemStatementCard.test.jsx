import { render, screen } from '@testing-library/react';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { WebPrefsProvider } from '../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { ItemStatementCard } from './ItemStatementCard';

describe('ItemStatementCard', () => {
  const props = {
    jid: 'jid',
    type: ItemType.Statement,
    meta: 'meta',
    config: {
      statement: 'statement',
    },
    disabled: false,
  };

  beforeEach(() => {
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <ItemStatementCard {...props} />
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  });

  it('should render item statement', () => {
    expect(screen.getByText(props.config.statement)).toBeInTheDocument();
  });
});
