import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
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
      <Provider store={store}>
        <ItemStatementCard {...props} />
      </Provider>
    );
  });

  it('should render item statement', () => {
    expect(screen.getByText(props.config.statement)).toBeInTheDocument();
  });
});
