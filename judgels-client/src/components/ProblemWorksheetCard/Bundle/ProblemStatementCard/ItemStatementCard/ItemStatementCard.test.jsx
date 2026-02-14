import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

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
    const store = createMockStore()({});
    render(
      <WebPrefsProvider>
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <ItemStatementCard {...props} />
          </Provider>
        </QueryClientProviderWrapper>
      </WebPrefsProvider>
    );
  });

  it('should render item statement', () => {
    expect(screen.getByText(props.config.statement)).toBeInTheDocument();
  });
});
