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
    onSubmit: jest.fn().mockReturnValue(undefined),
    itemNumber: 1,
  };

  beforeEach(() => {
    wrapper = mount(<ItemEssayCard {...props} />);
  });

  it('item statement', () => {
    const statement = wrapper
      .find('div')
      .find('.__item-statement')
      .text();
    expect(statement).toEqual(props.config.statement);
  });

  it('item number', () => {
    const itemNumber = wrapper
      .find('div')
      .find('.__item-num')
      .text();
    expect(itemNumber).toEqual('1.');
  });
});
