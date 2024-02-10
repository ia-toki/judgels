import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { ItemEssayCard } from './ItemEssayCard';

describe('ItemEssayCard', () => {
  let wrapper;
  const itemConfig = {
    statement: 'statement',
    score: 100,
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
        <ItemEssayCard {...props} />
      </Provider>
    );
  });

  it('should render item statement', () => {
    const text = wrapper.find('div').map(div => div.text());
    expect(text).toContain('1.statement');
  });
});
