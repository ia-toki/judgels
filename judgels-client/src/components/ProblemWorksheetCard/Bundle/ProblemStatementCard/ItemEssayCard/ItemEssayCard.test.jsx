import { mount } from 'enzyme';

import { ItemEssayCard } from './ItemEssayCard';
import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';

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
    wrapper = mount(<ItemEssayCard {...props} />);
  });

  it('should render item statement', () => {
    const text = wrapper.find('div').map(div => div.text());
    expect(text).toContain('1.statement');
  });
});
