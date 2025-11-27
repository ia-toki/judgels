import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { ItemShortAnswerCard } from './ItemShortAnswerCard';

describe('ItemShortAnswerCard', () => {
  const itemConfig = {
    statement: 'statement',
    score: 4,
    penalty: -2,
    inputValidationRegex: '/^d+$/',
    gradingRegex: '/^d+$/',
  };
  const props = {
    jid: 'jid',
    type: ItemType.Essay,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onSubmit: jest.fn(),
    itemNumber: 1,
  };

  beforeEach(() => {
    const store = createMockStore()({});
    render(
      <Provider store={store}>
        <ItemShortAnswerCard {...props} />
      </Provider>
    );
  });

  it('should render item statement', () => {
    expect(screen.getByText((_, el) => el.textContent === '1.statement')).toBeInTheDocument();
  });
});
