import { ReactWrapper, mount } from 'enzyme';
import { ItemStatementCardProps, ItemStatementCard } from './ItemStatementCard';
import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import * as React from 'react';

describe('ItemStatementCard', () => {
  let wrapper: ReactWrapper<ItemStatementCardProps>;

  const props: ItemStatementCardProps = {
    jid: 'jid',
    type: ItemType.Statement,
    meta: 'meta',
    config: {
      statement: 'statement',
    },
    disabled: false,
  };

  beforeEach(() => {
    wrapper = mount(<ItemStatementCard {...props} />);
  });

  it('statement', () => {
    const statement = wrapper
      .find('div')
      .first()
      .text();
    expect(statement).toEqual(props.config.statement);
  });
});
