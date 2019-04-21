import { ReactWrapper, mount } from 'enzyme';
import { ItemEssayCard, ItemEssayCardProps } from './ItemEssayCard';
import { ItemType, ItemEssayConfig } from 'modules/api/sandalphon/problemBundle';
import * as React from 'react';

describe('ItemEssayCard', () => {
  let wrapper: ReactWrapper<ItemEssayCard>;
  const itemConfig: ItemEssayConfig = {
    statement: 'statement',
    score: 100,
  };
  const props: ItemEssayCardProps = {
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
