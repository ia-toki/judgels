import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { ItemShortAnswerCard } from './ItemShortAnswerCard';

describe('ItemShortAnswerCard', () => {
  let wrapper;
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
    wrapper = mount(
      <Provider store={store}>
        <ItemShortAnswerCard {...props} />
      </Provider>
    );
  });

  it('should render item statement', () => {
    const text = wrapper.find('div').map(div => div.text());
    expect(text).toContain('1.statement');
  });
});
